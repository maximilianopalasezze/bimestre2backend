package com.minimarket.controller;

import com.minimarket.dto.ProductoRequestDTO;
import com.minimarket.dto.ProductoResponseDTO;
import com.minimarket.entity.Categoria;
import com.minimarket.entity.Producto;
import com.minimarket.service.CategoriaService;
import com.minimarket.service.ProductoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    public ProductoController(
            ProductoService productoService,
            CategoriaService categoriaService
    ) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public List<ProductoResponseDTO> listarProductos() {
        return productoService.findAll()
                .stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> obtenerProductoPorId(@PathVariable Long id) {
        Producto producto = productoService.findById(id);

        if (producto == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(convertirAResponseDTO(producto));
    }

    @PostMapping
    public ResponseEntity<?> guardarProducto(@RequestBody ProductoRequestDTO requestDTO) {
        Categoria categoria = categoriaService.findById(requestDTO.getCategoriaId());

        if (categoria == null) {
            return ResponseEntity.badRequest()
                    .body("La categoria indicada no existe.");
        }

        Producto producto = convertirAEntidad(requestDTO, categoria);
        Producto productoGuardado = productoService.save(producto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(convertirAResponseDTO(productoGuardado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarProducto(
            @PathVariable Long id,
            @RequestBody ProductoRequestDTO requestDTO
    ) {
        Producto productoExistente = productoService.findById(id);

        if (productoExistente == null) {
            return ResponseEntity.notFound().build();
        }

        Categoria categoria = categoriaService.findById(requestDTO.getCategoriaId());

        if (categoria == null) {
            return ResponseEntity.badRequest()
                    .body("La categoria indicada no existe.");
        }

        productoExistente.setNombre(requestDTO.getNombre());
        productoExistente.setPrecio(requestDTO.getPrecio());
        productoExistente.setStock(requestDTO.getStock());
        productoExistente.setCategoria(categoria);

        Producto productoActualizado = productoService.save(productoExistente);

        return ResponseEntity.ok(convertirAResponseDTO(productoActualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        Producto producto = productoService.findById(id);

        if (producto == null) {
            return ResponseEntity.notFound().build();
        }

        productoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Producto convertirAEntidad(
            ProductoRequestDTO requestDTO,
            Categoria categoria
    ) {
        Producto producto = new Producto();
        producto.setNombre(requestDTO.getNombre());
        producto.setPrecio(requestDTO.getPrecio());
        producto.setStock(requestDTO.getStock());
        producto.setCategoria(categoria);
        return producto;
    }

    private ProductoResponseDTO convertirAResponseDTO(Producto producto) {
        ProductoResponseDTO responseDTO = new ProductoResponseDTO();
        responseDTO.setId(producto.getId());
        responseDTO.setNombre(producto.getNombre());
        responseDTO.setPrecio(producto.getPrecio());
        responseDTO.setStock(producto.getStock());

        if (producto.getCategoria() != null) {
            responseDTO.setCategoriaId(producto.getCategoria().getId());
            responseDTO.setCategoriaNombre(producto.getCategoria().getNombre());
        }

        return responseDTO;
    }
}