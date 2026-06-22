package com.minimarket.service.impl;

import com.minimarket.entity.Inventario;
import com.minimarket.entity.Producto;
import com.minimarket.repository.InventarioRepository;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.service.InventarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

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
    public Inventario save(Inventario inventario) {
        return inventarioRepository.save(inventario);
    }

    @Override
    public void deleteById(Long id) {
        inventarioRepository.deleteById(id);
    }

    @Override
    public List<Inventario> findByProductoId(Long productoId) {
        return inventarioRepository.findByProductoId(productoId);
    }

    @Override
    @Transactional
    public Inventario registrarMovimiento(Long productoId,
                                          Integer cantidad,
                                          String tipoMovimiento) {

        if (productoId == null) {
            throw new IllegalArgumentException("El producto es obligatorio.");
        }

        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }

        if (tipoMovimiento == null || tipoMovimiento.trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de movimiento es obligatorio.");
        }

        if (!tipoMovimiento.equalsIgnoreCase("Entrada")
                && !tipoMovimiento.equalsIgnoreCase("Salida")) {
            throw new IllegalArgumentException(
                    "El tipo de movimiento debe ser Entrada o Salida."
            );
        }

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Producto no encontrado.")
                );

        if (producto.getStock() == null) {
            throw new IllegalStateException("El producto no tiene stock definido.");
        }

        if (tipoMovimiento.equalsIgnoreCase("Entrada")) {
            producto.setStock(producto.getStock() + cantidad);
        } else {
            if (producto.getStock() < cantidad) {
                throw new IllegalStateException(
                        "Stock insuficiente para registrar la salida."
                );
            }

            producto.setStock(producto.getStock() - cantidad);
        }

        Inventario inventario = new Inventario();
        inventario.setProducto(producto);
        inventario.setCantidad(cantidad);
        inventario.setTipoMovimiento(tipoMovimiento);
        inventario.setFechaMovimiento(new Date());

        productoRepository.save(producto);

        return inventarioRepository.save(inventario);
    }
}