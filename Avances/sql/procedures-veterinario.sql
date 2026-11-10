-- Procedimientos CRUD para patitasarriba.modelo.usuario.Veterinario

use patitasarriba;

DROP PROCEDURE IF EXISTS insertar_veterinario;
DROP PROCEDURE IF EXISTS modificar_veterinario;
DROP PROCEDURE IF EXISTS eliminar_veterinario;
DROP PROCEDURE IF EXISTS buscar_veterinario_por_id;
DROP PROCEDURE IF EXISTS buscar_veterinario_por_dni;
DROP PROCEDURE IF EXISTS buscar_veterinario_por_telefono;
DROP PROCEDURE IF EXISTS buscar_veterinario_por_colegiatura;
DROP PROCEDURE IF EXISTS buscar_veterinario_por_cuenta;
DROP PROCEDURE IF EXISTS listar_veterinarios;

DELIMITER //

-- Crea un veterinario y devuelve el id generado
CREATE PROCEDURE insertar_veterinario (
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

-- Actualiza todos los campos de un veterinario existente
CREATE PROCEDURE modificar_veterinario (
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

-- Borrado logico: el veterinario queda inactivo
CREATE PROCEDURE eliminar_veterinario (IN p_id INT)
BEGIN
    UPDATE veterinario
    SET activo = 0
    WHERE id_veterinario = p_id;
END //

-- Solo devuelve el veterinario si esta activo
CREATE PROCEDURE buscar_veterinario_por_id (IN p_id INT)
BEGIN
    SELECT *
    FROM veterinario
    WHERE id_veterinario = p_id
      AND activo = 1;
END //

-- dni es UNIQUE: no filtra activo, para detectar tambien los veterinarios inactivos
CREATE PROCEDURE buscar_veterinario_por_dni (IN p_dni VARCHAR(8))
BEGIN
    SELECT *
    FROM veterinario
    WHERE dni = p_dni;
END //

-- telefono es UNIQUE: no filtra activo, por la misma razon
CREATE PROCEDURE buscar_veterinario_por_telefono (IN p_telefono VARCHAR(9))
BEGIN
    SELECT *
    FROM veterinario
    WHERE telefono = p_telefono;
END //

-- numero_colegiatura es UNIQUE: no filtra activo, por la misma razon
CREATE PROCEDURE buscar_veterinario_por_colegiatura (IN p_numero_colegiatura VARCHAR(45))
BEGIN
    SELECT *
    FROM veterinario
    WHERE numero_colegiatura = p_numero_colegiatura;
END //

-- id_cuenta es UNIQUE: sirve para la regla "no eliminar una cuenta con un veterinario
-- activo asociado". No filtra activo
CREATE PROCEDURE buscar_veterinario_por_cuenta (IN p_id_cuenta INT)
BEGIN
    SELECT *
    FROM veterinario
    WHERE id_cuenta = p_id_cuenta;
END //

-- Solo lista los veterinarios activos (respeta el borrado logico)
CREATE PROCEDURE listar_veterinarios ()
BEGIN
    SELECT *
    FROM veterinario
    WHERE activo = 1;
END //

DELIMITER ;
