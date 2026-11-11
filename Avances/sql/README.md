# Diagrama Lógico en MySQL ⚙️

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
