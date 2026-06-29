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

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventarioServiceImplTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private InventarioServiceImpl inventarioService;

    @Test
    void registrarEntradaAumentaElStockDelProducto() {
        Producto producto = productoConStock(10);
        Inventario entrada = movimiento(producto, 5, "entrada");

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(inventarioRepository.save(any(Inventario.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        Inventario resultado = inventarioService.save(entrada);

        assertEquals(15, producto.getStock());
        assertEquals("ENTRADA", resultado.getTipoMovimiento());

        verify(productoRepository).save(producto);
        verify(inventarioRepository).save(entrada);
    }

    @Test
    void registrarSalidaConStockDisponibleReduceElStock() {
        Producto producto = productoConStock(10);
        Inventario salida = movimiento(producto, 4, "SALIDA");

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(inventarioRepository.save(any(Inventario.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        inventarioService.save(salida);

        assertEquals(6, producto.getStock());

        verify(productoRepository).save(producto);
        verify(inventarioRepository).save(salida);
    }

    @Test
    void rechazaSalidaCuandoNoExisteStockSuficiente() {
        Producto producto = productoConStock(2);
        Inventario salida = movimiento(producto, 3, "SALIDA");

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> inventarioService.save(salida)
        );

        assertEquals(
                "No hay stock suficiente para registrar la salida solicitada.",
                exception.getMessage()
        );

        verify(productoRepository).findById(1L);
        verifyNoMoreInteractions(productoRepository);
        verifyNoInteractions(inventarioRepository);
    }

    @Test
    void rechazaTipoDeMovimientoInvalido() {
        Producto producto = productoConStock(10);
        Inventario ajuste = movimiento(producto, 2, "AJUSTE");

        assertThrows(
                IllegalArgumentException.class,
                () -> inventarioService.save(ajuste)
        );

        verifyNoInteractions(productoRepository, inventarioRepository);
    }

    private Producto productoConStock(int stock) {
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Leche");
        producto.setStock(stock);
        producto.setPrecio(1200.0);
        return producto;
    }

    private Inventario movimiento(Producto producto, int cantidad, String tipo) {
        Inventario inventario = new Inventario();
        inventario.setProducto(producto);
        inventario.setCantidad(cantidad);
        inventario.setTipoMovimiento(tipo);
        inventario.setFechaMovimiento(new Date());
        return inventario;
    }
}