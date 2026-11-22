-- Procedimientos CRUD para patitasarriba.modelo.atencion.Atencion
-- Los insumos y diagnosticos de la atencion se manejan en procedures-insumo_utilizado.sql
-- y procedures-atencion_diagnostico.sql

use patitasarriba;

DROP PROCEDURE IF EXISTS insertar_atencion;
DROP PROCEDURE IF EXISTS modificar_atencion;
DROP PROCEDURE IF EXISTS eliminar_atencion;
DROP PROCEDURE IF EXISTS buscar_atencion_por_id;
DROP PROCEDURE IF EXISTS buscar_atencion_por_detalle_cita;
DROP PROCEDURE IF EXISTS listar_atenciones;
DROP PROCEDURE IF EXISTS listar_atenciones_por_mascota;

DELIMITER //

-- Registra la ejecucion de un servicio de la cita (detalle_cita) y devuelve el id generado.
-- p_motivo_consulta puede ser NULL (servicios esteticos)
CREATE PROCEDURE insertar_atencion (
    IN p_id_detalle_cita INT,
    IN p_fecha_hora DATETIME,
    IN p_motivo_consulta VARCHAR(200),
    IN p_observaciones VARCHAR(500),
    IN p_activo TINYINT(1),
    OUT p_id INT
)
BEGIN
    INSERT INTO atencion (
        id_detalle_cita, fecha_hora, motivo_consulta, observaciones, activo
    )
    VALUES (
        p_id_detalle_cita, p_fecha_hora, p_motivo_consulta, p_observaciones, p_activo
    );

    SET p_id = LAST_INSERT_ID();
END //

-- Actualiza todos los campos de una atencion existente
CREATE PROCEDURE modificar_atencion (
    IN p_id INT,
    IN p_id_detalle_cita INT,
    IN p_fecha_hora DATETIME,
    IN p_motivo_consulta VARCHAR(200),
    IN p_observaciones VARCHAR(500),
    IN p_activo TINYINT(1)
)
BEGIN
    UPDATE atencion
    SET id_detalle_cita = p_id_detalle_cita,
        fecha_hora = p_fecha_hora,
        motivo_consulta = p_motivo_consulta,
        observaciones = p_observaciones,
        activo = p_activo
    WHERE id_atencion = p_id;
END //

-- Borrado logico: la atencion queda inactiva
-- (sus insumos y diagnosticos se desactivan antes, en la misma transaccion del BL)
CREATE PROCEDURE eliminar_atencion (IN p_id INT)
BEGIN
    UPDATE atencion
    SET activo = 0
    WHERE id_atencion = p_id;
END //

-- Solo devuelve la atencion si esta activa
CREATE PROCEDURE buscar_atencion_por_id (IN p_id INT)
BEGIN
    SELECT *
    FROM atencion
    WHERE id_atencion = p_id
      AND activo = 1;
END //

-- id_detalle_cita es UNIQUE: no filtra activo, para detectar tambien las atenciones inactivas
-- (validacion "un servicio se ejecuta en una sola atencion" en el BL) y para completar
-- DetalleCita.atencion
CREATE PROCEDURE buscar_atencion_por_detalle_cita (IN p_id_detalle_cita INT)
BEGIN
    SELECT *
    FROM atencion
    WHERE id_detalle_cita = p_id_detalle_cita;
END //

-- Solo lista las atenciones activas (respeta el borrado logico)
CREATE PROCEDURE listar_atenciones ()
BEGIN
    SELECT *
    FROM atencion
    WHERE activo = 1;
END //

-- Historial clinico: atenciones activas de una mascota (atencion -> detalle_cita -> cita -> mascota)
CREATE PROCEDURE listar_atenciones_por_mascota (IN p_id_mascota INT)
BEGIN
    SELECT a.*
    FROM atencion a
    INNER JOIN detalle_cita dc ON dc.id_detalle_cita = a.id_detalle_cita
    INNER JOIN cita c ON c.id_cita = dc.id_cita
    WHERE c.id_mascota = p_id_mascota
      AND a.activo = 1;
END //

DELIMITER ;
