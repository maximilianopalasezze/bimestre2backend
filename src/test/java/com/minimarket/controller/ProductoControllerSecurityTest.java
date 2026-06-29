package com.minimarket.controller;

import com.minimarket.entity.Categoria;
import com.minimarket.entity.Producto;
import com.minimarket.security.config.SecurityConfig;
import com.minimarket.security.service.CustomUserDetailsService;
import com.minimarket.service.CategoriaService;
import com.minimarket.service.ProductoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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

    @MockitoBean
    private ProductoService productoService;

    @MockitoBean
    private CategoriaService categoriaService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void administradorPuedeModificarProducto() throws Exception {
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Alimentos");

        Producto existente = new Producto();
        existente.setId(1L);
        existente.setNombre("Arroz");
        existente.setPrecio(2000.0);
        existente.setStock(15);
        existente.setCategoria(categoria);

        Producto actualizado = new Producto();
        actualizado.setId(1L);
        actualizado.setNombre("Arroz integral");
        actualizado.setPrecio(2500.0);
        actualizado.setStock(25);
        actualizado.setCategoria(categoria);

        when(productoService.findById(1L)).thenReturn(existente);
        when(categoriaService.findById(1L)).thenReturn(categoria);
        when(productoService.save(any(Producto.class))).thenReturn(actualizado);

        mockMvc.perform(put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Arroz integral",
                                  "precio": 2500.0,
                                  "stock": 25,
                                  "categoriaId": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Arroz integral"))
                .andExpect(jsonPath("$.categoriaId").value(1))
                .andExpect(jsonPath("$.categoriaNombre").value("Alimentos"));

        verify(productoService).save(any(Producto.class));
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void clienteNoPuedeModificarProducto() throws Exception {
        mockMvc.perform(put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Producto no autorizado",
                                  "precio": 1500.0,
                                  "stock": 5,
                                  "categoriaId": 1
                                }
                                """))
                .andExpect(status().isForbidden());

        verifyNoInteractions(productoService, categoriaService);
    }

    @Test
    void usuarioNoAutenticadoNoPuedeModificarProducto() throws Exception {
        mockMvc.perform(put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Producto",
                                  "precio": 1500.0,
                                  "stock": 5,
                                  "categoriaId": 1
                                }
                                """))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(productoService, categoriaService);
    }
}


