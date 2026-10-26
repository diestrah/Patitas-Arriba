USE mydb;

DROP PROCEDURE IF EXISTS insertar_boleta;
DROP PROCEDURE IF EXISTS modificar_boleta;
DROP PROCEDURE IF EXISTS eliminar_boleta;
DROP PROCEDURE IF EXISTS buscar_boleta_por_id;
DROP PROCEDURE IF EXISTS listar_boletas;

DELIMITER //

CREATE PROCEDURE insertar_boleta(
    IN p_id_cliente INT,
    IN p_activo TINYINT(1),
    IN p_fecha DATE,
    IN p_total DECIMAL,
    IN p_metodo_pago VARCHAR(45),
    OUT p_id INT
)
BEGIN
    INSERT INTO boleta (
        id_cliente,
        activo,
        fecha,
        total,
        metodo_pago
    )
    VALUES (
        p_id_cliente,
        p_activo,
        p_fecha,
        p_total,
        p_metodo_pago
    );

    SET p_id = LAST_INSERT_ID();
END //

CREATE PROCEDURE modificar_boleta(
    IN p_id_cliente INT,
    IN p_activo TINYINT(1),
    IN p_fecha DATE,
    IN p_total DECIMAL,
    IN p_metodo_pago VARCHAR(45),
    IN p_id INT
)
BEGIN
    UPDATE boleta
    SET
        id_cliente = p_id_cliente,
        activo = p_activo,
        fecha = p_fecha,
        total = p_total,
        metodo_pago = p_metodo_pago
    WHERE id_boleta = p_id;
END //

CREATE PROCEDURE eliminar_boleta(IN p_id INT)
BEGIN
    DELETE FROM boleta
    WHERE id_boleta = p_id;
END //

CREATE PROCEDURE buscar_boleta_por_id(IN p_id INT)
BEGIN
    SELECT *
    FROM boleta
    WHERE id_boleta = p_id;
END //

CREATE PROCEDURE listar_boletas()
BEGIN
    SELECT *
    FROM boleta;
END //

DELIMITER ;
