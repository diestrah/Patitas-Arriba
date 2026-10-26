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
    INSERT INTO detalle_boleta_serv (
        id_boleta,
        id_servicio,
        activo,
        cantidad,
        subtotal
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
    UPDATE detalle_boleta_serv
    SET
        id_boleta = p_id_boleta,
        id_servicio = p_id_servicio,
        activo = p_activo,
        cantidad = p_cantidad,
        subtotal = p_subtotal
    WHERE id_detalle_serv = p_id;
END //

CREATE PROCEDURE eliminar_detalle_boleta_serv(IN p_id INT)
BEGIN
    DELETE FROM detalle_boleta_serv
    WHERE id_detalle_serv = p_id;
END //

CREATE PROCEDURE buscar_detalle_boleta_serv_por_id(IN p_id INT)
BEGIN
    SELECT *
    FROM detalle_boleta_serv
    WHERE id_detalle_serv = p_id;
END //

CREATE PROCEDURE listar_detalles_boleta_serv()
BEGIN
    SELECT *
    FROM detalle_boleta_serv;
END //

CREATE PROCEDURE listar_detalles_boleta_serv_por_boleta(IN p_id_boleta INT)
BEGIN
    SELECT *
    FROM detalle_boleta_serv
    WHERE id_boleta = p_id_boleta;
END //

CREATE PROCEDURE eliminar_detalles_boleta_serv_por_boleta(IN p_id_boleta INT)
BEGIN
    DELETE FROM detalle_boleta_serv
    WHERE id_boleta = p_id_boleta;
END //

DELIMITER ;
