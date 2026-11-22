# Patitas Arriba 🐾

Sistema de gestión para una clínica veterinaria de perros y gatos. Da soporte a las operaciones del día a día: clientes y sus mascotas, agenda de citas, atención médica y estética, recetas, inventario de artículos y cobro en caja (boletas).

Repositorio del trabajo de aplicación (TA) del curso **Programación 3**.

## Requerimientos

Alcance del sistema según el catálogo de requerimientos del equipo.

### Funcionales

| ID | Nombre | Descripción | Prioridad |
|---|---|---|---|
| RF01 | Autenticación y control de acceso | El sistema permitirá a los usuarios iniciar y cerrar sesión de forma segura, validando su correo electrónico y contraseña y restringiendo el acceso a los módulos según el rol asignado (administrador, veterinario o cliente). La recuperación de contraseña se especifica en RF19 | Alta |
| RF02 | Gestión de clientes | El sistema permitirá al administrador registrar, modificar, eliminar y consultar los datos de los clientes (DNI, nombres, apellidos, teléfono y correo electrónico). Los veterinarios podrán únicamente consultar (buscar y visualizar) esta información. | Alta |
| RF03 | Gestión de mascotas | El sistema permitirá al administrador registrar, modificar, eliminar y consultar los perfiles de las mascotas (nombre, sexo, tipo, raza, peso, foto opcional y fecha de nacimiento), asociándolos a su dueño. Los veterinarios podrán únicamente consultar. Los clientes podrán registrar, modificar y consultar sus propias mascotas, pero no eliminarlas. | Alta |
| RF04 | Gestión de personal | El sistema permitirá exclusivamente al administrador registrar, modificar, eliminar y consultar los datos del personal (veterinarios y otros administradores): DNI, nombres, apellidos, teléfono y, para los veterinarios, número de colegiatura. Al registrarlos, el sistema crea su cuenta de acceso. El primer administrador lo designa la clínica al implementar el sistema. | Alta |
| RF05 | Gestión de cuentas de usuario | El sistema permitirá exclusivamente al administrador registrar, modificar (incluyendo contraseñas), eliminar y consultar las cuentas de acceso del personal y de los clientes. El registro de cuentas por los propios clientes se especifica en RF18. El administrador no podrá eliminar la cuenta con la que tiene sesión activa. | Media |
| RF06 | Gestión de catálogo de diagnósticos | El sistema permitirá al administrador registrar, modificar, eliminar y consultar el catálogo base de enfermedades y diagnósticos. Los veterinarios podrán únicamente consultarlo. | Alta |
| RF07 | Gestión de catálogo de servicios | El sistema permitirá al administrador registrar, modificar, eliminar y consultar el catálogo de servicios (nombre, descripción, precio, duración estimada y tipo: consulta médica, operación, vacunación, estética o emergencia). Los veterinarios podrán únicamente consultarlo. | Alta |
| RF08 | Gestión de inventario de artículos | El sistema permitirá al administrador registrar, modificar, eliminar, actualizar el stock y consultar los artículos de la tienda, y organizarlos en categorías. Los veterinarios podrán únicamente consultar las existencias. | Alta |
| RF09 | Gestión de horarios de atención | El sistema permitirá exclusivamente al administrador registrar, modificar, eliminar y consultar la disponibilidad de turnos (días y horas) y asignarlos al personal. Los turnos deben estar dentro del horario de atención de la clínica (08:00 a 20:00) y durar entre 1 y 12 horas. No se permiten turnos que se crucen para una misma persona. | Alta |
| RF10 | Reserva de citas por cliente | El sistema permitirá a los clientes registrar, modificar y cancelar reservas desde su perfil web, solicitando uno o varios servicios para su mascota. El sistema asigna automáticamente al veterinario disponible. | Alta |
| RF11 | Gestión administrativa de citas | El sistema permitirá al administrador registrar, modificar, aprobar, rechazar, cancelar (citas pendientes o aprobadas), actualizar el estado y consultar las citas. Los veterinarios podrán únicamente consultar y filtrar su agenda diaria. | Alta |
| RF12 | Reporte de agenda diaria | El sistema permitirá generar un reporte exportable de las citas pendientes del día para organizar la atención. | Media |
| RF13 | Gestión de atenciones e historias clínicas | El sistema permitirá exclusivamente a los veterinarios registrar, modificar, eliminar y consultar la atención de cada servicio solicitado en una cita, incluyendo motivo de consulta, observaciones, diagnósticos con su nivel de gravedad e insumos utilizados, indicando cuáles se cobran aparte al cliente. El sistema actualizará automáticamente el stock de los insumos utilizados. | Alta |
| RF14 | Gestión de recetas médicas | El sistema permitirá exclusivamente a los veterinarios registrar, modificar, eliminar y consultar las recetas de las atenciones médicas, detallando medicamentos, dosis, frecuencia, duración y cantidad. | Alta |
| RF15 | Reporte de historial clínico | El sistema permitirá al administrador y a los veterinarios generar un reporte cronológico del historial clínico de una mascota, detallando la información del paciente y los diagnósticos, insumos y recetas de cada atención. | Media |
| RF16 | Reporte de boleta de venta | El sistema permitirá exclusivamente al administrador generar e imprimir el comprobante de pago (boleta) de una venta, detallando los servicios y artículos con su cantidad, precio unitario e importe, la operación gravada, el IGV (18 %) y el total a pagar. Una boleta anulada no se imprime. | Alta |
| RF17 | Facturación consolidada | El sistema permitirá exclusivamente al administrador generar, modificar y anular un comprobante de pago (boleta) que consolide los servicios realizados, los insumos cobrados y los productos adquiridos, desglosando precios, total a pagar y método de pago. Su impresión se especifica en RF16. El número de boleta es correlativo y no se reutiliza. Anular cambia el estado de la boleta; no la borra. | Alta |
| RF18 | Registro de clientes con verificación de correo | El sistema permitirá a un cliente registrarse por sí mismo; la cuenta se crea al confirmar un enlace enviado a su correo electrónico. | Alta |
| RF19 | Recuperación de contraseña | El sistema permitirá a un usuario recuperar su contraseña mediante un enlace temporal enviado a su correo electrónico. | Alta |

