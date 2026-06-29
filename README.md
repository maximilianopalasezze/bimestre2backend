# Minimarket Plus – Semana 6: Seguridad, DTOs y Pruebas Unitarias

Backend desarrollado con Spring Boot para aplicar autenticación, autorización por roles, DTOs y pruebas automatizadas en las áreas críticas del sistema Minimarket Plus.

## Objetivo del proyecto

El objetivo de esta implementación es proteger las operaciones sensibles del backend y comprobar su correcto funcionamiento mediante pruebas unitarias y pruebas de seguridad.

Las áreas trabajadas son:

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

La autenticación se realiza mediante HTTP Basic Authentication y las contraseñas se codifican con BCrypt antes de almacenarse.

Los roles considerados por el sistema son:

* `ADMIN`
* `CAJERO`
* `CLIENTE`

Los usuarios no autenticados reciben código HTTP `401 Unauthorized`.

Los usuarios autenticados que no poseen el permiso requerido reciben código HTTP `403 Forbidden`.

## Roles y permisos

| Recurso              | Operación                                | Rol autorizado                                  |
| -------------------- | ---------------------------------------- | ----------------------------------------------- |
| `/api/productos/**`  | Crear, actualizar y eliminar productos   | `ADMIN`                                         |
| `/api/productos/**`  | Consultar productos                      | Usuarios autenticados según reglas de seguridad |
| `/api/inventario/**` | Registrar, editar o eliminar movimientos | `ADMIN`                                         |
| `/api/inventario/**` | Consultar movimientos                    | Usuarios autorizados                            |
| `/api/ventas/**`     | Generar ventas                           | `CAJERO`                                        |
| `/api/ventas/**`     | Consultar ventas                         | Usuarios autorizados                            |
| `/api/usuarios/**`   | Gestionar usuarios                       | `ADMIN`                                         |

## Implementación de DTOs

Como mejora de arquitectura, el módulo de Producto utiliza DTOs para evitar que el controlador exponga o reciba directamente la entidad `Producto`.

Se implementaron los siguientes DTOs:

| DTO                   | Propósito                                                                                  |
| --------------------- | ------------------------------------------------------------------------------------------ |
| `ProductoRequestDTO`  | Representa los datos recibidos al crear o actualizar un producto.                          |
| `ProductoResponseDTO` | Representa los datos que el backend devuelve al consultar, crear o actualizar un producto. |

`ProductoRequestDTO` recibe:

* Nombre.
* Precio.
* Stock.
* Identificador de categoría.

`ProductoResponseDTO` entrega:

* Identificador del producto.
* Nombre.
* Precio.
* Stock.
* Identificador de categoría.
* Nombre de categoría.

Este enfoque reduce el acoplamiento entre la API y las entidades de persistencia, además de permitir controlar qué información entra y sale del sistema.

## Validaciones de negocio

### Producto

* Solo un administrador puede crear, modificar o eliminar productos.
* Se valida la categoría indicada antes de guardar o actualizar un producto.
* Se prueban listado, búsqueda existente, búsqueda inexistente, guardado, eliminación y consulta por categoría.
* El controlador utiliza `ProductoRequestDTO` y `ProductoResponseDTO`.

### Inventario

* Solo se permiten movimientos de tipo `ENTRADA` o `SALIDA`.
* La cantidad del movimiento debe ser positiva.
* Todo movimiento debe estar asociado a un producto.
* Una salida no puede superar el stock disponible.
* Las entradas aumentan el stock y las salidas lo disminuyen.

### Venta

* Solo un cajero puede generar ventas.
* Una venta debe incluir al menos un detalle.
* Cada detalle debe tener una cantidad positiva y un producto asociado.
* Se valida que exista stock suficiente antes de confirmar la venta.
* El stock se descuenta al registrar una venta válida.
* Se conserva el precio vigente del producto en cada detalle.
* Se valida el stock acumulado cuando un producto aparece más de una vez en una venta.

### Usuario

* Las contraseñas se codifican con BCrypt antes de persistirse.
* No se permiten contraseñas vacías.
* La contraseña no se expone en las respuestas JSON.
* Se validan credenciales correctas, contraseñas incorrectas y usuarios inexistentes.

## Pruebas implementadas

La versión actual cuenta con **33 pruebas automatizadas**, ejecutadas correctamente mediante Maven.

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

## Ejecución de pruebas

Ubícate en la raíz del proyecto y ejecuta:

```bash
mvn clean test
```

En Windows también puedes utilizar Maven Wrapper:

```powershell
.\mvnw.cmd clean test
```

La ejecución final validada entrega:

```text
Tests run: 33, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## Reportes generados

Después de ejecutar las pruebas se generan los siguientes reportes:

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
│   │       ├── dto/
│   │       │   ├── ProductoRequestDTO.java
│   │       │   └── ProductoResponseDTO.java
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

* Incorporar DTOs para Inventario, Venta y Usuario.
* Agregar validaciones específicas de precio, stock y campos obligatorios mediante Bean Validation.
* Crear pruebas de integración con usuarios y roles persistidos en H2.
* Incorporar auditoría de movimientos de inventario y ventas.
* Configurar GitHub Actions para ejecutar `mvn clean test` automáticamente ante cada push o pull request.
* Implementar JWT solo cuando exista una clase encargada de emitir, validar y controlar la expiración de tokens.

## Repositorio

El código fuente, las pruebas y la documentación se encuentran publicados en:

```text
https://github.com/maximilianopalasezze/bimestre2backend
```
