# Informe técnico - Configurando y diseñando pruebas unitarias en microservicios con JUnit

## 1. Resumen técnico del avance

El proyecto MiniMarket Plus corresponde a un backend desarrollado con Spring Boot para la gestión de operaciones digitales de un minimarket. En la semana anterior el sistema ya contaba con entidades principales como Usuario, Rol, Producto, Venta y DetalleVenta, además de controladores, servicios, repositorios JPA y configuración de seguridad con JWT y roles.

Para esta semana se seleccionaron las funcionalidades relacionadas con usuarios y ventas, porque son las que se vinculan directamente con los requerimientos del caso: validar que una venta esté asociada a un usuario válido, comprobar que los datos obligatorios del usuario estén completos, verificar que exista stock suficiente antes de registrar una venta y calcular correctamente el total según los productos vendidos.

Durante la implementación se detectó que la entidad Usuario solo tenía username, password y roles. Para cumplir con el enunciado, se agregaron los atributos nombre, apellido, email y dirección. También se agregó lógica de validación en los servicios para que las pruebas unitarias verifiquen reglas reales del negocio y no solamente getters y setters.

## 2. Guía de configuración del entorno de pruebas

El proyecto utiliza Maven como gestor de dependencias. En el archivo pom.xml se mantuvo Spring Boot Starter Test, que incluye soporte para JUnit 5, y se agregó explícitamente Mockito para la ejecución de pruebas unitarias con mocks. Además, se integró JaCoCo como herramienta de medición de cobertura.

Herramientas configuradas:

- JUnit 5: utilizado para definir y ejecutar pruebas unitarias mediante anotaciones como @Test, @DisplayName y @ParameterizedTest.
- Mockito: utilizado para simular dependencias externas, especialmente repositorios, evitando depender de una base de datos real durante las pruebas unitarias.
- JaCoCo: utilizado para generar reportes de cobertura y verificar una cobertura mínima del 80% en las clases de servicio relacionadas con usuarios y ventas.

Comandos sugeridos para ejecutar las pruebas:

```bash
mvn clean test
```

Comando sugerido para ejecutar pruebas y validación de cobertura:

```bash
mvn clean verify
```

Una vez ejecutado el comando, el reporte de cobertura de JaCoCo se genera en:

```text
target/site/jacoco/index.html
```

## 3. Diseño e implementación de pruebas unitarias

### 3.1 Pruebas de Usuario

Se creó la clase de prueba UsuarioServiceImplTest, enfocada en validar los datos obligatorios del usuario y el acceso según rol.

Pruebas implementadas:

- Validación de usuario con datos completos.
- Rechazo de usuario sin username.
- Rechazo de usuario sin password.
- Rechazo de usuario sin nombre.
- Rechazo de usuario sin apellido.
- Rechazo de usuario sin email.
- Rechazo de usuario sin dirección.
- Validación de usuario con rol ROLE_EMPLEADO para registrar ventas.
- Validación de usuario con rol ROLE_ADMIN para registrar ventas.
- Rechazo de usuario con rol ROLE_CLIENTE para registrar ventas.
- Guardado de usuario usando PasswordEncoder y UsuarioRepository simulados con Mockito.
- Consulta y eliminación de usuarios usando repositorio mockeado.

Con estas pruebas se validan los datos requeridos del usuario y se simula la interacción con la base de datos mediante mocks.

### 3.2 Pruebas de Venta

Se creó la clase de prueba VentaServiceImplTest, enfocada en stock, cálculo de total, relación venta-usuario y relación detalle-producto.

Pruebas implementadas:

- Validación de stock suficiente para todos los productos de una venta.
- Rechazo de una venta cuando un producto no posee stock suficiente.
- Cálculo correcto del total de la venta.
- Validación de que la venta esté vinculada a un usuario completo.
- Validación de la relación entre DetalleVenta y Producto.
- Guardado de venta válida usando VentaRepository simulado con Mockito.
- Rechazo de venta sin usuario válido.
- Rechazo de venta sin stock suficiente.
- Consulta de ventas usando repositorio mockeado.

Estas pruebas permiten comprobar múltiples comportamientos del microservicio de ventas bajo condiciones específicas, sin depender de la base de datos real.

### 3.3 Enfoque de mocking

El enfoque utilizado fue aislar la lógica de negocio de los servicios. Para lograrlo, los repositorios UsuarioRepository y VentaRepository se simularon con Mockito. De esta manera, las pruebas no consultan una base de datos real, sino que verifican la lógica interna del servicio y confirman que las dependencias sean llamadas correctamente mediante verify().

Ejemplo de dependencias simuladas:

- UsuarioRepository para guardar, buscar y eliminar usuarios.
- PasswordEncoder para simular la encriptación de contraseñas.
- VentaRepository para guardar y consultar ventas.

## 4. Cobertura de código

Se configuró JaCoCo para generar un reporte de cobertura y validar un mínimo del 80% en las clases de servicio relacionadas con usuarios y ventas:

- com.minimarket.service.impl.UsuarioServiceImpl
- com.minimarket.service.impl.VentaServiceImpl

Para evidenciar el cumplimiento, se debe ejecutar:

```bash
mvn clean verify
```

Luego se debe abrir el siguiente archivo y tomar captura para adjuntarla al informe final:

```text
target/site/jacoco/index.html
```

## 5. Reflexión técnica

Las pruebas unitarias ayudan a mejorar la calidad del sistema porque permiten verificar la lógica de negocio antes de que el código llegue a producción. En el caso de MiniMarket Plus, esto es especialmente importante porque una venta mal validada podría generar errores operativos, como descontar productos sin stock suficiente, asociar ventas a usuarios incompletos o calcular totales incorrectos.

El uso de Mockito aporta una ventaja importante, ya que permite probar los servicios de forma aislada. Esto significa que las pruebas no dependen de una base de datos disponible ni de otros componentes externos. Si una prueba falla, es más fácil identificar el origen del problema porque se está evaluando una unidad específica del sistema.

Medir la cobertura con JaCoCo también es útil porque entrega una visión objetiva de qué partes del código fueron probadas. En sistemas con múltiples entidades relacionadas, como Usuario, Venta, Producto y DetalleVenta, la cobertura ayuda a detectar zonas del código que podrían quedar sin validación. Sin embargo, la cobertura no debe verse solo como un número, sino como una herramienta para mejorar la confianza en el comportamiento del sistema.

En conclusión, la implementación de pruebas unitarias en MiniMarket Plus permite prevenir errores, validar reglas críticas del negocio y entregar un backend más confiable, mantenible y alineado con estándares de calidad de la industria.
