package com.minimarket.service.impl;

import com.minimarket.entity.DetalleVenta;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Usuario;
import com.minimarket.entity.Venta;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.repository.VentaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VentaServiceImplTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private VentaServiceImpl ventaService;

    @Test
    void registraVentaDescuentaStockYConservaElPrecioVigente() {
        Producto producto = producto("Pan", 5, 1800.0);
        DetalleVenta detalle = detalle(producto, 2);
        Venta venta = ventaCon(detalle);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(ventaRepository.save(any(Venta.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        Venta resultado = ventaService.save(venta);

        assertSame(venta, resultado);
        assertEquals(3, producto.getStock());
        assertEquals(1800.0, detalle.getPrecio());
        assertSame(venta, detalle.getVenta());

        verify(productoRepository).save(producto);
        verify(ventaRepository).save(venta);
    }

    @Test
    void rechazaVentaCuandoLaCantidadTotalSolicitadaSuperaElStock() {
        Producto producto = producto("Jugo", 3, 1500.0);
        Venta venta = ventaCon(
                detalle(producto, 2),
                detalle(producto, 2)
        );

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ventaService.save(venta)
        );

        assertEquals(
                "Stock insuficiente para el producto: Jugo",
                exception.getMessage()
        );

        assertEquals(3, producto.getStock());

        verify(productoRepository, times(2)).findById(1L);
        verifyNoMoreInteractions(productoRepository);
        verifyNoInteractions(ventaRepository);
    }

    @Test
    void rechazaVentaSinDetalles() {
        Venta venta = ventaCon();

        assertThrows(
                IllegalArgumentException.class,
                () -> ventaService.save(venta)
        );

        verifyNoInteractions(productoRepository, ventaRepository);
    }

    private Producto producto(String nombre, int stock, double precio) {
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre(nombre);
        producto.setStock(stock);
        producto.setPrecio(precio);
        return producto;
    }

    private DetalleVenta detalle(Producto producto, int cantidad) {
        DetalleVenta detalle = new DetalleVenta();
        detalle.setProducto(producto);
        detalle.setCantidad(cantidad);
        detalle.setPrecio(0.0);
        return detalle;
    }

    private Venta ventaCon(DetalleVenta... detalles) {
        Usuario cajero = new Usuario();
        cajero.setId(1L);
        cajero.setUsername("cajero1");

        Venta venta = new Venta();
        venta.setUsuario(cajero);
        venta.setFecha(new Date());
        venta.setDetalles(List.of(detalles));

        return venta;
    }
}