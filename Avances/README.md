# Estructura Java (Maven Multimódulo)

## Módulos

- **patitasarriba-modelo**: clases del modelo de dominio (paquete `patitasarriba.modelo`).
- **patitasarriba-dbmanager**: conexión JDBC a la base de datos (paquete `conexion`, clase `DBManager`).
- **patitasarriba-persistencia**: capa de acceso a datos (DAOs) — en construcción.
- **patitasarriba-negocio**: capa de negocio (reglas, validaciones de flujo) — en construcción.
- **patitasarriba-app**: aplicación de prueba — `Principal.java` (prueba el modelo) y `PruebaConexion.java` (prueba la conexión a la base de datos).

Los nombres de módulo siguen la convención minúscula-con-guiones (`<proyecto>-<capa>`) usada por el profesor en su ejemplo `testsoft`.

## Requisitos

- JDK 25
- IntelliJ IDEA (usa Maven integrado, no hace falta instalarlo aparte)

## Cómo abrir y correr el proyecto

1. Abrir la carpeta `Avances` como proyecto en IntelliJ.
2. Recargar el proyecto Maven (ícono de flechas circulares en el panel de Maven).
3. Para probar el modelo: correr `patitasarriba-app/src/main/java/ejecucion/Principal.java`.
4. Para probar la conexión a la base de datos: correr `PruebaConexion.java` (ver sección siguiente).

## Sobre la conexión a la base de datos

- `db.properties` (en `patitasarriba-app/src/main/resources/`) tiene los datos reales de conexión — **no se sube a git** (está en `.gitignore`), cada integrante debe crear el suyo con sus propios datos.
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

## Últimos cambios

- Los módulos se renombraron a la convención minúscula-con-guiones (`patitasarriba-modelo`, etc.), y se agregaron los módulos `patitasarriba-persistencia` y `patitasarriba-negocio` (todavía en construcción).
- El modelo aplica encapsulamiento reforzado en todas sus clases: los getters de listas devuelven una vista no modificable (`Collections.unmodifiableList`), los setters de listas y de referencias a objetos guardan copias defensivas y validan nulos, y cada clase tiene un constructor de copia (`new Cliente(cliente)`) además del constructor normal.
- `NivelGravedad` (enum: `LEVE`, `MODERADO`, `GRAVE`, `CRITICO`) reemplaza al `String` que antes tenía `AtencionDiagnostico` para el nivel de gravedad.
