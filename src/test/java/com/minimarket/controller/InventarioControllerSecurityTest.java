package com.minimarket.controller;

import com.minimarket.entity.Inventario;
import com.minimarket.security.config.SecurityConfig;
import com.minimarket.security.service.CustomUserDetailsService;
import com.minimarket.service.InventarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = InventarioController.class)
@Import(SecurityConfig.class)
class InventarioControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InventarioService inventarioService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void administradorPuedeRegistrarMovimientoInventario() throws Exception {
        Inventario movimiento = new Inventario();
        movimiento.setId(1L);
        movimiento.setCantidad(10);
        movimiento.setTipoMovimiento("ENTRADA");
        movimiento.setFechaMovimiento(new Date());
        when(inventarioService.save(any(Inventario.class))).thenReturn(movimiento);

        mockMvc.perform(post("/api/inventario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "producto": {"id": 1},
                                  "cantidad": 10,
                                  "tipoMovimiento": "ENTRADA",
                                  "fechaMovimiento": "2026-06-29T00:00:00.000+00:00"
                                }
                                """))
                .andExpect(status().isCreated());

        verify(inventarioService).save(any(Inventario.class));
    }

    @Test
    @WithMockUser(roles = "CAJERO")
    void cajeroNoPuedeRegistrarMovimientoInventario() throws Exception {
        mockMvc.perform(post("/api/inventario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cantidad\":10,\"tipoMovimiento\":\"ENTRADA\"}"))
                .andExpect(status().isForbidden());

        verifyNoInteractions(inventarioService);
    }

    @Test
    void usuarioNoAutenticadoNoPuedeRegistrarMovimientoInventario() throws Exception {
        mockMvc.perform(post("/api/inventario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cantidad\":10,\"tipoMovimiento\":\"ENTRADA\"}"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(inventarioService);
    }
}
