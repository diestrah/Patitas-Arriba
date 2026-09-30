USE mydb;

DROP PROCEDURE IF EXISTS insertar_cuenta;
DROP PROCEDURE IF EXISTS modificar_cuenta;
DROP PROCEDURE IF EXISTS eliminar_cuenta;
DROP PROCEDURE IF EXISTS buscar_cuenta_por_id;
DROP PROCEDURE IF EXISTS buscar_cuenta_por_nombre_usuario;
DROP PROCEDURE IF EXISTS listar_cuentas;

DELIMITER //

CREATE PROCEDURE insertar_cuenta(
    IN p_activo TINYINT(1),
    IN p_password VARCHAR(60),
    IN p_correo VARCHAR(100),
    IN p_fecha_creacion DATE,
    IN p_nombre_usuario VARCHAR(45),
    OUT p_id INT
)
BEGIN
    INSERT INTO CUENTA (
        ACTIVO,
        PASSWORD,
        CORREO,
        FECHA_CREACION,
        NOMBRE_USUARIO
    )
    VALUES (
        p_activo,
        p_password,
        p_correo,
        p_fecha_creacion,
        p_nombre_usuario
    );

    SET p_id = LAST_INSERT_ID();
END //

CREATE PROCEDURE modificar_cuenta(
    IN p_activo TINYINT(1),
    IN p_password VARCHAR(60),
    IN p_correo VARCHAR(100),
    IN p_fecha_creacion DATE,
    IN p_nombre_usuario VARCHAR(45),
    IN p_id INT
)
BEGIN
    UPDATE CUENTA
    SET
        ACTIVO = p_activo,
        PASSWORD = p_password,
        CORREO = p_correo,
        FECHA_CREACION = p_fecha_creacion,
        NOMBRE_USUARIO = p_nombre_usuario
    WHERE ID_CUENTA = p_id;
END //

CREATE PROCEDURE eliminar_cuenta(IN p_id INT)
BEGIN
    DELETE FROM CUENTA
    WHERE ID_CUENTA = p_id;
END //

CREATE PROCEDURE buscar_cuenta_por_id(IN p_id INT)
BEGIN
    SELECT *
    FROM CUENTA
    WHERE ID_CUENTA = p_id;
END //

CREATE PROCEDURE buscar_cuenta_por_nombre_usuario(IN p_nombre_usuario VARCHAR(45))
BEGIN
    SELECT *
    FROM CUENTA
    WHERE NOMBRE_USUARIO = p_nombre_usuario;
END //

CREATE PROCEDURE listar_cuentas()
BEGIN
    SELECT *
    FROM CUENTA;
END //

DELIMITER ;
