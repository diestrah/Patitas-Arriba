USE mydb;

DROP PROCEDURE IF EXISTS insertar_receta;
DROP PROCEDURE IF EXISTS modificar_receta;
DROP PROCEDURE IF EXISTS eliminar_receta;
DROP PROCEDURE IF EXISTS buscar_receta_por_id;
DROP PROCEDURE IF EXISTS listar_recetas;

DELIMITER //

CREATE PROCEDURE insertar_receta(
    IN p_id_atencion_medica INT,
    IN p_fecha_emision DATE,
    IN p_indicaciones_generales VARCHAR(500),
    IN p_estado TINYINT(1),
    OUT p_id INT
)
BEGIN
    INSERT INTO receta (
        id_atencion_medica,
        fecha_emision,
        indicaciones_generales,
        estado
    )
    VALUES (
        p_id_atencion_medica,
        p_fecha_emision,
        p_indicaciones_generales,
        p_estado
    );

    SET p_id = LAST_INSERT_ID();
END //

CREATE PROCEDURE modificar_receta(
    IN p_id_atencion_medica INT,
    IN p_fecha_emision DATE,
    IN p_indicaciones_generales VARCHAR(500),
    IN p_estado TINYINT(1),
    IN p_id INT
)
BEGIN
    UPDATE receta
    SET 
        id_atencion_medica = p_id_atencion_medica,
        fecha_emision = p_fecha_emision,
        indicaciones_generales = p_indicaciones_generales,
        estado = p_estado
    WHERE id_receta = p_id;
END //

CREATE PROCEDURE eliminar_receta(IN p_id INT)
BEGIN
    DELETE FROM receta
    WHERE id_receta = p_id;
END //

CREATE PROCEDURE buscar_receta_por_id(IN p_id INT)
BEGIN
    SELECT *
    FROM receta
    WHERE id_receta = p_id;
END //

CREATE PROCEDURE listar_recetas()
BEGIN
    SELECT *
    FROM receta;
END //
