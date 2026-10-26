-- Procedimientos CRUD para patitasarriba.modelo.usuario.Cuenta

use patitasarriba;

DROP PROCEDURE IF EXISTS insertar_cuenta;
DROP PROCEDURE IF EXISTS modificar_cuenta;
DROP PROCEDURE IF EXISTS eliminar_cuenta;
DROP PROCEDURE IF EXISTS buscar_cuenta_por_id;
DROP PROCEDURE IF EXISTS buscar_cuenta_por_nombre_usuario;
DROP PROCEDURE IF EXISTS buscar_cuenta_por_correo;
DROP PROCEDURE IF EXISTS listar_cuentas;

DELIMITER //

-- Crea una cuenta y devuelve el id generado
CREATE PROCEDURE insertar_cuenta (
    IN p_activo TINYINT(1),
    IN p_password VARCHAR(255),
    IN p_correo VARCHAR(100),
    IN p_fecha_creacion DATE,
    IN p_nombre_usuario VARCHAR(45),
    OUT p_id INT
)
BEGIN
    INSERT INTO cuenta (activo, password, correo, fecha_creacion, nombre_usuario)
    VALUES (p_activo, p_password, p_correo, p_fecha_creacion, p_nombre_usuario);

    SET p_id = LAST_INSERT_ID();
END //

-- Actualiza todos los campos de una cuenta existente
CREATE PROCEDURE modificar_cuenta (
    IN p_activo TINYINT(1),
    IN p_password VARCHAR(255),
    IN p_correo VARCHAR(100),
    IN p_fecha_creacion DATE,
    IN p_nombre_usuario VARCHAR(45),
    IN p_id INT
)
BEGIN
    UPDATE cuenta
    SET activo = p_activo,
        password = p_password,
        correo = p_correo,
        fecha_creacion = p_fecha_creacion,
        nombre_usuario = p_nombre_usuario
    WHERE id_cuenta = p_id;
END //

-- Borrado logico: la cuenta queda inactiva
CREATE PROCEDURE eliminar_cuenta (IN p_id INT)
BEGIN
    UPDATE cuenta
    SET activo = 0
    WHERE id_cuenta = p_id;
END //

-- Solo devuelve la cuenta si esta activa
CREATE PROCEDURE buscar_cuenta_por_id (IN p_id INT)
BEGIN
    SELECT *
    FROM cuenta
    WHERE id_cuenta = p_id
      AND activo = 1;
END //

-- nombre_usuario es UNIQUE: no filtra activo, para detectar tambien las cuentas inactivas
CREATE PROCEDURE buscar_cuenta_por_nombre_usuario (IN p_nombre_usuario VARCHAR(45))
BEGIN
    SELECT *
    FROM cuenta
    WHERE nombre_usuario = p_nombre_usuario;
END //

-- correo es UNIQUE: no filtra activo, por la misma razon
CREATE PROCEDURE buscar_cuenta_por_correo (IN p_correo VARCHAR(100))
BEGIN
    SELECT *
    FROM cuenta
    WHERE correo = p_correo;
END //

-- Solo lista las cuentas activas (respeta el borrado logico)
CREATE PROCEDURE listar_cuentas ()
BEGIN
    SELECT *
    FROM cuenta
    WHERE activo = 1;
END //

DELIMITER ;
