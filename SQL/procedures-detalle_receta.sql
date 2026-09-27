USE mydb;

DROP PROCEDURE IF EXISTS insertar_detalle_receta;
DROP PROCEDURE IF EXISTS modificar_detalle_receta;
DROP PROCEDURE IF EXISTS eliminar_detalle_receta;
DROP PROCEDURE IF EXISTS buscar_detalle_receta_por_id;
DROP PROCEDURE IF EXISTS listar_detalles_receta;
DROP PROCEDURE IF EXISTS listar_detalles_por_receta;
DROP PROCEDURE IF EXISTS eliminar_detalles_por_receta;

DELIMITER //

CREATE PROCEDURE insertar_detalle_receta (
    IN p_id_receta INT,
    IN p_id_articulo INT,
    IN p_dosis VARCHAR(45),
    IN p_frecuencia VARCHAR(45),
    IN p_duracion_dias INT,
    IN p_cantidad_total INT,
    OUT p_id INT
)
BEGIN
    INSERT INTO detalle_receta (
        id_receta,
        id_articulo,
        dosis,
        frecuencia,
        duracion_dias,
        cantidad_total
    )
    VALUES (
        p_id_receta,
        p_id_articulo,
        p_dosis,
        p_frecuencia,
        p_duracion_dias,
        p_cantidad_total
    );

    SET p_id = LAST_INSERT_ID();
END //

CREATE PROCEDURE modificar_detalle_receta (
    IN p_id_receta INT,
    IN p_id_articulo INT,
    IN p_dosis VARCHAR(45),
    IN p_frecuencia VARCHAR(45),
    IN p_duracion_dias INT,
    IN p_cantidad_total INT,
    IN p_id INT
)
BEGIN
    UPDATE detalle_receta 
    SET 
        id_receta = p_id_receta,
        id_articulo = p_id_articulo,
        dosis = p_dosis,
        frecuencia = p_frecuencia,
        duracion_dias = p_duracion_dias,
        cantidad_total = p_cantidad_total
    WHERE id_detalle_receta = p_id;
END //

CREATE PROCEDURE eliminar_detalle_receta(IN p_id INT)
BEGIN
    DELETE FROM detalle_receta
    WHERE id_detalle_receta = p_id;
END //

CREATE PROCEDURE buscar_detalle_receta_por_id(IN p_id INT)
BEGIN
    SELECT *
    FROM detalle_receta
    WHERE id_detalle_receta = p_id;
END //

CREATE PROCEDURE listar_detalles_receta()
BEGIN
    SELECT *
    FROM detalle_receta;
END //

CREATE PROCEDURE listar_detalles_por_receta(IN p_id_receta INT)
BEGIN
    SELECT *
    FROM detalle_receta
    WHERE id_receta = p_id_receta;
END //

CREATE PROCEDURE eliminar_detalles_por_receta(IN p_id_receta INT)
BEGIN
    DELETE FROM detalle_receta
    WHERE id_receta = p_id_receta;
END //

DELIMITER ;