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
    INSERT INTO cuenta (
        activo,
        password,
        correo,
        fecha_creacion,
        nombre_usuario
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
    UPDATE cuenta
    SET
        activo = p_activo,
        password = p_password,
        correo = p_correo,
        fecha_creacion = p_fecha_creacion,
        nombre_usuario = p_nombre_usuario
    WHERE id_cuenta = p_id;
END //

CREATE PROCEDURE eliminar_cuenta(IN p_id INT)
BEGIN
    DELETE FROM cuenta
    WHERE id_cuenta = p_id;
END //

CREATE PROCEDURE buscar_cuenta_por_id(IN p_id INT)
BEGIN
    SELECT *
    FROM cuenta
    WHERE id_cuenta = p_id;
END //

CREATE PROCEDURE buscar_cuenta_por_nombre_usuario(IN p_nombre_usuario VARCHAR(45))
BEGIN
    SELECT *
    FROM cuenta
    WHERE nombre_usuario = p_nombre_usuario;
END //

CREATE PROCEDURE listar_cuentas()
BEGIN
    SELECT *
    FROM cuenta;
END //

DELIMITER ;
