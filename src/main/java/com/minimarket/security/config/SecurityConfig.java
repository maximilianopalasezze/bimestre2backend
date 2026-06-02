package com.minimarket.security.config;

import com.minimarket.security.filter.JwtAuthenticationFilter;
import com.minimarket.security.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService,
                          JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // API REST stateless: se valida cada solicitud con JWT, sin crear sesion en servidor.
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                        .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'"))
                        .frameOptions(frame -> frame.sameOrigin())
                        .referrerPolicy(referrer -> referrer.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN))
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .userDetailsService(customUserDetailsService)
                .authorizeHttpRequests(auth -> auth
                        // Endpoints publicos: login, registro, prueba publica y consola H2.
                        .requestMatchers("/api/auth/**", "/public/**", "/h2-console/**").permitAll()

                        // Clientes, empleados y gerentes pueden consultar productos y categorias.
                        .requestMatchers(HttpMethod.GET, "/api/productos/**", "/api/categorias/**")
                        .hasAnyRole("CLIENTE", "EMPLEADO", "GERENTE")

                        // El carrito puede ser usado por clientes, empleados y gerentes.
                        .requestMatchers("/api/carrito/**")
                        .hasAnyRole("CLIENTE", "EMPLEADO", "GERENTE")

                        // Solo empleados y gerentes administran productos, categorias, inventario y ventas.
                        .requestMatchers("/api/productos/**", "/api/categorias/**", "/api/inventario/**",
                                "/api/ventas/**", "/api/detalle-ventas/**")
                        .hasAnyRole("EMPLEADO", "GERENTE")

                        // La administracion de usuarios queda reservada solo al gerente.
                        .requestMatchers("/api/usuarios/**")
                        .hasRole("GERENTE")

                        .anyRequest().authenticated()
                )
                // Se deshabilitan login por formulario, logout y Basic Auth para usar JWT como mecanismo principal.
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
