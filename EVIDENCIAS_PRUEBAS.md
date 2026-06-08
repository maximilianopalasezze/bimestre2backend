# Evidencias recomendadas para el informe

## Capturas obligatorias sugeridas

1. Proyecto abierto en IDE con estructura de paquetes `security`, `controller`, `entity`, `repository`, `service`.
2. `pom.xml` mostrando `spring-boot-starter-security`, `spring-boot-starter-web`, `spring-boot-starter-data-jpa` y `jjwt`.
3. `SecurityConfig.java` mostrando API stateless, CSRF deshabilitado, reglas por roles y filtro JWT.
4. `JwtUtil.java` mostrando generación, firma, expiración y validación del token.
5. `JwtAuthenticationFilter.java` mostrando lectura del header `Authorization: Bearer`.
6. `CustomUserDetailsService.java` mostrando carga de usuario desde base de datos.
7. `DataInitializer.java` mostrando usuarios `cliente`, `empleado` y `admin` con BCrypt.
8. Login correcto en Postman con `/api/auth/login` y token generado.
9. Prueba sin token contra un endpoint protegido: resultado esperado 401.
10. Prueba con token inválido o alterado: resultado esperado 401.
11. Prueba con usuario CLIENTE intentando crear producto: resultado esperado 403.
12. Prueba con usuario EMPLEADO creando producto: resultado esperado 200/201.
13. Prueba con usuario ADMIN accediendo a `/api/usuarios`: resultado esperado 200.
14. Prueba de SQL Injection usando texto como `' OR '1'='1` en un campo de búsqueda o creación controlada, mostrando que no se ejecuta como SQL.
15. Prueba de XSS usando texto controlado como `<script>alert('xss')</script>` y verificando que no se ejecuta en el backend.
16. Explicación de CSRF: API stateless con JWT en header Authorization, sin sesión de servidor.
17. Repositorio GitHub con código subido y README visible.

## Usuarios para pruebas

- cliente / Cliente123
- empleado / Empleado123
- admin / Admin123
