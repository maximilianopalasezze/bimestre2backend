# MiniMarket Plus - Seguridad Backend

Proyecto backend Spring Boot para MiniMarket Plus con integración de Spring Security, JWT, BCrypt y autorización basada en roles.

## Tecnologías principales

- Java 17
- Spring Boot 3.4.1
- Spring Security
- Spring Data JPA
- H2 Database
- JJWT 0.12.6
- BCrypt para cifrado de contraseñas

## Usuarios de prueba

Al iniciar la aplicación se crean automáticamente los siguientes usuarios:

| Usuario | Contraseña | Rol |
|---|---|---|
| cliente | Cliente123 | ROLE_CLIENTE |
| empleado | Empleado123 | ROLE_EMPLEADO |
| admin | Admin123 | ROLE_ADMIN |

## Endpoint de autenticación

### Login

```http
POST http://localhost:8081/api/auth/login
Content-Type: application/json
```

Body:

```json
{
  "username": "admin",
  "password": "Admin123"
}
```

La respuesta entrega un token JWT que debe usarse en los endpoints protegidos:

```http
Authorization: Bearer TOKEN_GENERADO
```

## Reglas de autorización

| Recurso | CLIENTE | EMPLEADO | ADMIN |
|---|---:|---:|---:|
| GET /api/productos | Sí | Sí | Sí |
| POST /api/productos | No | Sí | Sí |
| PUT /api/productos/{id} | No | Sí | Sí |
| DELETE /api/productos/{id} | No | Sí | Sí |
| /api/carrito/** | Sí | Sí | Sí |
| POST /api/ventas | Sí | Sí | Sí |
| GET /api/ventas | No | Sí | Sí |
| /api/inventario/** | No | Sí | Sí |
| /api/detalle-ventas/** | No | Sí | Sí |
| /api/usuarios/** | No | No | Sí |

## Seguridad implementada

- API stateless con JWT.
- Contraseñas cifradas con BCrypt.
- `CustomUserDetailsService` para cargar usuarios desde la base de datos.
- `JwtAuthenticationFilter` para validar el token antes de procesar solicitudes protegidas.
- Autorización por roles en `SecurityConfig` y anotaciones `@PreAuthorize`.
- Cabeceras de seguridad básicas: Content Security Policy, frame options y referrer policy.
- Monitoreo básico de autenticación mediante logs de inicio exitoso y fallido.
- DTO para respuesta de usuarios, evitando exponer el hash de contraseña.

## Variables de entorno opcionales

```bash
JWT_SECRET=MiniMarketPlusClaveJwtSegura2026ConMasDe32Caracteres
JWT_EXPIRATION_MS=3600000
```

## Ejecución

```bash
mvn spring-boot:run
```

O con Maven Wrapper:

```bash
./mvnw spring-boot:run
```

La aplicación queda disponible en:

```text
http://localhost:8081
```
