package com.minimarket.service.impl;

import com.minimarket.entity.DetalleVenta;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Venta;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.repository.VentaRepository;
import com.minimarket.service.VentaService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;

    public VentaServiceImpl(VentaRepository ventaRepository,
                            ProductoRepository productoRepository) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
    }

    @Override
    public List<Venta> findAll() {
        return ventaRepository.findAll();
    }

    @Override
    public Venta findById(Long id) {
        return ventaRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Venta save(Venta venta) {
        validarVenta(venta);

        Map<Long, Producto> productos = new HashMap<>();
        Map<Long, Integer> cantidadesPorProducto = new HashMap<>();

        for (DetalleVenta detalle : venta.getDetalles()) {
            Long productoId = detalle.getProducto().getId();
            Producto producto = productoRepository.findById(productoId)
                    .orElseThrow(() -> new IllegalArgumentException("El producto asociado a la venta no existe."));
            productos.put(productoId, producto);
            cantidadesPorProducto.merge(productoId, detalle.getCantidad(), Integer::sum);
        }

        for (Map.Entry<Long, Integer> entrada : cantidadesPorProducto.entrySet()) {
            Producto producto = productos.get(entrada.getKey());
            int stockActual = producto.getStock() == null ? 0 : producto.getStock();
            if (entrada.getValue() > stockActual) {
                throw new IllegalArgumentException("Stock insuficiente para el producto: " + producto.getNombre());
            }
        }

        for (Map.Entry<Long, Integer> entrada : cantidadesPorProducto.entrySet()) {
            Producto producto = productos.get(entrada.getKey());
            producto.setStock(producto.getStock() - entrada.getValue());
            productoRepository.save(producto);
        }

        for (DetalleVenta detalle : venta.getDetalles()) {
            Producto producto = productos.get(detalle.getProducto().getId());
            detalle.setProducto(producto);
            detalle.setPrecio(producto.getPrecio());
            detalle.setVenta(venta);
        }

        return ventaRepository.save(venta);
    }

    private void validarVenta(Venta venta) {
        if (venta == null || venta.getUsuario() == null) {
            throw new IllegalArgumentException("La venta debe estar asociada a un usuario.");
        }
        if (venta.getDetalles() == null || venta.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("La venta debe incluir al menos un producto.");
        }

        for (DetalleVenta detalle : venta.getDetalles()) {
            if (detalle == null || detalle.getProducto() == null || detalle.getProducto().getId() == null) {
                throw new IllegalArgumentException("Cada detalle debe estar asociado a un producto existente.");
            }
            if (detalle.getCantidad() == null || detalle.getCantidad() <= 0) {
                throw new IllegalArgumentException("La cantidad vendida debe ser mayor que cero.");
            }
        }
    }

    @Override
    public List<Venta> findByUsuarioId(Long usuarioId) {
        return ventaRepository.findByUsuarioId(usuarioId);
    }
}
