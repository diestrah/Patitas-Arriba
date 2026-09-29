USE mydb;

DROP PROCEDURE IF EXISTS insertar_detalle_boleta_art;
DROP PROCEDURE IF EXISTS modificar_detalle_boleta_art;
DROP PROCEDURE IF EXISTS eliminar_detalle_boleta_art;
DROP PROCEDURE IF EXISTS buscar_detalle_boleta_art_por_id;
DROP PROCEDURE IF EXISTS listar_detalles_boleta_art;
DROP PROCEDURE IF EXISTS listar_detalles_boleta_art_por_boleta;
DROP PROCEDURE IF EXISTS eliminar_detalles_boleta_art_por_boleta;

DELIMITER //

CREATE PROCEDURE insertar_detalle_boleta_art(
    IN p_id_boleta INT,
    IN p_id_articulo INT,
    IN p_activo TINYINT(1),
    IN p_cantidad INT,
    IN p_subtotal DECIMAL,
    OUT p_id INT
)
BEGIN
    INSERT INTO DETALLE_BOLETA_ART (
        ID_BOLETA,
        ID_ARTICULO,
        ACTIVO,
        CANTIDAD,
        SUBTOTAL
    )
    VALUES (
        p_id_boleta,
        p_id_articulo,
        p_activo,
        p_cantidad,
        p_subtotal
    );

    SET p_id = LAST_INSERT_ID();
END //

CREATE PROCEDURE modificar_detalle_boleta_art(
    IN p_id_boleta INT,
    IN p_id_articulo INT,
    IN p_activo TINYINT(1),
    IN p_cantidad INT,
    IN p_subtotal DECIMAL,
    IN p_id INT
)
BEGIN
    UPDATE DETALLE_BOLETA_ART
    SET
        ID_BOLETA = p_id_boleta,
        ID_ARTICULO = p_id_articulo,
        ACTIVO = p_activo,
        CANTIDAD = p_cantidad,
        SUBTOTAL = p_subtotal
    WHERE ID_DETALLE_BOLETA_ARTI = p_id;
END //

CREATE PROCEDURE eliminar_detalle_boleta_art(IN p_id INT)
BEGIN
    DELETE FROM DETALLE_BOLETA_ART
    WHERE ID_DETALLE_BOLETA_ARTI = p_id;
END //

CREATE PROCEDURE buscar_detalle_boleta_art_por_id(IN p_id INT)
BEGIN
    SELECT *
    FROM DETALLE_BOLETA_ART
    WHERE ID_DETALLE_BOLETA_ARTI = p_id;
END //

CREATE PROCEDURE listar_detalles_boleta_art()
BEGIN
    SELECT *
    FROM DETALLE_BOLETA_ART;
END //

CREATE PROCEDURE listar_detalles_boleta_art_por_boleta(IN p_id_boleta INT)
BEGIN
    SELECT *
    FROM DETALLE_BOLETA_ART
    WHERE ID_BOLETA = p_id_boleta;
END //

CREATE PROCEDURE eliminar_detalles_boleta_art_por_boleta(IN p_id_boleta INT)
BEGIN
    DELETE FROM DETALLE_BOLETA_ART
    WHERE ID_BOLETA = p_id_boleta;
END //

DELIMITER ;
