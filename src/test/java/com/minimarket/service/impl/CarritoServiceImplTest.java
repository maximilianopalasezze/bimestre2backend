package com.minimarket.service.impl;

import com.minimarket.entity.Carrito;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.CarritoRepository;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CarritoServiceImplTest {

    @Mock
    private CarritoRepository carritoRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CarritoServiceImpl carritoService;

    @Test
    void deberiaAgregarProductoCuandoHayStockSuficiente() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        Producto producto = new Producto();
        producto.setId(10L);
        producto.setStock(15);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(productoRepository.findById(10L)).thenReturn(Optional.of(producto));
        when(carritoRepository.save(any(Carrito.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Carrito resultado = carritoService.agregarProducto(1L, 10L, 3);

        assertNotNull(resultado);
        assertEquals(usuario, resultado.getUsuario());
        assertEquals(producto, resultado.getProducto());
        assertEquals(3, resultado.getCantidad());
        assertEquals(12, producto.getStock());

        verify(productoRepository).save(producto);
        verify(carritoRepository).save(any(Carrito.class));
    }

    @Test
    void deberiaLanzarExcepcionCuandoNoHayStockSuficiente() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        Producto producto = new Producto();
        producto.setId(10L);
        producto.setStock(2);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(productoRepository.findById(10L)).thenReturn(Optional.of(producto));

        assertThrows(IllegalStateException.class,
                () -> carritoService.agregarProducto(1L, 10L, 5));

        assertEquals(2, producto.getStock());
        verify(productoRepository, never()).save(any(Producto.class));
        verify(carritoRepository, never()).save(any(Carrito.class));
    }

    @Test
    void deberiaLanzarExcepcionCuandoCantidadEsCero() {
        assertThrows(IllegalArgumentException.class,
                () -> carritoService.agregarProducto(1L, 10L, 0));

        verifyNoInteractions(usuarioRepository, productoRepository, carritoRepository);
    }

    @Test
    void deberiaLanzarExcepcionCuandoUsuarioNoExiste() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> carritoService.agregarProducto(1L, 10L, 2));

        verify(productoRepository, never()).findById(anyLong());
        verify(carritoRepository, never()).save(any(Carrito.class));
    }

    @Test
    void deberiaLanzarExcepcionCuandoProductoNoExiste() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(productoRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> carritoService.agregarProducto(1L, 10L, 2));

        verify(carritoRepository, never()).save(any(Carrito.class));
    }

    @Test
    void deberiaObtenerTodosLosCarritos() {
        Carrito carrito = new Carrito();

        when(carritoRepository.findAll()).thenReturn(List.of(carrito));

        List<Carrito> resultado = carritoService.findAll();

        assertEquals(1, resultado.size());
        verify(carritoRepository).findAll();
    }

    @Test
    void deberiaBuscarCarritoPorId() {
        Carrito carrito = new Carrito();
        carrito.setId(1L);

        when(carritoRepository.findById(1L)).thenReturn(Optional.of(carrito));

        Carrito resultado = carritoService.findById(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(carritoRepository).findById(1L);
    }

    @Test
    void deberiaRetornarNullCuandoNoExisteCarritoPorId() {
        when(carritoRepository.findById(99L)).thenReturn(Optional.empty());

        Carrito resultado = carritoService.findById(99L);

        assertNull(resultado);
        verify(carritoRepository).findById(99L);
    }

    @Test
    void deberiaGuardarCarrito() {
        Carrito carrito = new Carrito();

        when(carritoRepository.save(carrito)).thenReturn(carrito);

        Carrito resultado = carritoService.save(carrito);

        assertEquals(carrito, resultado);
        verify(carritoRepository).save(carrito);
    }

    @Test
    void deberiaEliminarCarritoPorId() {
        carritoService.deleteById(1L);

        verify(carritoRepository).deleteById(1L);
    }

    @Test
    void deberiaBuscarCarritosPorUsuario() {
        Carrito carrito = new Carrito();

        when(carritoRepository.findByUsuarioId(1L)).thenReturn(List.of(carrito));

        List<Carrito> resultado = carritoService.findByUsuarioId(1L);

        assertEquals(1, resultado.size());
        verify(carritoRepository).findByUsuarioId(1L);
    }
}