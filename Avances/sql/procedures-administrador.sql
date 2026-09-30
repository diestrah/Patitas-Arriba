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
    INSERT INTO ADMINISTRADOR (
        ID_CUENTA, ACTIVO, DNI, NOMBRES, APELLIDO_PATERNO,
        APELLIDO_MATERNO, TELEFONO
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
    UPDATE ADMINISTRADOR
    SET ID_CUENTA = p_id_cuenta,
        ACTIVO = p_activo,
        DNI = p_dni,
        NOMBRES = p_nombres,
        APELLIDO_PATERNO = p_apellido_paterno,
        APELLIDO_MATERNO = p_apellido_materno,
        TELEFONO = p_telefono
    WHERE ID_ADMINISTRADOR = p_id;
END //

CREATE PROCEDURE eliminar_administrador(IN p_id INT)
BEGIN
    DELETE FROM ADMINISTRADOR WHERE ID_ADMINISTRADOR = p_id;
END //

CREATE PROCEDURE buscar_administrador_por_id(IN p_id INT)
BEGIN
    SELECT * FROM ADMINISTRADOR WHERE ID_ADMINISTRADOR = p_id;
END //

CREATE PROCEDURE buscar_administrador_por_dni(IN p_dni VARCHAR(8))
BEGIN
    SELECT * FROM ADMINISTRADOR WHERE DNI = p_dni;
END //

CREATE PROCEDURE listar_administradores()
BEGIN
    SELECT * FROM ADMINISTRADOR;
END //

DELIMITER ;
