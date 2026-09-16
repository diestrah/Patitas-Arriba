# Estructura Java (Maven Multimódulo)

## Módulos

- **PatitasArribaModelo**: clases del modelo de dominio (paquete `model`).
- **PatitasArribaDBManager**: conexión JDBC a la base de datos (paquete `conexion`, clase `DBManager`).
- **PatitasArribaApp**: aplicación de prueba — `Principal.java` (prueba el modelo) y `PruebaConexion.java` (prueba la conexión a la base de datos).

## Requisitos

- JDK 25
- IntelliJ IDEA (usa Maven integrado, no hace falta instalarlo aparte)

## Cómo abrir y correr el proyecto

1. Abrir la carpeta `Avances` como proyecto en IntelliJ.
2. Recargar el proyecto Maven (ícono de flechas circulares en el panel de Maven).
3. Para probar el modelo: correr `PatitasArribaApp/src/main/java/ejecucion/Principal.java`.
4. Para probar la conexión a la base de datos: correr `PruebaConexion.java` (ver sección siguiente).

## Sobre la conexión a la base de datos

- `db.properties` (en `PatitasArribaApp/src/main/resources/`) tiene los datos reales de conexión — **no se sube a git** (está en `.gitignore`), cada integrante debe crear el suyo con sus propios datos.
- `db.properties.example` es la plantilla — sí está en git. Cópialo, renómbralo a `db.properties`, y completa tus datos:
  ```
  servidor=<tu_servidor>
  puerto=3306
  esquema=patitas_arriba
  usuario=<tu_usuario>
  password=<tu_password>
  ```

## Notas de diseño

- `DBManager` usa el patrón Singleton (`getInstance()`), como se enseñó en clase.
- Versión de Java estandarizada para todo el equipo: **25** (revisar que todos tengan este JDK instalado).
