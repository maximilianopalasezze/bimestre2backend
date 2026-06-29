package com.minimarket.controller;

import com.minimarket.entity.Producto;
import com.minimarket.security.config.SecurityConfig;
import com.minimarket.security.service.CustomUserDetailsService;
import com.minimarket.service.ProductoService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProductoController.class)
@Import(SecurityConfig.class)
class ProductoControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductoService productoService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void administradorPuedeModificarProducto() throws Exception {
        Producto existente = new Producto();
        existente.setId(1L);
        Producto actualizado = new Producto();
        actualizado.setId(1L);
        actualizado.setNombre("Arroz integral");
        actualizado.setPrecio(2500.0);
        actualizado.setStock(25);

        when(productoService.findById(1L)).thenReturn(existente);
        when(productoService.save(any(Producto.class))).thenReturn(actualizado);

        mockMvc.perform(put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Arroz integral\",\"precio\":2500.0,\"stock\":25}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Arroz integral"));

        verify(productoService).save(any(Producto.class));
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void clienteNoPuedeModificarProducto() throws Exception {
        mockMvc.perform(put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Producto no autorizado\",\"precio\":1500.0,\"stock\":5}"))
                .andExpect(status().isForbidden());

        verifyNoInteractions(productoService);
    }

    @Test
    void usuarioNoAutenticadoNoPuedeModificarProducto() throws Exception {
        mockMvc.perform(put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Producto\",\"precio\":1500.0,\"stock\":5}"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(productoService);
    }
}
