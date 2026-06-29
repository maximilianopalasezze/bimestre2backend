package com.minimarket.service.impl;

import com.minimarket.entity.Producto;
import com.minimarket.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoServiceImplTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoServiceImpl productoService;

    @Test
    void listarProductosRetornaLosProductosDelRepositorio() {
        Producto arroz = producto(1L, "Arroz", 1800.0, 12);
        Producto leche = producto(2L, "Leche", 1200.0, 8);

        when(productoRepository.findAll()).thenReturn(List.of(arroz, leche));

        List<Producto> resultado = productoService.findAll();

        assertEquals(2, resultado.size());
        assertEquals("Arroz", resultado.get(0).getNombre());
        assertEquals("Leche", resultado.get(1).getNombre());

        verify(productoRepository).findAll();
    }

    @Test
    void buscarProductoPorIdRetornaProductoCuandoExiste() {
        Producto producto = producto(1L, "Aceite", 3500.0, 6);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        Producto resultado = productoService.findById(1L);

        assertEquals(producto, resultado);
        assertEquals("Aceite", resultado.getNombre());

        verify(productoRepository).findById(1L);
    }

    @Test
    void buscarProductoPorIdRetornaNullCuandoNoExiste() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        Producto resultado = productoService.findById(99L);

        assertNull(resultado);

        verify(productoRepository).findById(99L);
    }

    @Test
    void guardarProductoPersisteYRetornaElProducto() {
        Producto producto = producto(null, "Fideos", 1500.0, 20);
        Producto productoGuardado = producto(1L, "Fideos", 1500.0, 20);

        when(productoRepository.save(producto)).thenReturn(productoGuardado);

        Producto resultado = productoService.save(producto);

        assertEquals(1L, resultado.getId());
        assertEquals("Fideos", resultado.getNombre());
        assertEquals(20, resultado.getStock());

        verify(productoRepository).save(producto);
    }

    @Test
    void eliminarProductoPorIdInvocaElRepositorio() {
        productoService.deleteById(1L);

        verify(productoRepository).deleteById(1L);
    }

    @Test
    void buscarPorCategoriaRetornaProductosAsociados() {
        Producto producto = producto(1L, "Galletas", 1000.0, 15);

        when(productoRepository.findByCategoriaId(3L)).thenReturn(List.of(producto));

        List<Producto> resultado = productoService.findByCategoriaId(3L);

        assertEquals(1, resultado.size());
        assertEquals("Galletas", resultado.get(0).getNombre());

        verify(productoRepository).findByCategoriaId(3L);
    }

    private Producto producto(Long id, String nombre, double precio, int stock) {
        Producto producto = new Producto();
        producto.setId(id);
        producto.setNombre(nombre);
        producto.setPrecio(precio);
        producto.setStock(stock);
        return producto;
    }
}