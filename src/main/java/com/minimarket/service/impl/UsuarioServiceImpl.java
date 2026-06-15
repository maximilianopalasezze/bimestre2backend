package com.minimarket.service.impl;

import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.UsuarioRepository;
import com.minimarket.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public Optional<Usuario> findByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    @Override
    public Usuario save(Usuario usuario) {
        if (!tieneDatosObligatoriosCompletos(usuario)) {
            throw new IllegalArgumentException("El usuario debe tener nombre, apellido, email y direccion.");
        }

        if (!tieneRolValido(usuario)) {
            throw new IllegalArgumentException("El usuario debe tener al menos un rol valido.");
        }

        return usuarioRepository.save(usuario);
    }

    @Override
    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }

    @Override
    public boolean tieneDatosObligatoriosCompletos(Usuario usuario) {
        return usuario != null
                && textoValido(usuario.getNombre())
                && textoValido(usuario.getApellido())
                && textoValido(usuario.getEmail())
                && textoValido(usuario.getDireccion());
    }

    @Override
    public boolean tieneRolValido(Usuario usuario) {
        if (usuario == null || usuario.getRoles() == null || usuario.getRoles().isEmpty()) {
            return false;
        }

        return usuario.getRoles().stream()
                .map(Rol::getNombre)
                .anyMatch(rol -> rol.equalsIgnoreCase("ADMIN")
                        || rol.equalsIgnoreCase("VENDEDOR")
                        || rol.equalsIgnoreCase("USER"));
    }

    @Override
    public boolean puedeRegistrarVentas(Usuario usuario) {
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