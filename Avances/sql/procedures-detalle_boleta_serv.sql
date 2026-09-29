USE mydb;

DROP PROCEDURE IF EXISTS insertar_detalle_boleta_serv;
DROP PROCEDURE IF EXISTS modificar_detalle_boleta_serv;
DROP PROCEDURE IF EXISTS eliminar_detalle_boleta_serv;
DROP PROCEDURE IF EXISTS buscar_detalle_boleta_serv_por_id;
DROP PROCEDURE IF EXISTS listar_detalles_boleta_serv;
DROP PROCEDURE IF EXISTS listar_detalles_boleta_serv_por_boleta;
DROP PROCEDURE IF EXISTS eliminar_detalles_boleta_serv_por_boleta;

DELIMITER //

CREATE PROCEDURE insertar_detalle_boleta_serv(
    IN p_id_boleta INT,
    IN p_id_servicio INT,
    IN p_activo TINYINT(1),
    IN p_cantidad INT,
    IN p_subtotal DECIMAL,
    OUT p_id INT
)
BEGIN
    INSERT INTO DETALLE_BOLETA_SERV (
        ID_BOLETA,
        ID_SERVICIO,
        ACTIVO,
        CANTIDAD,
        SUBTOTAL
    )
    VALUES (
        p_id_boleta,
        p_id_servicio,
        p_activo,
        p_cantidad,
        p_subtotal
    );

    SET p_id = LAST_INSERT_ID();
END //

CREATE PROCEDURE modificar_detalle_boleta_serv(
    IN p_id_boleta INT,
    IN p_id_servicio INT,
    IN p_activo TINYINT(1),
    IN p_cantidad INT,
    IN p_subtotal DECIMAL,
    IN p_id INT
)
BEGIN
    UPDATE DETALLE_BOLETA_SERV
    SET
        ID_BOLETA = p_id_boleta,
        ID_SERVICIO = p_id_servicio,
        ACTIVO = p_activo,
        CANTIDAD = p_cantidad,
        SUBTOTAL = p_subtotal
    WHERE ID_DETALLE_SERV = p_id;
END //

CREATE PROCEDURE eliminar_detalle_boleta_serv(IN p_id INT)
BEGIN
    DELETE FROM DETALLE_BOLETA_SERV
    WHERE ID_DETALLE_SERV = p_id;
END //

CREATE PROCEDURE buscar_detalle_boleta_serv_por_id(IN p_id INT)
BEGIN
    SELECT *
    FROM DETALLE_BOLETA_SERV
    WHERE ID_DETALLE_SERV = p_id;
END //

CREATE PROCEDURE listar_detalles_boleta_serv()
BEGIN
    SELECT *
    FROM DETALLE_BOLETA_SERV;
END //

CREATE PROCEDURE listar_detalles_boleta_serv_por_boleta(IN p_id_boleta INT)
BEGIN
    SELECT *
    FROM DETALLE_BOLETA_SERV
    WHERE ID_BOLETA = p_id_boleta;
END //

CREATE PROCEDURE eliminar_detalles_boleta_serv_por_boleta(IN p_id_boleta INT)
BEGIN
    DELETE FROM DETALLE_BOLETA_SERV
    WHERE ID_BOLETA = p_id_boleta;
END //

DELIMITER ;
