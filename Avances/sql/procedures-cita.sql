-- Procedimientos CRUD para patitasarriba.modelo.cita.Cita
-- Los servicios de la cita (detalles) se manejan en procedures-detalle_cita.sql

use patitasarriba;

DROP PROCEDURE IF EXISTS insertar_cita;
DROP PROCEDURE IF EXISTS modificar_cita;
DROP PROCEDURE IF EXISTS eliminar_cita;
DROP PROCEDURE IF EXISTS buscar_cita_por_id;
DROP PROCEDURE IF EXISTS listar_cita;
DROP PROCEDURE IF EXISTS listar_cita_por_mascota;

DELIMITER //

-- Crea una cita y devuelve el id generado.
-- p_id_administrador puede ser NULL: lo asigna quien aprueba o rechaza la cita
CREATE PROCEDURE insertar_cita (
    IN p_fecha_hora DATETIME,
    IN p_estado VARCHAR(20),
    IN p_id_mascota INT,
    IN p_id_administrador INT,
    IN p_es_emergencia TINYINT(1),
    IN p_activo TINYINT(1),
    OUT p_id INT
)
BEGIN
    INSERT INTO cita (fecha_hora, estado, id_mascota, id_administrador, es_emergencia, activo)
    VALUES (p_fecha_hora, p_estado, p_id_mascota, p_id_administrador, p_es_emergencia, p_activo);

    SET p_id = LAST_INSERT_ID();
END //

-- Actualiza todos los campos de una cita existente
CREATE PROCEDURE modificar_cita (
    IN p_id INT,
    IN p_fecha_hora DATETIME,
    IN p_estado VARCHAR(20),
    IN p_id_mascota INT,
    IN p_id_administrador INT,
    IN p_es_emergencia TINYINT(1),
    IN p_activo TINYINT(1)
)
BEGIN
    UPDATE cita
    SET fecha_hora = p_fecha_hora,
        estado = p_estado,
        id_mascota = p_id_mascota,
        id_administrador = p_id_administrador,
        es_emergencia = p_es_emergencia,
        activo = p_activo
    WHERE id_cita = p_id;
END //

-- Borrado logico: la cita queda inactiva
-- (cancelar una cita es otra cosa: se hace con modificar_cita, estado = 'CANCELADO')
CREATE PROCEDURE eliminar_cita (IN p_id INT)
BEGIN
    UPDATE cita
    SET activo = 0
    WHERE id_cita = p_id;
END //

-- Solo devuelve la cita si esta activa
CREATE PROCEDURE buscar_cita_por_id (IN p_id INT)
BEGIN
    SELECT *
    FROM cita
    WHERE id_cita = p_id
      AND activo = 1;
END //

-- Solo lista las citas activas (respeta el borrado logico)
CREATE PROCEDURE listar_cita ()
BEGIN
    SELECT *
    FROM cita
    WHERE activo = 1;
END //

-- Citas activas de una mascota (util para la regla "no eliminar una mascota con citas activas")
CREATE PROCEDURE listar_cita_por_mascota (IN p_id_mascota INT)
BEGIN
    SELECT *
    FROM cita
    WHERE id_mascota = p_id_mascota
      AND activo = 1;
END //

DELIMITER ;
