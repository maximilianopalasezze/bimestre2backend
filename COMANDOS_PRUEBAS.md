# Comandos para la demostración de pruebas unitarias

## 1. Ejecutar pruebas unitarias

```bash
mvn clean test
```

## 2. Ejecutar pruebas y validación de cobertura con JaCoCo

```bash
mvn clean verify
```

## 3. Abrir reporte de cobertura

Ruta del reporte:

```text
target/site/jacoco/index.html
```

## 4. Archivos principales modificados

- pom.xml
- src/main/java/com/minimarket/entity/Usuario.java
- src/main/java/com/minimarket/dto/UsuarioResponseDTO.java
- src/main/java/com/minimarket/DataInitializer.java
- src/main/java/com/minimarket/controller/UsuarioController.java
- src/main/java/com/minimarket/controller/VentaController.java
- src/main/java/com/minimarket/service/UsuarioService.java
- src/main/java/com/minimarket/service/VentaService.java
- src/main/java/com/minimarket/service/impl/UsuarioServiceImpl.java
- src/main/java/com/minimarket/service/impl/VentaServiceImpl.java
- src/test/java/com/minimarket/service/UsuarioServiceImplTest.java
- src/test/java/com/minimarket/service/VentaServiceImplTest.java

## 5. Evidencias sugeridas para el informe

- Captura del pom.xml mostrando Mockito y JaCoCo.
- Captura de UsuarioServiceImplTest.
- Captura de VentaServiceImplTest.
- Captura de terminal con `mvn clean test` exitoso.
- Captura de terminal con `mvn clean verify` exitoso.
- Captura de `target/site/jacoco/index.html` mostrando cobertura igual o superior al 80%.
