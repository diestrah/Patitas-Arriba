USE mydb;

DROP PROCEDURE IF EXISTS insertar_cliente;
DROP PROCEDURE IF EXISTS modificar_cliente;
DROP PROCEDURE IF EXISTS eliminar_cliente;
DROP PROCEDURE IF EXISTS buscar_cliente_por_id;
DROP PROCEDURE IF EXISTS buscar_cliente_por_dni;
DROP PROCEDURE IF EXISTS listar_clientes;

DELIMITER //

CREATE PROCEDURE insertar_cliente(
    IN p_id_cuenta INT,
    IN p_activo TINYINT(1),
    IN p_dni VARCHAR(8),
    IN p_nombres VARCHAR(50),
    IN p_apellido_paterno VARCHAR(45),
    IN p_apellido_materno VARCHAR(45),
    IN p_telefono VARCHAR(9),
    OUT p_id INT
)
BEGIN
    INSERT INTO CLIENTE (
        ID_CUENTA, ACTIVO, DNI, NOMBRES, APELLIDO_PATERNO,
        APELLIDO_MATERNO, TELEFONO
    )
    VALUES (
        p_id_cuenta, p_activo, p_dni, p_nombres, p_apellido_paterno,
        p_apellido_materno, p_telefono
    );
    SET p_id = LAST_INSERT_ID();
END //

CREATE PROCEDURE modificar_cliente(
    IN p_id_cuenta INT,
    IN p_activo TINYINT(1),
    IN p_dni VARCHAR(8),
    IN p_nombres VARCHAR(50),
    IN p_apellido_paterno VARCHAR(45),
    IN p_apellido_materno VARCHAR(45),
    IN p_telefono VARCHAR(9),
    IN p_id INT
)
BEGIN
    UPDATE CLIENTE
    SET ID_CUENTA = p_id_cuenta,
        ACTIVO = p_activo,
        DNI = p_dni,
        NOMBRES = p_nombres,
        APELLIDO_PATERNO = p_apellido_paterno,
        APELLIDO_MATERNO = p_apellido_materno,
        TELEFONO = p_telefono
    WHERE ID_CLIENTE = p_id;
END //

CREATE PROCEDURE eliminar_cliente(IN p_id INT)
BEGIN
    DELETE FROM CLIENTE WHERE ID_CLIENTE = p_id;
END //

CREATE PROCEDURE buscar_cliente_por_id(IN p_id INT)
BEGIN
    SELECT * FROM CLIENTE WHERE ID_CLIENTE = p_id;
END //

CREATE PROCEDURE buscar_cliente_por_dni(IN p_dni VARCHAR(8))
BEGIN
    SELECT * FROM CLIENTE WHERE DNI = p_dni;
END //

CREATE PROCEDURE listar_clientes()
BEGIN
    SELECT * FROM CLIENTE;
END //

DELIMITER ;
