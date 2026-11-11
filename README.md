# Patitas Arriba 🐾

Sistema de gestión para una clínica veterinaria de perros y gatos. Da soporte a las operaciones del día a día: clientes y sus mascotas, agenda de citas, atención médica y estética, recetas, inventario de artículos y cobro en caja (boletas).

Repositorio del trabajo de aplicación (TA) del curso **Programación 3**.

## Estructura del repositorio

| Carpeta | Contenido |
|---|---|
| [`Avances/`](Avances/) | Backend en **Java** (Maven multimódulo) y servicios REST. |
| [`Avances/sql/`](Avances/sql/) | Script de creación de la base de datos MySQL, stored procedures, datos iniciales/de prueba y diagrama físico. |
| [`Avances-dotnet/`](Avances-dotnet/) | Frontend en **Blazor (.NET 10)** y sus pruebas con Playwright + NUnit. |
| `SQL/` | Respaldo antiguo del diagrama físico. |

### Módulos del backend (`Avances/`)

| Módulo | Capa |
|---|---|
| `patitasarriba-modelo` | Clases del dominio. |
| `patitasarriba-dbmanager` | Conexión JDBC (`DBManager`, Singleton). |
| `patitasarriba-persistencia` | DAOs que llaman a los stored procedures. |
| `patitasarriba-negocio` | Reglas de negocio y validaciones. |
| `patitasarriba-rest` | API REST (Jakarta REST), se empaqueta como `.war`. |
| `patitasarriba-app` | Programas de prueba por consola (`Principal`, `PruebaConexion`). |

```
Blazor (Avances-dotnet)  →  REST /api/v1  →  negocio  →  persistencia (DAOs)  →  MySQL (procedures)
```

## Requisitos

- **JDK 25** (el código usa sentencias antes de `super(...)`, que requieren JDK 24 o superior).
- **Maven** (o el que viene integrado en IntelliJ IDEA).
- **MySQL 8** y MySQL Workbench (para abrir el diagrama `.mwb`).
- **GlassFish 8** para desplegar el módulo REST.
- **.NET 10 SDK** para el frontend.

## Puesta en marcha

### 1. Base de datos

1. Ejecutar [`Avances/sql/patitasarriba-mysql.sql`](Avances/sql/patitasarriba-mysql.sql) para crear las tablas.
2. Ejecutar todos los archivos `procedures-*.sql`.
3. Cargar [`datos-iniciales.sql`](Avances/sql/datos-iniciales.sql) y, si se desea, [`datos-prueba.sql`](Avances/sql/datos-prueba.sql).

Si la base ya estaba instalada, basta con correr los scripts `actualizacion-*.sql` una sola vez. Los detalles están en el [README de SQL](Avances/sql/README.md).

### 2. Conexión a la base

Los archivos `db.properties` **no se suben a git**. En `patitasarriba-app/src/main/resources/` y en `patitasarriba-rest/src/main/resources/`, copia `db.properties.example` como `db.properties` y completa tus datos:

```properties
servidor=<tu_servidor>
puerto=3306
esquema=<tu_esquema>
usuario=<tu_usuario>
password=<tu_password>
```

### 3. Backend

```bash
cd Avances
mvn clean install
```

- Para probar el modelo o la conexión: correr `Principal.java` o `PruebaConexion.java` en `patitasarriba-app`.
- Para la API: desplegar `patitasarriba-rest/target/*.war` en GlassFish. Los recursos quedan bajo `/api/v1` (`/clientes`, `/mascotas`, `/citas`, `/boletas`, `/login`, etc.).

Más detalle en el [README del backend](Avances/README.md).

### 4. Frontend

```bash
cd Avances-dotnet/PatitasArriba.Web
dotnet run
```

> Por ahora el frontend trabaja con datos de ejemplo y una sesión simulada por rol (`SesionDemo`); todavía no consume la API REST.

Pruebas:

```bash
cd Avances-dotnet
dotnet test
```

## Documentación

- [Reglas de negocio](Avances/REGLAS-DE-NEGOCIO.md): reglas `RN-XX-nn` y conceptos clave del dominio.
- [Propuesta de login](Avances/PROPUESTA-LOGIN.md): registro, verificación de correo y recuperación de contraseña.
- [Cambios del diagrama](Avances/sql/cambios-diagrama.md).
- [Carpeta de Google Drive](https://drive.google.com/drive/folders/1DKZuonEXVqVJXOim5KmGmPvpVRSVrPI0?usp=sharing): listado de requerimientos y diagrama de clases.
