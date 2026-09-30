-- Procedimientos CRUD para patitasarriba.modelo.cita.DetalleCita

DROP PROCEDURE IF EXISTS insertar_detalle_cita;
DROP PROCEDURE IF EXISTS modificar_detalle_cita;
DROP PROCEDURE IF EXISTS eliminar_detalle_cita;
DROP PROCEDURE IF EXISTS buscar_detalle_cita_por_id;
DROP PROCEDURE IF EXISTS listar_detalle_cita_por_cita;

DELIMITER //

-- Crea un detalle de cita y devuelve el id generado
CREATE PROCEDURE insertar_detalle_cita (
    IN p_id_cita INT,
    IN p_id_servicio INT,
    IN p_observaciones VARCHAR(200),
    IN p_activo TINYINT(1),
    OUT p_id INT
)
BEGIN
    INSERT INTO DETALLE_CITA (id_cita, id_servicio, observaciones, activo)
    VALUES (p_id_cita, p_id_servicio, p_observaciones, p_activo);

    SET p_id = LAST_INSERT_ID();
END //

-- Actualiza un detalle existente (id_cita no cambia: un detalle no se muda de cita)
CREATE PROCEDURE modificar_detalle_cita (
    IN p_id INT,
    IN p_observaciones VARCHAR(200),
    IN p_id_servicio INT,
    IN p_activo TINYINT(1)
)
BEGIN
    UPDATE DETALLE_CITA
    SET observaciones = p_observaciones,
        id_servicio = p_id_servicio,
        activo = p_activo
    WHERE id_detalle_cita = p_id;
END //

-- Borrado suave: nunca se elimina la fila, solo se desactiva
CREATE PROCEDURE eliminar_detalle_cita (IN p_id INT)
BEGIN
    UPDATE DETALLE_CITA
    SET activo = 0
    WHERE id_detalle_cita = p_id;
END //

CREATE PROCEDURE buscar_detalle_cita_por_id (IN p_id INT)
BEGIN
    SELECT *
    FROM DETALLE_CITA
    WHERE id_detalle_cita = p_id;
END //

-- Lista los detalles de UNA cita especifica (patron maestro-detalle)
CREATE PROCEDURE listar_detalle_cita_por_cita (IN p_id_cita INT)
BEGIN
    SELECT *
    FROM DETALLE_CITA
    WHERE id_cita = p_id_cita AND activo = 1;
END //

DELIMITER ;
