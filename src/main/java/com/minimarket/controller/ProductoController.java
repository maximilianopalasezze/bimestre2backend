package com.minimarket.controller;

import com.minimarket.entity.Producto;
import com.minimarket.service.ProductoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    // CLIENTE, EMPLEADO y ADMIN pueden consultar productos.
    @PreAuthorize("hasAnyAuthority('ROLE_CLIENTE', 'ROLE_EMPLEADO', 'ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<List<Producto>> listarProductos() {
        return ResponseEntity.ok(productoService.findAll());
    }

    // CLIENTE, EMPLEADO y ADMIN pueden consultar un producto por ID.
    @PreAuthorize("hasAnyAuthority('ROLE_CLIENTE', 'ROLE_EMPLEADO', 'ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerProductoPorId(@PathVariable Long id) {
        Producto producto = productoService.findById(id);

        if (producto == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(producto);
    }

    // Solo EMPLEADO y ADMIN pueden crear productos.
    @PreAuthorize("hasAnyAuthority('ROLE_EMPLEADO', 'ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<?> guardarProducto(@RequestBody Producto producto) {
        try {
            String errorValidacion = validarProducto(producto);

            if (errorValidacion != null) {
                return ResponseEntity.badRequest().body(
                        Map.of("mensaje", errorValidacion)
                );
            }

            Producto productoGuardado = productoService.save(producto);

            return ResponseEntity.ok(
                    Map.of(
                            "mensaje", "Producto creado correctamente",
                            "producto", productoGuardado
                    )
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("mensaje", "No se pudo guardar el producto. Verifique que la categoría exista.")
            );
        }
    }

    // Solo EMPLEADO y ADMIN pueden actualizar productos.
    @PreAuthorize("hasAnyAuthority('ROLE_EMPLEADO', 'ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarProducto(@PathVariable Long id, @RequestBody Producto producto) {
        try {
            Producto productoExistente = productoService.findById(id);

            if (productoExistente == null) {
                return ResponseEntity.notFound().build();
            }

            String errorValidacion = validarProducto(producto);

            if (errorValidacion != null) {
                return ResponseEntity.badRequest().body(
                        Map.of("mensaje", errorValidacion)
                );
            }

            producto.setId(id);
            Producto productoActualizado = productoService.save(producto);

            return ResponseEntity.ok(
                    Map.of(
                            "mensaje", "Producto actualizado correctamente",
                            "producto", productoActualizado
                    )
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("mensaje", "No se pudo actualizar el producto. Verifique los datos ingresados.")
            );
        }
    }

    // Solo ADMIN puede eliminar productos.
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        Producto producto = productoService.findById(id);

        if (producto == null) {
            return ResponseEntity.notFound().build();
        }

        productoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Validaciones generales del producto.
    private String validarProducto(Producto producto) {
        if (producto == null) {
            return "El producto no puede estar vacío.";
        }

        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            return "El nombre del producto es obligatorio.";
        }

        if (contieneContenidoPeligroso(producto.getNombre())) {
            return "El nombre del producto contiene contenido no permitido por seguridad.";
        }

        if (producto.getPrecio() == null || producto.getPrecio() <= 0) {
            return "El precio del producto debe ser mayor a cero.";
        }

        if (producto.getStock() == null || producto.getStock() < 0) {
            return "El stock del producto no puede ser negativo.";
        }

        if (producto.getCategoria() == null || producto.getCategoria().getId() == null) {
            return "Debe indicar el ID de una categoría existente.";
        }

        return null;
    }

    // Validación básica para bloquear entradas típicas de XSS.
    private boolean contieneContenidoPeligroso(String texto) {
        if (texto == null) {
            return false;
        }

        String textoMinuscula = texto.toLowerCase();

        return textoMinuscula.contains("<script")
                || textoMinuscula.contains("</script>")
                || textoMinuscula.contains("javascript:")
                || textoMinuscula.contains("onerror=")
                || textoMinuscula.contains("onload=")
                || textoMinuscula.contains("<iframe")
                || textoMinuscula.contains("</iframe>");
    }
}