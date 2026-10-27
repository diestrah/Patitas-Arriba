-- Procedimientos CRUD para patitasarriba.modelo.usuario.Cliente

use patitasarriba;

DROP PROCEDURE IF EXISTS insertar_cliente;
DROP PROCEDURE IF EXISTS modificar_cliente;
DROP PROCEDURE IF EXISTS eliminar_cliente;
DROP PROCEDURE IF EXISTS buscar_cliente_por_id;
DROP PROCEDURE IF EXISTS buscar_cliente_por_dni;
DROP PROCEDURE IF EXISTS buscar_cliente_por_telefono;
DROP PROCEDURE IF EXISTS buscar_cliente_por_cuenta;
DROP PROCEDURE IF EXISTS listar_clientes;

DELIMITER //

-- Crea un cliente y devuelve el id generado
CREATE PROCEDURE insertar_cliente (
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
    INSERT INTO cliente (
        id_cuenta, activo, dni, nombres, apellido_paterno,
        apellido_materno, telefono
    )
    VALUES (
        p_id_cuenta, p_activo, p_dni, p_nombres, p_apellido_paterno,
        p_apellido_materno, p_telefono
    );

    SET p_id = LAST_INSERT_ID();
END //

-- Actualiza todos los campos de un cliente existente
CREATE PROCEDURE modificar_cliente (
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
    UPDATE cliente
    SET id_cuenta = p_id_cuenta,
        activo = p_activo,
        dni = p_dni,
        nombres = p_nombres,
        apellido_paterno = p_apellido_paterno,
        apellido_materno = p_apellido_materno,
        telefono = p_telefono
    WHERE id_cliente = p_id;
END //

-- Borrado logico: el cliente queda inactivo
CREATE PROCEDURE eliminar_cliente (IN p_id INT)
BEGIN
    UPDATE cliente
    SET activo = 0
    WHERE id_cliente = p_id;
END //

-- Solo devuelve el cliente si esta activo
CREATE PROCEDURE buscar_cliente_por_id (IN p_id INT)
BEGIN
    SELECT *
    FROM cliente
    WHERE id_cliente = p_id
      AND activo = 1;
END //

-- dni es UNIQUE: no filtra activo, para detectar tambien los clientes inactivos
CREATE PROCEDURE buscar_cliente_por_dni (IN p_dni VARCHAR(8))
BEGIN
    SELECT *
    FROM cliente
    WHERE dni = p_dni;
END //

-- telefono es UNIQUE: no filtra activo, por la misma razon
CREATE PROCEDURE buscar_cliente_por_telefono (IN p_telefono VARCHAR(9))
BEGIN
    SELECT *
    FROM cliente
    WHERE telefono = p_telefono;
END //

-- id_cuenta es UNIQUE (una cuenta pertenece a una sola persona): sirve para la regla
-- "no eliminar una cuenta con un cliente activo asociado". No filtra activo
CREATE PROCEDURE buscar_cliente_por_cuenta (IN p_id_cuenta INT)
BEGIN
    SELECT *
    FROM cliente
    WHERE id_cuenta = p_id_cuenta;
END //

-- Solo lista los clientes activos (respeta el borrado logico)
CREATE PROCEDURE listar_clientes ()
BEGIN
    SELECT *
    FROM cliente
    WHERE activo = 1;
END //

DELIMITER ;
