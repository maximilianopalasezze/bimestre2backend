package com.minimarket.service.impl;

import com.minimarket.entity.DetalleVenta;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.entity.Venta;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.repository.UsuarioRepository;
import com.minimarket.repository.VentaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VentaServiceImplTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private VentaServiceImpl ventaService;

    @Test
    void debeRegistrarVentaCuandoUsuarioEsValidoYHayStockSuficiente() {
        Usuario usuario = crearUsuarioValido("VENDEDOR");
        Producto producto = crearProducto(1L, "Pan", 1000.0, 10);
        Venta venta = crearVenta(usuario, producto, 2);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(ventaRepository.save(any(Venta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Venta resultado = ventaService.registrarVenta(venta);

        assertNotNull(resultado);
        assertEquals(usuario, resultado.getUsuario());
        assertEquals(2000.0, resultado.getTotal(), 0.001);
        assertEquals(8, producto.getStock());
        assertNotNull(resultado.getFecha());

        verify(usuarioRepository, times(1)).findById(1L);
        verify(productoRepository, times(1)).findById(1L);
        verify(productoRepository, times(1)).save(producto);
        verify(ventaRepository, times(1)).save(venta);
    }

    @Test
    void debeLanzarErrorCuandoUsuarioNoExiste() {
        Usuario usuario = crearUsuarioValido("VENDEDOR");
        Producto producto = crearProducto(1L, "Pan", 1000.0, 10);
        Venta venta = crearVenta(usuario, producto, 2);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ventaService.registrarVenta(venta)
        );

        assertTrue(exception.getMessage().contains("usuario"));
        verify(ventaRepository, never()).save(any());
        verify(productoRepository, never()).save(any());
    }

    @Test
    void debeLanzarErrorCuandoUsuarioTieneDatosIncompletos() {
        Usuario usuario = crearUsuarioValido("VENDEDOR");
        usuario.setEmail("");

        Producto producto = crearProducto(1L, "Pan", 1000.0, 10);
        Venta venta = crearVenta(usuario, producto, 2);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ventaService.registrarVenta(venta)
        );

        assertTrue(exception.getMessage().contains("incompletos"));
        verify(ventaRepository, never()).save(any());
        verify(productoRepository, never()).save(any());
    }

    @Test
    void debeLanzarErrorCuandoUsuarioNoTienePermisoParaRegistrarVentas() {
        Usuario usuario = crearUsuarioValido("USER");
        Producto producto = crearProducto(1L, "Pan", 1000.0, 10);
        Venta venta = crearVenta(usuario, producto, 2);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ventaService.registrarVenta(venta)
        );

        assertTrue(exception.getMessage().contains("permisos"));
        verify(ventaRepository, never()).save(any());
        verify(productoRepository, never()).save(any());
    }

    @Test
    void debeLanzarErrorCuandoNoHayStockSuficiente() {
        Usuario usuario = crearUsuarioValido("VENDEDOR");
        Producto producto = crearProducto(1L, "Pan", 1000.0, 1);
        Venta venta = crearVenta(usuario, producto, 2);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ventaService.registrarVenta(venta)
        );

        assertTrue(exception.getMessage().contains("stock"));
        verify(ventaRepository, never()).save(any());
        verify(productoRepository, never()).save(any());
    }

    @Test
    void debeCalcularTotalCorrectamente() {
        Producto pan = crearProducto(1L, "Pan", 1000.0, 10);
        Producto leche = crearProducto(2L, "Leche", 1500.0, 10);

        DetalleVenta detallePan = crearDetalle(pan, 2);
        DetalleVenta detalleLeche = crearDetalle(leche, 3);

        Venta venta = new Venta();
        venta.setDetalles(List.of(detallePan, detalleLeche));

        Double total = ventaService.calcularTotal(venta);

        assertEquals(6500.0, total, 0.001);
    }

    @Test
    void debeRetornarCeroCuandoVentaNoTieneDetalles() {
        Venta venta = new Venta();

        Double total = ventaService.calcularTotal(venta);

        assertEquals(0.0, total, 0.001);
    }

    @Test
    void debeLanzarErrorCuandoCantidadEsInvalida() {
        Usuario usuario = crearUsuarioValido("VENDEDOR");
        Producto producto = crearProducto(1L, "Pan", 1000.0, 10);
        Venta venta = crearVenta(usuario, producto, 0);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ventaService.registrarVenta(venta)
        );

        assertTrue(exception.getMessage().contains("cantidad"));
        verify(ventaRepository, never()).save(any());
    }

    @Test
    void debeBuscarVentasPorUsuarioId() {
        Venta venta = new Venta();

        when(ventaRepository.findByUsuarioId(1L)).thenReturn(List.of(venta));

        List<Venta> resultado = ventaService.findByUsuarioId(1L);

        assertEquals(1, resultado.size());
        verify(ventaRepository, times(1)).findByUsuarioId(1L);
    }

    private Usuario crearUsuarioValido(String nombreRol) {
        Rol rol = new Rol(nombreRol);

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Juan");
        usuario.setApellido("Pérez");
        usuario.setEmail("juan@test.cl");
        usuario.setDireccion("Av. Siempre Viva 123");
        usuario.setUsername("juanperez");
        usuario.setPassword("123456");
        usuario.setRoles(Set.of(rol));

        return usuario;
    }

    private Producto crearProducto(Long id, String nombre, Double precio, Integer stock) {
        Producto producto = new Producto();
        producto.setId(id);
        producto.setNombre(nombre);
        producto.setPrecio(precio);
        producto.setStock(stock);
        return producto;
    }

    private Venta crearVenta(Usuario usuario, Producto producto, Integer cantidad) {
        Venta venta = new Venta();
        venta.setUsuario(usuario);
        venta.setDetalles(List.of(crearDetalle(producto, cantidad)));
        return venta;
    }

    private DetalleVenta crearDetalle(Producto producto, Integer cantidad) {
        DetalleVenta detalle = new DetalleVenta();
        detalle.setProducto(producto);
        detalle.setCantidad(cantidad);
        return detalle;
    }
}