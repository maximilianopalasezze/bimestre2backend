# Minimarket Plus – Semana 6: Seguridad y Pruebas Unitarias

Proyecto desarrollado en Spring Boot para aplicar autenticación, autorización por roles y pruebas unitarias en los módulos de Producto, Inventario, Venta y Usuario del sistema Minimarket Plus.

## Objetivo

El objetivo de esta actividad es proteger las operaciones críticas del backend mediante reglas de acceso según el rol del usuario y validar su funcionamiento con pruebas automatizadas.

Las principales áreas protegidas son:

* Gestión de productos.
* Movimientos de inventario.
* Registro de ventas.
* Administración de usuarios.
* Autenticación y protección de credenciales.

## Tecnologías utilizadas

* Java 17
* Spring Boot 3.4.1
* Spring Security
* Spring Data JPA
* Maven
* JUnit 5
* Mockito
* Spring Security Test
* MockMvc
* H2 Database
* JaCoCo
* BCrypt

## Seguridad implementada

La autenticación se implementó mediante HTTP Basic Authentication y las contraseñas se almacenan codificadas con BCrypt.

Los roles utilizados en el sistema son:

* `ADMIN`
* `CAJERO`
* `CLIENTE`

Spring Security transforma estos roles en autoridades con el prefijo requerido:

* `ROLE_ADMIN`
* `ROLE_CAJERO`
* `ROLE_CLIENTE`

## Roles y permisos

| Recurso              | Operación                                | Rol autorizado               |
| -------------------- | ---------------------------------------- | ---------------------------- |
| `/api/productos/**`  | Crear, actualizar y eliminar productos   | `ADMIN`                      |
| `/api/productos/**`  | Consultar productos                      | `ADMIN`, `CAJERO`, `CLIENTE` |
| `/api/inventario/**` | Registrar, editar o eliminar movimientos | `ADMIN`                      |
| `/api/inventario/**` | Consultar movimientos                    | `ADMIN`, `CAJERO`            |
| `/api/ventas/**`     | Generar ventas                           | `CAJERO`                     |
| `/api/ventas/**`     | Consultar ventas                         | `ADMIN`, `CAJERO`            |
| `/api/usuarios/**`   | Gestionar usuarios                       | `ADMIN`                      |
| `/api/carrito/**`    | Operaciones del carrito                  | `ADMIN`, `CAJERO`, `CLIENTE` |

Los usuarios sin autenticación reciben código HTTP `401 Unauthorized`.
Los usuarios autenticados sin permisos suficientes reciben código HTTP `403 Forbidden`.

## Validaciones de negocio

### Producto

* Solo un administrador puede crear, modificar o eliminar productos.
* Se prueban listado, búsqueda por identificador, búsqueda inexistente, guardado, eliminación y consulta por categoría.

### Inventario

* Solo se permiten movimientos de tipo `ENTRADA` o `SALIDA`.
* La cantidad del movimiento debe ser positiva.
* Todo movimiento debe estar asociado a un producto.
* Una salida no puede ser mayor al stock disponible.
* Las entradas aumentan el stock y las salidas lo disminuyen.

### Venta

* Solo un cajero puede generar ventas.
* Una venta debe incluir al menos un detalle.
* Cada detalle debe tener una cantidad positiva y un producto asociado.
* Se valida que exista stock suficiente antes de confirmar la venta.
* El stock se descuenta al registrar una venta válida.
* Se guarda el precio vigente del producto en cada detalle de venta.
* Se valida el stock acumulado cuando un mismo producto aparece más de una vez en una venta.

### Usuario

* Las contraseñas se codifican con BCrypt antes de persistirse.
* No se permiten contraseñas vacías.
* La contraseña no se expone en las respuestas JSON.
* Se validan credenciales correctas, contraseñas incorrectas y usuarios inexistentes.

## Pruebas implementadas

La versión final cuenta con **33 pruebas automatizadas**, ejecutadas correctamente mediante Maven.

### Seguridad de endpoints

* Producto: administrador autorizado; cliente y usuario no autenticado bloqueados.
* Inventario: administrador autorizado; cajero y usuario no autenticado bloqueados.
* Venta: cajero autorizado; cliente y usuario no autenticado bloqueados.

### Lógica de Producto

* Listado de productos.
* Búsqueda de producto existente.
* Búsqueda de producto inexistente.
* Guardado de producto.
* Eliminación de producto.
* Búsqueda por categoría.

### Lógica de Inventario

* Registro de entrada y aumento de stock.
* Registro de salida y reducción de stock.
* Rechazo de salida por stock insuficiente.
* Rechazo de tipo de movimiento inválido.

### Lógica de Venta

* Registro de venta válida.
* Descuento de stock.
* Conservación del precio vigente.
* Rechazo de venta sin detalles.
* Rechazo cuando la cantidad acumulada supera el stock disponible.

### Autenticación y Usuario

* Credenciales válidas.
* Contraseña inválida.
* Usuario inexistente.
* Conversión de roles con prefijo `ROLE_`.
* Codificación BCrypt.
* Rechazo de contraseña vacía.

## Cobertura obtenida

La ejecución final de JaCoCo entregó los siguientes resultados:

| Indicador                            |            Resultado |
| ------------------------------------ | -------------------: |
| Pruebas ejecutadas                   |                   33 |
| Fallos                               |                    0 |
| Errores                              |                    0 |
| Pruebas omitidas                     |                    0 |
| Cobertura global de instrucciones    |                  61% |
| Cobertura global de ramas            |                  46% |
| Cobertura de `service.impl`          | 69% de instrucciones |
| Cobertura de ramas en `service.impl` |                  69% |

Cobertura de clases relevantes:

| Clase                   | Cobertura de instrucciones |
| ----------------------- | -------------------------: |
| `ProductoServiceImpl`   |                       100% |
| `InventarioServiceImpl` |                        74% |
| `VentaServiceImpl`      |                        84% |
| `UsuarioServiceImpl`    |                        72% |

## Ejecución del proyecto

Ubícate en la raíz del proyecto y ejecuta:

```bash
mvn clean test
```

En Windows también puedes usar Maven Wrapper:

```powershell
.\mvnw.cmd clean test
```

## Reportes generados

Después de ejecutar las pruebas, se generan los siguientes reportes:

```text
target/surefire-reports/
```

Contiene los resultados XML de las pruebas ejecutadas.

```text
target/site/jacoco/index.html
```

Contiene el reporte visual HTML de cobertura generado por JaCoCo.

```text
target/site/jacoco/jacoco.xml
```

Contiene el reporte XML de cobertura.

## Estructura relevante

```text
src/
├── main/
│   ├── java/
│   │   └── com/minimarket/
│   │       ├── controller/
│   │       ├── entity/
│   │       ├── repository/
│   │       ├── security/
│   │       └── service/
│   └── resources/
│       └── application.properties
│
└── test/
    └── java/
        └── com/minimarket/
            ├── controller/
            ├── security/
            └── service/impl/
```

## Mejoras futuras

* Incorporar más pruebas unitarias para los servicios con menor cobertura.
* Agregar pruebas de integración con usuarios y roles persistidos en H2.
* Incorporar auditoría de movimientos de inventario y ventas.
* Configurar GitHub Actions para ejecutar `mvn clean test` automáticamente en cada push.
* Evaluar la incorporación de JWT únicamente cuando exista una implementación completa para emitir, validar y controlar la expiración de tokens.

## Repositorio

El código fuente, las pruebas y la documentación se encuentran publicados en el repositorio:

```text
https://github.com/maximilianopalasezze/bimestre2backend
```
