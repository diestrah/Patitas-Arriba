USE mydb;

DROP PROCEDURE IF EXISTS insertar_administrador;
DROP PROCEDURE IF EXISTS modificar_administrador;
DROP PROCEDURE IF EXISTS eliminar_administrador;
DROP PROCEDURE IF EXISTS buscar_administrador_por_id;
DROP PROCEDURE IF EXISTS buscar_administrador_por_dni;
DROP PROCEDURE IF EXISTS listar_administradores;

DELIMITER //

CREATE PROCEDURE insertar_administrador(
    IN p_id_cuenta INT,
    IN p_activo TINYINT(1),
    IN p_dni VARCHAR(8),
    IN p_nombres VARCHAR(45),
    IN p_apellido_paterno VARCHAR(45),
    IN p_apellido_materno VARCHAR(45),
    IN p_telefono VARCHAR(9),
    OUT p_id INT
)
BEGIN
    INSERT INTO administrador (
        id_cuenta, activo, dni, nombres, apellido_paterno,
        apellido_materno, telefono
    )
    VALUES (
        p_id_cuenta, p_activo, p_dni, p_nombres, p_apellido_paterno,
        p_apellido_materno, p_telefono
    );
    SET p_id = LAST_INSERT_ID();
END //

CREATE PROCEDURE modificar_administrador(
    IN p_id_cuenta INT,
    IN p_activo TINYINT(1),
    IN p_dni VARCHAR(8),
    IN p_nombres VARCHAR(45),
    IN p_apellido_paterno VARCHAR(45),
    IN p_apellido_materno VARCHAR(45),
    IN p_telefono VARCHAR(9),
    IN p_id INT
)
BEGIN
    UPDATE administrador
    SET id_cuenta = p_id_cuenta,
        activo = p_activo,
        dni = p_dni,
        nombres = p_nombres,
        apellido_paterno = p_apellido_paterno,
        apellido_materno = p_apellido_materno,
        telefono = p_telefono
    WHERE id_administrador = p_id;
END //

CREATE PROCEDURE eliminar_administrador(IN p_id INT)
BEGIN
    DELETE FROM administrador WHERE id_administrador = p_id;
END //

CREATE PROCEDURE buscar_administrador_por_id(IN p_id INT)
BEGIN
    SELECT * FROM administrador WHERE id_administrador = p_id;
END //

CREATE PROCEDURE buscar_administrador_por_dni(IN p_dni VARCHAR(8))
BEGIN
    SELECT * FROM administrador WHERE dni = p_dni;
END //

CREATE PROCEDURE listar_administradores()
BEGIN
    SELECT * FROM administrador;
END //

DELIMITER ;
