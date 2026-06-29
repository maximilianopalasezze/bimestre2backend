package com.minimarket.security;

import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.security.model.CustomUserDetails;
import com.minimarket.security.service.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AutenticacionUsuarioTest {

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    private PasswordEncoder passwordEncoder;
    private DaoAuthenticationProvider authenticationProvider;

    @BeforeEach
    void configurarProveedorAutenticacion() {
        passwordEncoder = new BCryptPasswordEncoder();
        authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(customUserDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
    }

    @Test
    void autenticaUsuarioConCredencialesValidasYRolCorrecto() {
        Usuario usuario = usuario("cliente1", passwordEncoder.encode("ClaveSegura123"), "CLIENTE");
        when(customUserDetailsService.loadUserByUsername("cliente1"))
                .thenReturn(new CustomUserDetails(usuario));

        Authentication autenticacion = authenticationProvider.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated("cliente1", "ClaveSegura123"));

        assertTrue(autenticacion.isAuthenticated());
        assertEquals("cliente1", autenticacion.getName());
        assertTrue(autenticacion.getAuthorities().stream()
                .anyMatch(autoridad -> autoridad.getAuthority().equals("ROLE_CLIENTE")));
    }

    @Test
    void rechazaAutenticacionConContrasenaInvalida() {
        Usuario usuario = usuario("cliente1", passwordEncoder.encode("ClaveSegura123"), "CLIENTE");
        when(customUserDetailsService.loadUserByUsername("cliente1"))
                .thenReturn(new CustomUserDetails(usuario));

        assertThrows(BadCredentialsException.class, () -> authenticationProvider.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated("cliente1", "claveIncorrecta")));
    }

    @Test
    void conservaPrefijoRoleParaEvaluarLasReglasDeAutorizacion() {
        Usuario usuario = usuario("admin1", passwordEncoder.encode("Admin123"), "ADMIN");
        CustomUserDetails userDetails = new CustomUserDetails(usuario);

        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(autoridad -> autoridad.getAuthority().equals("ROLE_ADMIN")));
    }

    private Usuario usuario(String username, String password, String rol) {
        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword(password);
        usuario.setRoles(Set.of(new Rol(rol)));
        return usuario;
    }
}
