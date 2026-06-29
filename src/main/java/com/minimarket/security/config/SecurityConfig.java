package com.minimarket.security.config;

import com.minimarket.security.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                    DaoAuthenticationProvider authenticationProvider) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
                .authenticationProvider(authenticationProvider)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/public/**", "/h2-console/**", "/error").permitAll()

                        // Productos: lectura para usuarios autenticados; modificaciones solo para ADMIN.
                        .requestMatchers(HttpMethod.POST, "/api/productos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/productos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/productos/**").hasRole("ADMIN")
                        .requestMatchers("/api/productos/**").hasAnyRole("ADMIN", "CAJERO", "CLIENTE")

                        // Inventario: los movimientos pueden ser administrados únicamente por ADMIN.
                        .requestMatchers(HttpMethod.POST, "/api/inventario/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/inventario/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/inventario/**").hasRole("ADMIN")
                        .requestMatchers("/api/inventario/**").hasAnyRole("ADMIN", "CAJERO")

                        // Ventas: solo CAJERO puede generar una venta; ADMIN y CAJERO pueden consultarlas.
                        .requestMatchers(HttpMethod.POST, "/api/ventas/**").hasRole("CAJERO")
                        .requestMatchers("/api/ventas/**").hasAnyRole("ADMIN", "CAJERO")

                        // La administración de usuarios queda reservada para ADMIN.
                        .requestMatchers("/api/usuarios/**").hasRole("ADMIN")
                        .requestMatchers("/api/carrito/**").hasAnyRole("ADMIN", "CAJERO", "CLIENTE")
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/public/hola")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(DaoAuthenticationProvider authenticationProvider) {
        return new ProviderManager(List.of(authenticationProvider));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
