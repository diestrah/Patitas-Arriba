# Patitas Arriba 🐾

Sistema de gestión para una clínica veterinaria de perros y gatos. Da soporte a las operaciones del día a día: clientes y sus mascotas, agenda de citas, atención médica y estética, recetas, inventario de artículos y cobro en caja (boletas).

Repositorio del trabajo de aplicación (TA) del curso **Programación 3**.

## Estructura del repositorio

| Carpeta | Contenido |
|---|---|
| [`Avances/`](Avances/) | Backend en **Java** (Maven multimódulo) y servicios REST. |
| [`Avances/sql/`](Avances/sql/) | Script de creación de la base de datos MySQL, stored procedures, datos iniciales/de prueba y diagrama físico. |
| [`Avances-dotnet/`](Avances-dotnet/) | Frontend en **Blazor (.NET 10)** y sus pruebas con Playwright + NUnit. |

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

- **JDK 25** (el código usa sentencias antes de `super(...)`, que requieren JDK 24 o superior). Con un JDK más antiguo Maven falla con `invalid source release`.
- **Maven** (o el que viene integrado en IntelliJ IDEA).
- **MySQL 8 o superior** y MySQL Workbench (para correr los scripts y abrir el diagrama `.mwb`).
- **GlassFish 8**, configurado para correr con el JDK 25, para desplegar el módulo REST.
- **.NET 10 SDK** para el frontend.

## Puesta en marcha

Hay que levantar las tres piezas en este orden: **base de datos → API REST en GlassFish → frontend Blazor**. El frontend no guarda datos propios: todo lo lee y lo escribe a través de la API.

### 1. Base de datos

Desde MySQL Workbench (o la consola de `mysql`), con un usuario que pueda crear esquemas, correr en este orden los scripts de [`Avances/sql/`](Avances/sql/):

1. [`patitasarriba-mysql.sql`](Avances/sql/patitasarriba-mysql.sql): crea el esquema `patitasarriba` y sus tablas.
2. Todos los archivos `procedures-*.sql` (el orden entre ellos no importa).
3. [`datos-iniciales.sql`](Avances/sql/datos-iniciales.sql): crea el primer administrador (correo `admin@patitasarriba.pe`, contraseña `Admin123`; cámbiala después del primer ingreso).
4. Opcional, para tener datos con qué probar: [`datos-prueba.sql`](Avances/sql/datos-prueba.sql). Se corre **una sola vez**; para recargarlo, primero `limpiar-tablas.sql`.

Si la base ya estaba instalada de antes, en vez de reinstalar basta con correr una vez los scripts `actualizacion-*.sql` que falten. Para empezar de cero, `borrar-tablas.sql` elimina todas las tablas. Los detalles están en el [README de SQL](Avances/sql/README.md).

**Cuentas de `datos-prueba.sql`** (todas con la contraseña `Patitas123`):

| Rol | Correo |
|---|---|
| Administrador | `rosa.admin@patitas.pe` |
| Veterinario | `dra.lucia@patitas.pe` |
| Cliente | `ana.torres@gmail.com` |

### 2. Conexión a la base

Los archivos `db.properties` **no se suben a git** (cada integrante usa su propia base). En `Avances/patitasarriba-rest/src/main/resources/` (y en `Avances/patitasarriba-app/src/main/resources/` si vas a usar los programas de consola), copia `db.properties.example` como `db.properties` y completa tus datos:

```properties
servidor=localhost
puerto=3306
esquema=patitasarriba
usuario=<tu_usuario>
password=<tu_password>
```

`servidor` puede ser `localhost` o el host de una base en RDS. El `db.properties` se empaqueta dentro del `.war`: si lo cambias, vuelve a compilar y a desplegar.

### 3. Backend (API REST)

Compilar todos los módulos con el JDK 25:

```bash
cd Avances
mvn clean install
```

Esto genera `Avances/patitasarriba-rest/target/patitasarriba-rest-1.0-SNAPSHOT.war`. Para desplegarlo en GlassFish:

```bash
asadmin start-domain
```

```bash
asadmin deploy --force=true Avances/patitasarriba-rest/target/patitasarriba-rest-1.0-SNAPSHOT.war
```

También se puede subir el `.war` desde la consola de administración de GlassFish (`http://localhost:4848`), o desplegarlo desde IntelliJ con una configuración de GlassFish.

