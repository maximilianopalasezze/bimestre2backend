package com.minimarket.service.impl;

import com.minimarket.entity.DetalleVenta;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.entity.Venta;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.repository.UsuarioRepository;
import com.minimarket.repository.VentaRepository;
import com.minimarket.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class VentaServiceImpl implements VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public List<Venta> findAll() {
        return ventaRepository.findAll();
    }

    @Override
    public Venta findById(Long id) {
        return ventaRepository.findById(id).orElse(null);
    }

    @Override
    public Venta save(Venta venta) {
        return registrarVenta(venta);
    }

    @Override
    public List<Venta> findByUsuarioId(Long usuarioId) {
        return ventaRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public Venta registrarVenta(Venta venta) {
        validarVentaBasica(venta);

        Usuario usuario = usuarioRepository.findById(venta.getUsuario().getId())
                .orElseThrow(() -> new IllegalArgumentException("El usuario asociado a la venta no existe."));

        if (!usuarioTieneDatosCompletos(usuario)) {
            throw new IllegalArgumentException("El usuario asociado a la venta tiene datos incompletos.");
        }

        if (!usuarioPuedeRegistrarVentas(usuario)) {
            throw new IllegalArgumentException("El usuario no tiene permisos para registrar ventas.");
        }

        double total = 0.0;

        for (DetalleVenta detalle : venta.getDetalles()) {
            validarDetalle(detalle);

            Producto producto = productoRepository.findById(detalle.getProducto().getId())
                    .orElseThrow(() -> new IllegalArgumentException("El producto no existe."));

            if (producto.getStock() < detalle.getCantidad()) {
                throw new IllegalArgumentException("No hay stock suficiente para el producto: " + producto.getNombre());
            }

            detalle.setVenta(venta);
            detalle.setProducto(producto);
            detalle.setPrecio(producto.getPrecio());

            producto.setStock(producto.getStock() - detalle.getCantidad());
            productoRepository.save(producto);

            total += producto.getPrecio() * detalle.getCantidad();
        }

        venta.setUsuario(usuario);

        if (venta.getFecha() == null) {
            venta.setFecha(new Date());
        }

        venta.setTotal(total);

        return ventaRepository.save(venta);
    }

    @Override
    public Double calcularTotal(Venta venta) {
        if (venta == null || venta.getDetalles() == null || venta.getDetalles().isEmpty()) {
            return 0.0;
        }

        return venta.getDetalles().stream()
                .mapToDouble(detalle -> {
                    if (detalle.getProducto() == null
                            || detalle.getProducto().getPrecio() == null
                            || detalle.getCantidad() == null) {
                        throw new IllegalArgumentException("El detalle de venta tiene datos incompletos.");
                    }

                    return detalle.getProducto().getPrecio() * detalle.getCantidad();
                })
                .sum();
    }

    private void validarVentaBasica(Venta venta) {
        if (venta == null) {
            throw new IllegalArgumentException("La venta no puede ser nula.");
        }

        if (venta.getUsuario() == null || venta.getUsuario().getId() == null) {
            throw new IllegalArgumentException("La venta debe estar asociada a un usuario valido.");
        }

        if (venta.getDetalles() == null || venta.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("La venta debe tener al menos un detalle.");
        }
    }

    private void validarDetalle(DetalleVenta detalle) {
        if (detalle == null) {
            throw new IllegalArgumentException("El detalle de venta no puede ser nulo.");
        }

        if (detalle.getProducto() == null || detalle.getProducto().getId() == null) {
            throw new IllegalArgumentException("El detalle debe tener un producto valido.");
        }

        if (detalle.getCantidad() == null || detalle.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad vendida debe ser mayor a cero.");
        }
    }

    private boolean usuarioTieneDatosCompletos(Usuario usuario) {
        return usuario != null
                && textoValido(usuario.getNombre())
                && textoValido(usuario.getApellido())
                && textoValido(usuario.getEmail())
                && textoValido(usuario.getDireccion());
    }

    private boolean usuarioPuedeRegistrarVentas(Usuario usuario) {
        if (usuario == null || usuario.getRoles() == null || usuario.getRoles().isEmpty()) {
            return false;
        }

        return usuario.getRoles().stream()
                .map(Rol::getNombre)
                .anyMatch(rol -> rol.equalsIgnoreCase("ADMIN")
                        || rol.equalsIgnoreCase("VENDEDOR"));
    }

    private boolean textoValido(String texto) {
        return texto != null && !texto.trim().isEmpty();
    }
}