-- Procedimientos CRUD para patitasarriba.modelo.cita.DetalleCita

DROP PROCEDURE IF EXISTS insertar_detalle_cita;
DROP PROCEDURE IF EXISTS modificar_detalle_cita;
DROP PROCEDURE IF EXISTS eliminar_detalle_cita;
DROP PROCEDURE IF EXISTS eliminar_detalles_por_cita;
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
    INSERT INTO detalle_cita (id_cita, id_servicio, observaciones, activo)
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
    UPDATE detalle_cita
    SET observaciones = p_observaciones,
        id_servicio = p_id_servicio,
        activo = p_activo
    WHERE id_detalle_cita = p_id;
END //

CREATE PROCEDURE eliminar_detalle_cita (IN p_id INT)
BEGIN
    DELETE FROM detalle_cita
    WHERE id_detalle_cita = p_id;
END //

-- Borra todos los detalles de UNA cita (usado por CitaDAOImpl.delete antes de
-- borrar la cita misma, ya que la FK se mantiene en NO ACTION)
CREATE PROCEDURE eliminar_detalles_por_cita (IN p_id_cita INT)
BEGIN
    DELETE FROM detalle_cita
    WHERE id_cita = p_id_cita;
END //

CREATE PROCEDURE buscar_detalle_cita_por_id (IN p_id INT)
BEGIN
    SELECT *
    FROM detalle_cita
    WHERE id_detalle_cita = p_id;
END //

-- Lista los detalles de UNA cita especifica (patron maestro-detalle)
CREATE PROCEDURE listar_detalle_cita_por_cita (IN p_id_cita INT)
BEGIN
    SELECT *
    FROM detalle_cita
    WHERE id_cita = p_id_cita AND activo = 1;
END //

DELIMITER ;