Sin `--contextroot`, la API queda en:

```
http://localhost:8080/patitasarriba-rest-1.0-SNAPSHOT/api/v1
```

Para comprobar que responde y que llega a la base, abre `http://localhost:8080/patitasarriba-rest-1.0-SNAPSHOT/api/v1/servicios` en el navegador: debe devolver un JSON. Si devuelve error 500, revisa `db.properties` y el log de GlassFish (`glassfish/domains/domain1/logs/server.log`).

Los programas de consola de `patitasarriba-app` (`Principal.java`, `PruebaConexion.java`) sirven para probar el modelo y la conexión sin GlassFish. Más detalle en el [README del backend](Avances/README.md).

#### Correos (opcional)

El auto-registro de clientes y la recuperación de contraseña envían un correo con un enlace. Se configuran con variables de entorno **del proceso de GlassFish** (defínelas antes de `asadmin start-domain`):

| Variable | Para qué |
|---|---|
| `PATITAS_SMTP_HOST`, `PATITAS_SMTP_USUARIO`, `PATITAS_SMTP_CLAVE` | Servidor y credenciales SMTP. |
| `PATITAS_SMTP_PUERTO` | Puerto SMTP (por defecto `465`). |
| `PATITAS_SMTP_REMITENTE` | Remitente (por defecto, el usuario SMTP). |
| `PATITAS_URL_FRONT` | URL del frontend que va en los enlaces (por defecto `http://localhost:5118`). |

Si falta el host, el usuario o la clave, los correos **no se envían**: se escriben en el log de GlassFish, y desde ahí se puede copiar el enlace para probar.

### 4. Frontend (Blazor)

La dirección de la API se configura en [`Avances-dotnet/PatitasArriba.Web/appsettings.json`](Avances-dotnet/PatitasArriba.Web/appsettings.json):

```json
"RestApiBaseUrl": "http://localhost:8080/patitasarriba-rest-1.0-SNAPSHOT/api/v1"
```

Debe incluir el contexto con que se desplegó el `.war` y terminar en `/api/v1`. Si desplegaste con otro `--contextroot`, cámbiala aquí.

Con la API ya corriendo:

```bash
cd Avances-dotnet/PatitasArriba.Web
dotnet run --launch-profile http
```

El sitio queda en `http://localhost:5118`. Inicia sesión con una de las cuentas de prueba; el menú cambia según el rol de la cuenta (administrador, veterinario o cliente).

### 5. Pruebas

```bash
cd Avances-dotnet
dotnet test
```

- `RestServicesTests` prueba los servicios REST del frontend contra una API simulada; no necesita nada levantado.
- `PruebasInterfaz` (Playwright) abre el navegador sobre `http://localhost:5118`: necesita el frontend y la API corriendo con los datos de prueba, y los navegadores de Playwright instalados (`pwsh PatitasArriba.Tests/bin/Debug/net10.0/playwright.ps1 install` después de compilar).

Para correr solo las pruebas que no necesitan nada levantado:

```bash
cd Avances-dotnet
dotnet test --filter "FullyQualifiedName~RestServicesTests"
```

### Problemas comunes

| Síntoma | Causa probable |
|---|---|
| `invalid source release` al compilar con Maven | `JAVA_HOME` apunta a un JDK anterior al 25. |
| La API responde 500 en todo | `db.properties` con datos incorrectos, o faltan los procedures o scripts de actualización en la base. |
| El frontend muestra errores de conexión al cargar las pantallas | GlassFish no está corriendo, o `RestApiBaseUrl` no coincide con el contexto del `.war`. |
| `dotnet build` falla con `apphost.exe` bloqueado | El frontend ya está corriendo; deténlo antes de compilar. |

## Documentación

- [Reglas de negocio](Avances/REGLAS-DE-NEGOCIO.md): reglas `RN-XX-nn` y conceptos clave del dominio.
- [Propuesta de login](Avances/PROPUESTA-LOGIN.md): registro, verificación de correo y recuperación de contraseña.
- [Cambios del diagrama](Avances/sql/cambios-diagrama.md).
- [Carpeta de Google Drive](https://drive.google.com/drive/folders/1DKZuonEXVqVJXOim5KmGmPvpVRSVrPI0?usp=sharing): listado de requerimientos y diagrama de clases.