### No funcionales

| ID | Nombre | Descripción | Prioridad |
|---|---|---|---|
| RNF01 | Arquitectura Distribuida | El sistema integrará obligatoriamente el front-end (C#) y el back-end (Java) comunicándose exclusivamente a través de servicios web (REST o SOAP), aislando la lógica de negocio de la interfaz. | Alta |
| RNF02 | Rendimiento y Tiempos de Respuesta | El tiempo de respuesta del sistema para las transacciones (CRUDs y registro de citas) será de un máximo de 2 a 4 segundos, garantizando fluidez durante la evaluación del sistema. | Alta |
| RNF03 | Compatibilidad de Interfaz (Solo Web) | El sistema será desarrollado con una interfaz web diseñada exclusivamente para computadoras de escritorio (resolución recomendada de 1366x768 o superior), garantizando compatibilidad con navegadores modernos (Chrome, Edge). No incluirá diseño móvil. | Alta |
| RNF04 | Disponibilidad y mantenimiento | El sistema deberá compilar y ejecutarse correctamente de forma local (o en el servidor de pruebas asignado) sin caídas durante las sesiones de operación. | Alta |
| RNF05 | Seguridad | Para garantizar la seguridad del acceso, el sistema almacenará las contraseñas de los usuarios cifradas en la base de datos utilizando algoritmos de hashing (ej. BCrypt o SHA-256). Esto será demostrable consultando directamente los registros de la base de datos. Los enlaces de verificación y recuperación también se guardan solo como hash. | Alta |
| RNF06 | Respaldo (Backup) Manual | El proyecto incluirá scripts de base de datos (SQL Dump) que permitirán al administrador o desarrollador realizar copias de seguridad manuales de las tablas y registros para evitar pérdida de datos. | Alta |
| RNF07 | Conservación del historial | El sistema conservará toda la información registrada: cuando se elimine un registro dejará de mostrarse en las consultas, pero permanecerá almacenado para preservar el historial clínico y comercial de la clínica. | Alta |

## Estructura del repositorio

| Carpeta | Contenido |
|---|---|
| [`BackEnd/`](BackEnd/) | Backend en **Java** (Maven multimódulo) y servicios REST. |
| [`BackEnd/sql/`](BackEnd/sql/) | Script de creación de la base de datos MySQL, stored procedures, datos iniciales/de prueba y diagrama físico. |
| [`FrontEnd/`](FrontEnd/) | Frontend en **Blazor (.NET 10)** y sus pruebas con Playwright + NUnit. |

### Módulos del backend (`BackEnd/`)

| Módulo | Capa |
|---|---|
| `patitasarriba-modelo` | Clases del dominio. |
| `patitasarriba-dbmanager` | Conexión JDBC (`DBManager`, Singleton). |
| `patitasarriba-persistencia` | DAOs que llaman a los stored procedures. |
| `patitasarriba-negocio` | Reglas de negocio y validaciones. |
| `patitasarriba-rest` | API REST (Jakarta REST), se empaqueta como `.war`. |
| `patitasarriba-reportes` | Reportes en PDF con JasperReports: un servlet por reporte (agenda diaria, historial clínico y boleta), se empaqueta como `.war` aparte. Los diseños (`.jrxml`) están en [`Reportes/`](Reportes/), el proyecto de Jaspersoft Studio. |
| `patitasarriba-app` | Programas de prueba por consola (`Principal`, `PruebaConexion`). |

```
Blazor (FrontEnd)  →  REST /api/v1  →  negocio  →  persistencia (DAOs)  →  MySQL (procedures)
                   →  servlets /reportes/...  →  JasperReports  →  MySQL (procedures-reportes.sql)
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

Desde MySQL Workbench (o la consola de `mysql`), con un usuario que pueda crear esquemas, correr en este orden los scripts de [`BackEnd/sql/`](BackEnd/sql/). Atajo para una base nueva: [`instalar-desde-cero.sql`](BackEnd/sql/instalar-desde-cero.sql) hace los cuatro pasos de una vez, pero **borra** el esquema `patitasarriba` si ya existía.

1. [`patitasarriba-mysql.sql`](BackEnd/sql/patitasarriba-mysql.sql): crea el esquema `patitasarriba` y sus tablas.
2. Todos los archivos `procedures-*.sql` (el orden entre ellos no importa).
3. [`datos-iniciales.sql`](BackEnd/sql/datos-iniciales.sql): crea el primer administrador (correo `admin@patitasarriba.pe`, contraseña `Admin123`; cámbiala después del primer ingreso).
4. Opcional, para tener datos con qué probar: [`datos-prueba.sql`](BackEnd/sql/datos-prueba.sql). Se corre **una sola vez**; para recargarlo, primero `limpiar-tablas.sql`.

Si la base ya estaba instalada de antes, en vez de reinstalar basta con correr una vez los scripts `actualizacion-*.sql` que falten (el último es `actualizacion-rendimiento.sql`: sin él fallan Mis citas, Mi agenda y el registro de citas). Para empezar de cero, `borrar-tablas.sql` elimina todas las tablas. Los detalles están en el [README de SQL](BackEnd/sql/README.md).

**Cuentas de `datos-prueba.sql`** (todas con la contraseña `Patitas123`):

| Rol | Correo |
|---|---|
| Administrador | `rosa.admin@patitas.pe` |
| Veterinario | `dra.lucia@patitas.pe` |
| Cliente | `ana.torres@gmail.com` |

### 2. Conexión a la base

Los archivos `db.properties` **no se suben a git** (cada integrante usa su propia base). En `BackEnd/patitasarriba-rest/src/main/resources/` y en `BackEnd/patitasarriba-reportes/src/main/resources/` (y en `BackEnd/patitasarriba-app/src/main/resources/` si vas a usar los programas de consola), copia `db.properties.example` como `db.properties` y completa tus datos:

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
cd BackEnd
mvn clean install
```

Esto genera dos `.war`: `BackEnd/patitasarriba-rest/target/patitasarriba-rest-1.0.war` (la API) y `BackEnd/patitasarriba-reportes/target/patitasarriba-reportes-1.0.war` (los reportes). Para desplegarlos en GlassFish:

```bash
asadmin start-domain
```

```bash
asadmin deploy --force=true --contextroot patitasarriba-rest-1.0-SNAPSHOT BackEnd/patitasarriba-rest/target/patitasarriba-rest-1.0.war
asadmin deploy --force=true --contextroot patitasarriba-reportes-1.0-SNAPSHOT BackEnd/patitasarriba-reportes/target/patitasarriba-reportes-1.0.war
```

También se puede subir el `.war` desde la consola de administración de GlassFish (`http://localhost:4848`), o desplegarlo desde IntelliJ con una configuración de GlassFish.

En local se mantiene el contexto `patitasarriba-rest-1.0-SNAPSHOT` (el mismo de `appsettings.json` y de la configuración de GlassFish en IntelliJ), aunque los `pom.xml` ya no usen `-SNAPSHOT`. La API queda en:

```
http://localhost:8080/patitasarriba-rest-1.0-SNAPSHOT/api/v1
```

Para comprobar que responde y que llega a la base, abre `http://localhost:8080/patitasarriba-rest-1.0-SNAPSHOT/api/v1/servicios` en el navegador: debe devolver un JSON. Si devuelve error 500, revisa `db.properties` y el log de GlassFish (`glassfish/domains/domain1/logs/server.log`).

Los reportes quedan en `http://localhost:8080/patitasarriba-reportes-1.0-SNAPSHOT/` (esa página tiene un enlace de prueba a cada uno):

| Reporte | URL |
|---|---|
| Agenda diaria (RF12) | `/reportes/agenda-diaria?fecha=AAAA-MM-DD[&veterinario={id}][&generadoPor={nombre}]` |
| Historial clínico (RF15) | `/reportes/historial-clinico?mascota={id}[&generadoPor={nombre}]` |
| Boleta de venta (RF16) | `/reportes/boleta?id={id}[&generadoPor={nombre}]` |

Necesitan los procedures de `procedures-reportes.sql` en la base. Si se cambia un diseño en Jaspersoft Studio, hay que copiar el `.jasper` que genera Studio a `BackEnd/patitasarriba-reportes/src/main/resources/reportes/` y volver a desplegar.

Los programas de consola de `patitasarriba-app` (`Principal.java`, `PruebaConexion.java`) sirven para probar el modelo y la conexión sin GlassFish. Más detalle en el [README del backend](BackEnd/README.md).

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

La dirección de la API se configura en [`FrontEnd/PatitasArriba.Web/appsettings.json`](FrontEnd/PatitasArriba.Web/appsettings.json):

```json
"RestApiBaseUrl": "http://localhost:8080/patitasarriba-rest-1.0-SNAPSHOT/api/v1"
```

Debe incluir el contexto con que se desplegó el `.war` y terminar en `/api/v1`. Si desplegaste con otro `--contextroot`, cámbiala aquí.

Los reportes se abren en el navegador directamente desde el `.war` de reportes, así que tienen su propia dirección (sin `/api/v1`):

```json
"Reportes": {
  "BaseUrl": "http://localhost:8080/patitasarriba-reportes-1.0-SNAPSHOT"
}
```

Con la API ya corriendo:

```bash
cd FrontEnd/PatitasArriba.Web
dotnet run --launch-profile http
```

El sitio queda en `http://localhost:5118`. Inicia sesión con una de las cuentas de prueba; el menú cambia según el rol de la cuenta (administrador, veterinario o cliente).

### 5. Pruebas

```bash
cd FrontEnd
dotnet test
```

- `RestServicesTests` prueba los servicios REST del frontend contra una API simulada; no necesita nada levantado.
- `PruebasInterfaz` (Playwright) abre el navegador sobre `http://localhost:5118`: necesita el frontend y la API corriendo con los datos de prueba, y los navegadores de Playwright instalados (`pwsh PatitasArriba.Tests/bin/Debug/net10.0/playwright.ps1 install` después de compilar).

Para correr solo las pruebas que no necesitan nada levantado:

```bash
cd FrontEnd
dotnet test --filter "FullyQualifiedName~RestServicesTests"
```

### 6. Despliegue en AWS

Sigue la clase de despliegue: dos servidores EC2 con Windows Server, uno con GlassFish para el backend y otro con IIS para el frontend.

- **Backend:** los `pom.xml` ya no usan `-SNAPSHOT`. Con `db.properties` apuntando a RDS, `mvn clean package` genera `patitasarriba-rest-1.0.war`. En la consola de GlassFish del servidor (`http://localhost:4848`) se despliega con la ruta de contexto `patitasarriba-rest`, así que la API queda en `http://<servidor>:8080/patitasarriba-rest/api/v1`. El `.war` de reportes se despliega igual, con la ruta de contexto `patitasarriba-reportes`.
- **Frontend:** [`appsettings.Production.json`](FrontEnd/PatitasArriba.Web/appsettings.Production.json) reemplaza `RestApiBaseUrl` cuando la app corre fuera de desarrollo, como en IIS. Antes de publicar, cambia `IP-PRIVADA-DEL-BACKEND` por la IP privada del servidor del backend y, en `Reportes:BaseUrl`, `IP-PUBLICA-DEL-BACKEND` por su IP o DNS público (el PDF lo pide el navegador del usuario, no el servidor del frontend): a diferencia del DNS público, no cambia cuando la instancia se detiene y se vuelve a encender. En local (`dotnet run` o Visual Studio) se sigue usando `appsettings.json`.
- Cada vez que se reinicia el servidor del backend hay que volver a ejecutar `asadmin start-domain`.

### Problemas comunes

| Síntoma | Causa probable |
|---|---|
| `invalid source release` al compilar con Maven | `JAVA_HOME` apunta a un JDK anterior al 25. |
| La API responde 500 en todo | `db.properties` con datos incorrectos, o faltan los procedures o scripts de actualización en la base. |
| El frontend muestra errores de conexión al cargar las pantallas | GlassFish no está corriendo, o `RestApiBaseUrl` no coincide con el contexto del `.war`. |
| «Descargar PDF» da error 404 o no abre | El `.war` de reportes no está desplegado, o `Reportes:BaseUrl` no coincide con su contexto. |
| El PDF del reporte sale con error 500 | Falta correr `procedures-reportes.sql` en la base, o falta el `db.properties` del módulo de reportes. |
| `dotnet build` falla con `apphost.exe` bloqueado | El frontend ya está corriendo; deténlo antes de compilar. |

## Documentación

- [Reglas de negocio](BackEnd/REGLAS-DE-NEGOCIO.md): reglas `RN-XX-nn` y conceptos clave del dominio.
- [Propuesta de login](BackEnd/PROPUESTA-LOGIN.md): registro, verificación de correo y recuperación de contraseña.
- [Cambios del diagrama](BackEnd/sql/cambios-diagrama.md).
- [Carpeta de Google Drive](https://drive.google.com/drive/folders/1DKZuonEXVqVJXOim5KmGmPvpVRSVrPI0?usp=sharing): listado de requerimientos y diagrama de clases.
