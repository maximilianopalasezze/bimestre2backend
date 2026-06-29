package com.minimarket.service.impl;

import com.minimarket.entity.Inventario;
import com.minimarket.entity.Producto;
import com.minimarket.repository.InventarioRepository;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.service.InventarioService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class InventarioServiceImpl implements InventarioService {

    private final InventarioRepository inventarioRepository;
    private final ProductoRepository productoRepository;

    public InventarioServiceImpl(InventarioRepository inventarioRepository,
                                 ProductoRepository productoRepository) {
        this.inventarioRepository = inventarioRepository;
        this.productoRepository = productoRepository;
    }

    @Override
    public List<Inventario> findAll() {
        return inventarioRepository.findAll();
    }

    @Override
    public Inventario findById(Long id) {
        return inventarioRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Inventario save(Inventario inventario) {
        validarMovimiento(inventario);

        Long productoId = inventario.getProducto().getId();
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("El producto asociado al movimiento no existe."));

        int stockActual = producto.getStock() == null ? 0 : producto.getStock();
        String tipoMovimiento = inventario.getTipoMovimiento().trim().toUpperCase(Locale.ROOT);

        if ("SALIDA".equals(tipoMovimiento) && inventario.getCantidad() > stockActual) {
            throw new IllegalArgumentException("No hay stock suficiente para registrar la salida solicitada.");
        }

        int nuevoStock = "ENTRADA".equals(tipoMovimiento)
                ? stockActual + inventario.getCantidad()
                : stockActual - inventario.getCantidad();

        producto.setStock(nuevoStock);
        inventario.setProducto(producto);
        inventario.setTipoMovimiento(tipoMovimiento);
        productoRepository.save(producto);
        return inventarioRepository.save(inventario);
    }

    private void validarMovimiento(Inventario inventario) {
        if (inventario == null || inventario.getProducto() == null || inventario.getProducto().getId() == null) {
            throw new IllegalArgumentException("El movimiento debe estar asociado a un producto existente.");
        }
        if (inventario.getCantidad() == null || inventario.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad del movimiento debe ser mayor que cero.");
        }
        if (inventario.getTipoMovimiento() == null) {
            throw new IllegalArgumentException("El tipo de movimiento es obligatorio.");
        }

        String tipoMovimiento = inventario.getTipoMovimiento().trim().toUpperCase(Locale.ROOT);
        if (!"ENTRADA".equals(tipoMovimiento) && !"SALIDA".equals(tipoMovimiento)) {
            throw new IllegalArgumentException("El tipo de movimiento debe ser ENTRADA o SALIDA.");
        }
    }

    @Override
    public void deleteById(Long id) {
        inventarioRepository.deleteById(id);
    }

    @Override
    public List<Inventario> findByProductoId(Long productoId) {
        return inventarioRepository.findByProductoId(productoId);
    }
}
