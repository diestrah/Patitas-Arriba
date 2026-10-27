-- Vacia todas las tablas (conserva la estructura y los procedures) y reinicia los AUTO_INCREMENT.
-- Orden: primero las tablas hijas; las llaves foraneas se desactivan mientras se vacian.

USE patitasarriba;
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE detalle_boleta_art;
TRUNCATE TABLE detalle_boleta_serv;
TRUNCATE TABLE insumo_utilizado;
TRUNCATE TABLE atencion_diagnostico;
TRUNCATE TABLE detalle_receta;
TRUNCATE TABLE receta;
TRUNCATE TABLE atencion;
TRUNCATE TABLE detalle_cita;
TRUNCATE TABLE cita;
TRUNCATE TABLE boleta;
TRUNCATE TABLE articulo_categoria;
TRUNCATE TABLE horario_veterinario;
TRUNCATE TABLE horario_administrador;
TRUNCATE TABLE mascota;
TRUNCATE TABLE cliente;
TRUNCATE TABLE veterinario;
TRUNCATE TABLE administrador;
TRUNCATE TABLE cuenta;
TRUNCATE TABLE horario;
TRUNCATE TABLE diagnostico;
TRUNCATE TABLE servicio;
TRUNCATE TABLE categoria;
TRUNCATE TABLE articulo;

SET FOREIGN_KEY_CHECKS = 1;
