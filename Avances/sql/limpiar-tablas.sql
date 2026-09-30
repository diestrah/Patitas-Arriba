USE mydb;
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE insumo_utilizado;
TRUNCATE TABLE detalle_receta;
TRUNCATE TABLE atencion_tratamiento;
TRUNCATE TABLE atencion_diagnostico;
TRUNCATE TABLE receta;
TRUNCATE TABLE detalle_boleta_serv;
TRUNCATE TABLE detalle_cita;
TRUNCATE TABLE atencion_medica;
TRUNCATE TABLE detalle_boleta_art;
TRUNCATE TABLE cita;
TRUNCATE TABLE horario_veterinario;
TRUNCATE TABLE horario_administrador;
TRUNCATE TABLE boleta;
TRUNCATE TABLE mascota;
TRUNCATE TABLE articulo;
TRUNCATE TABLE veterinario;
TRUNCATE TABLE administrador;
TRUNCATE TABLE cliente;
TRUNCATE TABLE tratamiento;
TRUNCATE TABLE diagnostico;
TRUNCATE TABLE servicio;
TRUNCATE TABLE categoria_articulo;
TRUNCATE TABLE horario;
TRUNCATE TABLE cuenta;

SET FOREIGN_KEY_CHECKS = 1;
