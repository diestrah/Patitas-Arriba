# Diagrama Lógico en MySQL ⚙️

## Orden de ejecución

**Base nueva o empezar de cero** (borra el esquema `patitasarriba` si ya existía):
- Atajo: `instalar-desde-cero.sql` hace todo en un solo paso. **Borra** el esquema completo, así que no se corre en una base cuyos datos quieras conservar.
- O, a mano, en este orden:
  1. `patitasarriba-mysql.sql`: esquema y tablas.
  2. Todos los `procedures-*.sql` (el orden entre ellos no importa).
  3. `datos-iniciales.sql`: primer administrador (`admin@patitasarriba.pe` / `Admin123`).
  4. Opcional: `datos-prueba.sql`, una sola vez (todas sus cuentas con la contraseña `Patitas123`). Para recargarlo, primero `limpiar-tablas.sql`.

**Base ya instalada antes** (para no perder sus datos): correr una vez los `actualizacion-*.sql` que falten:
1. `actualizacion-acceso.sql`: registro con verificación y recuperación de contraseña.
2. `actualizacion-foto-mascota.sql`: foto de la mascota.
3. `actualizacion-rendimiento.sql`: procedures que filtran citas en la base.

`instalar-desde-cero.sql` se genera uniendo los archivos del primer bloque (1 → 4): si alguno cambia, hay que volver a generarlo.

## Estándares a seguir para los DATATYPES
- Los atributos booleanos son TINYINT(1)
- ENUM: ENUM('...', '...', ...)
- descripciones: VARCHAR2(200)
- nombres y apellidos: VARCHAR2(40)
- DNI: VARCHAR2(8)
- Valores double de Java: DECIMAL

- Se utiliza el concrete table inheritance para la creación de tablas

## Cómo abrir el diagrama
1. Descargar el diagrama
2. Abrir a MySQL Workbench
3. Ir a file / Open Model y abrir el archivo
 
 
## Sobre el ddl 
- Se generó el ddl en base al DER con la función Forward Engineer SQL Script de MySQL Workbench 

## Sobre los procedures
- los archivos .sql de los procedures deben llamarse procedimientos-nombre_tabla.sql 

## Registro con verificación de correo y recuperación de contraseña
- Tablas nuevas: `registro_pendiente` y `token_recuperacion` (ya están en `patitasarriba-mysql.sql`).
- Procedures nuevos: `procedures-registro_pendiente.sql` y `procedures-token_recuperacion.sql`.
- `actualizacion-acceso.sql` junta tablas y procedures **solo para una base ya instalada**: ejecutarlo una vez. Se genera a partir de los tres archivos anteriores; si estos cambian hay que volver a generarlo.
- Solo agrega: no modifica ninguna tabla ni procedure existente.

## Foto de la mascota
- La tabla `mascota` tiene la columna `foto` (MEDIUMTEXT, opcional): la imagen como texto `data:image/jpeg;base64,...`.
- `insertar_mascota` y `modificar_mascota` reciben `p_foto` (después de `p_raza`).
- `actualizacion-foto-mascota.sql` es para una base **ya instalada**: ejecutarlo **una sola vez y antes de correr el backend nuevo**; sin él, leer o guardar una mascota falla. Se genera a partir de `patitasarriba-mysql.sql` y `procedures-mascota.sql`.

## Rendimiento (citas filtradas en la base)
- Procedures nuevos en `procedures-cita.sql`: `listar_cita_por_cliente` (Mis citas) y `listar_cita_por_rango` (citas de un día, agenda del veterinario y revisión de cruces al registrar una cita). Antes el backend traía **todas** las citas y las filtraba en Java.
- `actualizacion-rendimiento.sql` es para una base **ya instalada** (la local o la de AWS): ejecutarlo **una sola vez y antes de correr el backend nuevo**; sin él fallan Mis citas, Mi agenda y el registro de citas. Solo agrega procedures. Se genera a partir de `procedures-cita.sql`.
