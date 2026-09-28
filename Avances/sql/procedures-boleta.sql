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
    IN p_metodo_pago ENUM('Efectivo', 'Tarjeta de credito'),
    OUT p_id INT
)
BEGIN
    INSERT INTO BOLETA (
        ID_CLIENTE,
        ACTIVO,
        FECHA,
        TOTAL,
        METODO_PAGO
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
    IN p_metodo_pago ENUM('Efectivo', 'Tarjeta de credito'),
    IN p_id INT
)
BEGIN
    UPDATE BOLETA
    SET
        ID_CLIENTE = p_id_cliente,
        ACTIVO = p_activo,
        FECHA = p_fecha,
        TOTAL = p_total,
        METODO_PAGO = p_metodo_pago
    WHERE ID_BOLETA = p_id;
END //

CREATE PROCEDURE eliminar_boleta(IN p_id INT)
BEGIN
    DELETE FROM BOLETA
    WHERE ID_BOLETA = p_id;
END //

CREATE PROCEDURE buscar_boleta_por_id(IN p_id INT)
BEGIN
    SELECT *
    FROM BOLETA
    WHERE ID_BOLETA = p_id;
END //

CREATE PROCEDURE listar_boletas()
BEGIN
    SELECT *
    FROM BOLETA;
END //

DELIMITER ;
