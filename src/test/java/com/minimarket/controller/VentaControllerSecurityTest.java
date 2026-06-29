package com.minimarket.controller;

import com.minimarket.entity.Venta;
import com.minimarket.security.config.SecurityConfig;
import com.minimarket.security.service.CustomUserDetailsService;
import com.minimarket.service.VentaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = VentaController.class)
@Import(SecurityConfig.class)
class VentaControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VentaService ventaService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser(roles = "CAJERO")
    void cajeroPuedeGenerarVenta() throws Exception {
        Venta venta = new Venta();
        venta.setId(1L);
        when(ventaService.save(any(Venta.class))).thenReturn(venta);

        mockMvc.perform(post("/api/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "usuario": {"id": 1},
                                  "fecha": "2026-06-29T00:00:00.000+00:00",
                                  "detalles": [{
                                    "producto": {"id": 1},
                                    "cantidad": 2,
                                    "precio": 1500.0
                                  }]
                                }
                                """))
                .andExpect(status().isCreated());

        verify(ventaService).save(any(Venta.class));
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void clienteNoPuedeGenerarVenta() throws Exception {
        mockMvc.perform(post("/api/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"detalles\":[]}"))
                .andExpect(status().isForbidden());

        verifyNoInteractions(ventaService);
    }

    @Test
    void usuarioNoAutenticadoNoPuedeGenerarVenta() throws Exception {
        mockMvc.perform(post("/api/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"detalles\":[]}"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(ventaService);
    }
}
