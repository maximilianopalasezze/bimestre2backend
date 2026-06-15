\# MiniMarket Plus - Pruebas Unitarias con JUnit



Proyecto backend desarrollado con Spring Boot para el caso MiniMarket Plus. En esta actividad se trabajó principalmente en la configuración del entorno de pruebas unitarias y en el diseño de pruebas para validar funcionalidades relacionadas con usuarios y ventas.



\## Objetivo de la actividad



El objetivo principal fue asegurar que las funcionalidades de usuario y venta funcionen correctamente mediante pruebas unitarias. Para esto se validaron datos obligatorios del usuario, roles permitidos, registro de ventas, stock disponible y cálculo del total de una venta.



\## Tecnologías utilizadas



\* Java 17

\* Spring Boot

\* Maven

\* JUnit 5

\* Mockito

\* JaCoCo

\* H2 Database



\## Funcionalidades evaluadas



\### Usuario



Se agregaron pruebas para validar que los usuarios tengan sus datos obligatorios completos:



\* Nombre

\* Apellido

\* Email

\* Dirección



También se validó que los usuarios tengan roles permitidos para realizar operaciones críticas, como registrar ventas.



\### Venta



Se agregaron pruebas para validar que una venta:



\* Esté asociada a un usuario válido.

\* No se registre si el usuario no existe.

\* No se registre si el usuario no tiene permisos.

\* No se registre si no hay stock suficiente.

\* Calcule correctamente el total según precio y cantidad de productos.

\* Actualice el stock luego de una venta correcta.



\## Uso de Mockito



Se utilizó Mockito para simular dependencias como repositorios de usuario, producto y venta. Esto permite probar la lógica de los servicios sin depender directamente de una base de datos real.



\## Ejecución de pruebas



Para ejecutar las pruebas unitarias se debe usar el siguiente comando:



```bash

mvn clean test

```



Para ejecutar las pruebas y generar el reporte de cobertura con JaCoCo:



```bash

mvn clean verify

```



\## Cobertura de código



El proyecto fue configurado con JaCoCo para medir la cobertura de las clases principales evaluadas en la actividad:



\* UsuarioServiceImpl

\* VentaServiceImpl



La cobertura obtenida fue superior al 80%, 



\## Resultado de pruebas



Las pruebas se ejecutaron correctamente, obteniendo el siguiente resultado general:



```text

Tests run: 19

Failures: 0

Errors: 0

Skipped: 0

BUILD SUCCESS

```




