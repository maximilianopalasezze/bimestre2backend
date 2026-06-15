package com.minimarket.service.impl;

import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    @Test
    void debeGuardarUsuarioCuandoTieneDatosCompletosYRolValido() {
        Usuario usuario = crearUsuarioValido("VENDEDOR");

        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        Usuario resultado = usuarioService.save(usuario);

        assertNotNull(resultado);
        assertEquals("Juan", resultado.getNombre());
        assertEquals("Pérez", resultado.getApellido());
        assertEquals("juan@test.cl", resultado.getEmail());
        assertEquals("Av. Siempre Viva 123", resultado.getDireccion());

        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    void debeLanzarErrorCuandoUsuarioNoTieneNombre() {
        Usuario usuario = crearUsuarioValido("VENDEDOR");
        usuario.setNombre("");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> usuarioService.save(usuario)
        );

        assertTrue(exception.getMessage().contains("nombre"));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void debeLanzarErrorCuandoUsuarioNoTieneEmail() {
        Usuario usuario = crearUsuarioValido("VENDEDOR");
        usuario.setEmail(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> usuarioService.save(usuario)
        );

        assertTrue(exception.getMessage().contains("email"));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void debeLanzarErrorCuandoUsuarioNoTieneRolValido() {
        Usuario usuario = crearUsuarioValido("INVITADO");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> usuarioService.save(usuario)
        );

        assertTrue(exception.getMessage().contains("rol"));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void debePermitirRegistrarVentasCuandoUsuarioEsVendedor() {
        Usuario usuario = crearUsuarioValido("VENDEDOR");

        boolean resultado = usuarioService.puedeRegistrarVentas(usuario);

        assertTrue(resultado);
    }

    @Test
    void noDebePermitirRegistrarVentasCuandoUsuarioSoloEsUser() {
        Usuario usuario = crearUsuarioValido("USER");

        boolean resultado = usuarioService.puedeRegistrarVentas(usuario);

        assertFalse(resultado);
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
}