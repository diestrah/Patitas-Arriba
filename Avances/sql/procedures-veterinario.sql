USE mydb;

DROP PROCEDURE IF EXISTS insertar_veterinario;
DROP PROCEDURE IF EXISTS modificar_veterinario;
DROP PROCEDURE IF EXISTS eliminar_veterinario;
DROP PROCEDURE IF EXISTS buscar_veterinario_por_id;
DROP PROCEDURE IF EXISTS buscar_veterinario_por_dni;
DROP PROCEDURE IF EXISTS listar_veterinarios;

DELIMITER //

CREATE PROCEDURE insertar_veterinario(
    IN p_id_cuenta INT,
    IN p_activo TINYINT(1),
    IN p_dni VARCHAR(8),
    IN p_nombres VARCHAR(45),
    IN p_apellido_paterno VARCHAR(45),
    IN p_apellido_materno VARCHAR(45),
    IN p_telefono VARCHAR(9),
    IN p_numero_colegiatura VARCHAR(45),
    OUT p_id INT
)
BEGIN
    INSERT INTO veterinario (
        id_cuenta, activo, dni, nombres, apellido_paterno,
        apellido_materno, telefono, numero_colegiatura
    )
    VALUES (
        p_id_cuenta, p_activo, p_dni, p_nombres, p_apellido_paterno,
        p_apellido_materno, p_telefono, p_numero_colegiatura
    );
    SET p_id = LAST_INSERT_ID();
END //

CREATE PROCEDURE modificar_veterinario(
    IN p_id_cuenta INT,
    IN p_activo TINYINT(1),
    IN p_dni VARCHAR(8),
    IN p_nombres VARCHAR(45),
    IN p_apellido_paterno VARCHAR(45),
    IN p_apellido_materno VARCHAR(45),
    IN p_telefono VARCHAR(9),
    IN p_numero_colegiatura VARCHAR(45),
    IN p_id INT
)
BEGIN
    UPDATE veterinario
    SET id_cuenta = p_id_cuenta,
        activo = p_activo,
        dni = p_dni,
        nombres = p_nombres,
        apellido_paterno = p_apellido_paterno,
        apellido_materno = p_apellido_materno,
        telefono = p_telefono,
        numero_colegiatura = p_numero_colegiatura
    WHERE id_veterinario = p_id;
END //

CREATE PROCEDURE eliminar_veterinario(IN p_id INT)
BEGIN
    DELETE FROM veterinario WHERE id_veterinario = p_id;
END //

CREATE PROCEDURE buscar_veterinario_por_id(IN p_id INT)
BEGIN
    SELECT * FROM veterinario WHERE id_veterinario = p_id;
END //

CREATE PROCEDURE buscar_veterinario_por_dni(IN p_dni VARCHAR(8))
BEGIN
    SELECT * FROM veterinario WHERE dni = p_dni;
END //

CREATE PROCEDURE listar_veterinarios()
BEGIN
    SELECT * FROM veterinario;
END //

DELIMITER ;
