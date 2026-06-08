package com.minimarket.controller;

import com.minimarket.dto.UsuarioResponseDTO;
import com.minimarket.entity.Usuario;
import com.minimarket.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<UsuarioResponseDTO> listarUsuarios() {
        return usuarioService.findAll()
                .stream()
                .map(this::convertirAUsuarioResponseDTO)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> obtenerUsuarioPorId(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.findById(id);
        return usuario.map(value -> ResponseEntity.ok(convertirAUsuarioResponseDTO(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public UsuarioResponseDTO guardarUsuario(@RequestBody Usuario usuario) {
        Usuario usuarioGuardado = usuarioService.save(usuario);
        return convertirAUsuarioResponseDTO(usuarioGuardado);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuario) {
        Optional<Usuario> usuarioExistente = usuarioService.findById(id);
        if (usuarioExistente.isPresent()) {
            usuario.setId(id);
            Usuario usuarioActualizado = usuarioService.save(usuario);
            return ResponseEntity.ok(convertirAUsuarioResponseDTO(usuarioActualizado));
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.findById(id);
        if (usuario.isPresent()) { // Verifica si el usuario existe
            usuarioService.deleteById(id); // Elimina al usuario
            return ResponseEntity.noContent().build(); // Respuesta 204 (sin contenido)
        }
        return ResponseEntity.notFound().build(); // Respuesta 404 (no encontrado)
    }
    private UsuarioResponseDTO convertirAUsuarioResponseDTO(Usuario usuario) {
        List<String> roles = usuario.getRoles()
                .stream()
                .map(rol -> rol.getNombre())
                .toList();

        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getUsername(),
                roles
        );
    }

}
