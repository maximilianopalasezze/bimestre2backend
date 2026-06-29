package com.minimarket.service.impl;

import com.minimarket.entity.Usuario;
import com.minimarket.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    void guardaContrasenaCodificadaAntesDePersistirUsuario() {
        UsuarioServiceImpl usuarioService = new UsuarioServiceImpl(usuarioRepository, passwordEncoder);
        Usuario usuario = new Usuario();
        usuario.setUsername("admin1");
        usuario.setPassword("ClavePlano123");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        Usuario resultado = usuarioService.save(usuario);

        assertFalse(resultado.getPassword().equals("ClavePlano123"));
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void rechazaUsuarioSinContrasena() {
        UsuarioServiceImpl usuarioService = new UsuarioServiceImpl(usuarioRepository, passwordEncoder);
        Usuario usuario = new Usuario();
        usuario.setUsername("sinClave");

        assertThrows(IllegalArgumentException.class, () -> usuarioService.save(usuario));
    }
}
