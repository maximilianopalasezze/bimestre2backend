# Minimarket Plus – Semana 6: seguridad y pruebas unitarias

Proyecto Spring Boot para aplicar autenticación HTTP Basic, autorización por roles y pruebas unitarias en los módulos de Producto, Inventario, Venta y Usuario.

## Roles y permisos implementados

| Recurso | Operación | Rol autorizado |
|---|---|---|
| `/api/productos/**` | Crear, actualizar y eliminar | `ADMIN` |
| `/api/productos/**` | Consultar | `ADMIN`, `CAJERO`, `CLIENTE` |
| `/api/inventario/**` | Registrar, editar o eliminar movimientos | `ADMIN` |
| `/api/inventario/**` | Consultar movimientos | `ADMIN`, `CAJERO` |
| `/api/ventas/**` | Generar ventas | `CAJERO` |
| `/api/ventas/**` | Consultar ventas | `ADMIN`, `CAJERO` |
| `/api/usuarios/**` | Gestionar usuarios | `ADMIN` |

Los roles se almacenan como `ADMIN`, `CAJERO` o `CLIENTE` y, al autenticar, se convierten a autoridades de Spring Security con el prefijo requerido: `ROLE_ADMIN`, `ROLE_CAJERO` y `ROLE_CLIENTE`.

## Validaciones de negocio incorporadas

- Inventario: solo admite movimientos `ENTRADA` y `SALIDA`, con cantidad positiva y producto asociado. Las salidas no pueden superar el stock disponible.
- Venta: exige al menos un detalle, cantidades positivas, productos asociados y stock suficiente. Descuenta el stock y registra el precio vigente del producto en cada detalle.
- Usuario: la contraseña se codifica con BCrypt antes de persistirse y no se serializa en las respuestas JSON.

## Pruebas implementadas

- Seguridad de Producto: administrador autorizado; cliente y usuario no autenticado denegados.
- Seguridad de Inventario: administrador autorizado; cajero y usuario no autenticado denegados.
- Seguridad de Venta: cajero autorizado; cliente y usuario no autenticado denegados.
- Lógica de Inventario: entrada, salida, stock insuficiente y tipo inválido.
- Lógica de Venta: descuento de stock, precio vigente, stock acumulado insuficiente y venta sin detalles.
- Autenticación: credenciales válidas, contraseña inválida, usuario inexistente y autoridad con prefijo `ROLE_`.
- Usuario: codificación BCrypt y rechazo de contraseña vacía.

## Ejecución y reportes

Ejecutar en la raíz del proyecto:

```bash
mvn clean test
```

También es válido usar Maven Wrapper:

```bash
./mvnw clean test
```

Al finalizar se generan:

- Resultados XML de Surefire: `target/surefire-reports/`
- Reporte de cobertura JaCoCo en HTML: `target/site/jacoco/index.html`
- Reporte de cobertura JaCoCo en XML: `target/site/jacoco/jacoco.xml`

## Consideraciones

La autenticación se implementó con HTTP Basic porque el proyecto base ya incluía Spring Security, `CustomUserDetailsService` y BCrypt, pero su clase `JwtUtil` está vacía. Esto evita declarar JWT como una funcionalidad que el código no soporta aún.
