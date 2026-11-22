-- Borra TODAS las tablas del esquema patitasarriba (estructura y datos). No se puede deshacer.
-- Los procedures NO se borran. Para reinstalar: patitasarriba-mysql.sql, luego los procedures-*.sql
-- y despues datos-iniciales.sql / datos-prueba.sql.
-- Para solo vaciar los datos y conservar la estructura, usar limpiar-tablas.sql.
-- Orden: primero las tablas hijas; las llaves foraneas se desactivan mientras se borran.

USE patitasarriba;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS detalle_boleta_art;
DROP TABLE IF EXISTS detalle_boleta_serv;
DROP TABLE IF EXISTS insumo_utilizado;
DROP TABLE IF EXISTS atencion_diagnostico;
DROP TABLE IF EXISTS detalle_receta;
DROP TABLE IF EXISTS receta;
DROP TABLE IF EXISTS atencion;
DROP TABLE IF EXISTS detalle_cita;
DROP TABLE IF EXISTS cita;
DROP TABLE IF EXISTS boleta;
DROP TABLE IF EXISTS articulo_categoria;
DROP TABLE IF EXISTS horario_veterinario;
DROP TABLE IF EXISTS horario_administrador;
DROP TABLE IF EXISTS mascota;
DROP TABLE IF EXISTS cliente;
DROP TABLE IF EXISTS veterinario;
DROP TABLE IF EXISTS administrador;
DROP TABLE IF EXISTS token_recuperacion;
DROP TABLE IF EXISTS registro_pendiente;
DROP TABLE IF EXISTS cuenta;
DROP TABLE IF EXISTS horario;
DROP TABLE IF EXISTS diagnostico;
DROP TABLE IF EXISTS servicio;
DROP TABLE IF EXISTS categoria;
DROP TABLE IF EXISTS articulo;

SET FOREIGN_KEY_CHECKS = 1;
