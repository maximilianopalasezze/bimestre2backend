package com.minimarket.service.impl;

import com.minimarket.entity.Inventario;
import com.minimarket.entity.Producto;
import com.minimarket.repository.InventarioRepository;
import com.minimarket.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventarioServiceImplTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private InventarioServiceImpl inventarioService;

    @Test
    void deberiaRegistrarEntradaYAumentarStock() {
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setStock(10);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(inventarioRepository.save(any(Inventario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Inventario resultado = inventarioService.registrarMovimiento(1L, 5, "Entrada");

        assertNotNull(resultado);
        assertEquals(producto, resultado.getProducto());
        assertEquals(5, resultado.getCantidad());
        assertEquals("Entrada", resultado.getTipoMovimiento());
        assertNotNull(resultado.getFechaMovimiento());
        assertEquals(15, producto.getStock());

        verify(productoRepository).save(producto);
        verify(inventarioRepository).save(any(Inventario.class));
    }

    @Test
    void deberiaRegistrarSalidaYDisminuirStock() {
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setStock(10);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(inventarioRepository.save(any(Inventario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Inventario resultado = inventarioService.registrarMovimiento(1L, 4, "Salida");

        assertNotNull(resultado);
        assertEquals("Salida", resultado.getTipoMovimiento());
        assertEquals(6, producto.getStock());

        verify(productoRepository).save(producto);
        verify(inventarioRepository).save(any(Inventario.class));
    }

    @Test
    void deberiaLanzarExcepcionCuandoSalidaSuperaStockDisponible() {
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setStock(2);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        assertThrows(IllegalStateException.class,
                () -> inventarioService.registrarMovimiento(1L, 5, "Salida"));

        assertEquals(2, producto.getStock());
        verify(productoRepository, never()).save(any(Producto.class));
        verify(inventarioRepository, never()).save(any(Inventario.class));
    }

    @Test
    void deberiaLanzarExcepcionCuandoProductoIdEsNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> inventarioService.registrarMovimiento(null, 5, "Entrada"));

        verifyNoInteractions(productoRepository, inventarioRepository);
    }

    @Test
    void deberiaLanzarExcepcionCuandoCantidadEsCero() {
        assertThrows(IllegalArgumentException.class,
                () -> inventarioService.registrarMovimiento(1L, 0, "Entrada"));

        verifyNoInteractions(productoRepository, inventarioRepository);
    }

    @Test
    void deberiaLanzarExcepcionCuandoCantidadEsNegativa() {
        assertThrows(IllegalArgumentException.class,
                () -> inventarioService.registrarMovimiento(1L, -2, "Entrada"));

        verifyNoInteractions(productoRepository, inventarioRepository);
    }

    @Test
    void deberiaLanzarExcepcionCuandoTipoMovimientoEsNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> inventarioService.registrarMovimiento(1L, 3, null));

        verifyNoInteractions(productoRepository, inventarioRepository);
    }

    @Test
    void deberiaLanzarExcepcionCuandoTipoMovimientoEstaVacio() {
        assertThrows(IllegalArgumentException.class,
                () -> inventarioService.registrarMovimiento(1L, 3, "   "));

        verifyNoInteractions(productoRepository, inventarioRepository);
    }

    @Test
    void deberiaLanzarExcepcionCuandoTipoMovimientoEsInvalido() {
        assertThrows(IllegalArgumentException.class,
                () -> inventarioService.registrarMovimiento(1L, 3, "Transferencia"));

        verifyNoInteractions(productoRepository, inventarioRepository);
    }

    @Test
    void deberiaLanzarExcepcionCuandoProductoNoExiste() {
        when(productoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> inventarioService.registrarMovimiento(1L, 3, "Entrada"));

        verify(inventarioRepository, never()).save(any(Inventario.class));
    }

    @Test
    void deberiaLanzarExcepcionCuandoProductoNoTieneStockDefinido() {
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setStock(null);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        assertThrows(IllegalStateException.class,
                () -> inventarioService.registrarMovimiento(1L, 3, "Entrada"));

        verify(productoRepository, never()).save(any(Producto.class));
        verify(inventarioRepository, never()).save(any(Inventario.class));
    }

    @Test
    void deberiaObtenerTodosLosMovimientosInventario() {
        Inventario inventario = new Inventario();

        when(inventarioRepository.findAll()).thenReturn(List.of(inventario));

        List<Inventario> resultado = inventarioService.findAll();

        assertEquals(1, resultado.size());
        verify(inventarioRepository).findAll();
    }

    @Test
    void deberiaBuscarInventarioPorId() {
        Inventario inventario = new Inventario();
        inventario.setId(1L);

        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inventario));

        Inventario resultado = inventarioService.findById(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(inventarioRepository).findById(1L);
    }

    @Test
    void deberiaRetornarNullCuandoNoExisteInventarioPorId() {
        when(inventarioRepository.findById(99L)).thenReturn(Optional.empty());

        Inventario resultado = inventarioService.findById(99L);

        assertNull(resultado);
        verify(inventarioRepository).findById(99L);
    }

    @Test
    void deberiaGuardarInventario() {
        Inventario inventario = new Inventario();

        when(inventarioRepository.save(inventario)).thenReturn(inventario);

        Inventario resultado = inventarioService.save(inventario);

        assertEquals(inventario, resultado);
        verify(inventarioRepository).save(inventario);
    }

    @Test
    void deberiaEliminarInventarioPorId() {
        inventarioService.deleteById(1L);

        verify(inventarioRepository).deleteById(1L);
    }

    @Test
    void deberiaBuscarInventariosPorProducto() {
        Inventario inventario = new Inventario();

        when(inventarioRepository.findByProductoId(1L)).thenReturn(List.of(inventario));

        List<Inventario> resultado = inventarioService.findByProductoId(1L);

        assertEquals(1, resultado.size());
        verify(inventarioRepository).findByProductoId(1L);
    }
}