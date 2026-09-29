-- Procedimientos CRUD para patitasarriba.modelo.atencion.AtencionTratamiento

DROP PROCEDURE IF EXISTS insertar_atencion_tratamiento;
DROP PROCEDURE IF EXISTS modificar_atencion_tratamiento;
DROP PROCEDURE IF EXISTS eliminar_atencion_tratamiento;
DROP PROCEDURE IF EXISTS buscar_atencion_tratamiento_por_id;
DROP PROCEDURE IF EXISTS listar_atencion_tratamiento_por_atencion_medica;

DELIMITER //

-- Crea un tratamiento asociado a una atencion medica y devuelve el id generado
CREATE PROCEDURE insertar_atencion_tratamiento (
    IN p_id_atencion_medica INT,
    IN p_id_tratamiento INT,
    IN p_activo TINYINT(1),
    OUT p_id INT
)
BEGIN
    INSERT INTO atencion_tratamiento (id_atencion_medica, id_tratamiento, activo)
    VALUES (p_id_atencion_medica, p_id_tratamiento, p_activo);

    SET p_id = LAST_INSERT_ID();
END //

-- Actualiza un tratamiento de atencion existente (id_atencion_medica no cambia: un tratamiento no se muda de atencion)
CREATE PROCEDURE modificar_atencion_tratamiento (
    IN p_id INT,
    IN p_id_tratamiento INT,
    IN p_activo TINYINT(1)
)
BEGIN
    UPDATE atencion_tratamiento
    SET id_tratamiento = p_id_tratamiento,
        activo = p_activo
    WHERE id_atencion_tratamiento = p_id;
END //

CREATE PROCEDURE eliminar_atencion_tratamiento (IN p_id INT)
BEGIN
    DELETE FROM atencion_tratamiento
    WHERE id_atencion_tratamiento = p_id;
END //

CREATE PROCEDURE buscar_atencion_tratamiento_por_id (IN p_id INT)
BEGIN
    SELECT *
    FROM atencion_tratamiento
    WHERE id_atencion_tratamiento = p_id;
END //

-- Lista los tratamientos de UNA atencion medica especifica (patron maestro-detalle)
CREATE PROCEDURE listar_atencion_tratamiento_por_atencion_medica (IN p_id_atencion_medica INT)
BEGIN
    SELECT *
    FROM atencion_tratamiento
    WHERE id_atencion_medica = p_id_atencion_medica;
END //

DELIMITER ;
