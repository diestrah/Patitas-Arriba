-- Procedimientos CRUD para patitasarriba.modelo.mascota.Mascota

use patitasarriba;

DROP PROCEDURE IF EXISTS insertar_mascota;
DROP PROCEDURE IF EXISTS modificar_mascota;
DROP PROCEDURE IF EXISTS eliminar_mascota;
DROP PROCEDURE IF EXISTS eliminar_mascotas_por_cliente;
DROP PROCEDURE IF EXISTS buscar_mascota_por_id;
DROP PROCEDURE IF EXISTS buscar_mascota_por_nombre;
DROP PROCEDURE IF EXISTS listar_mascotas;
DROP PROCEDURE IF EXISTS listar_mascotas_por_cliente;

DELIMITER //

-- Crea una mascota y devuelve el id generado
CREATE PROCEDURE insertar_mascota (
    IN p_id_cliente INT,
    IN p_nombre VARCHAR(45),
    IN p_sexo VARCHAR(6),
    IN p_peso DECIMAL(5,2),
    IN p_fecha_nacimiento DATE,
    IN p_tipo_mascota VARCHAR(10),
    IN p_raza VARCHAR(45),
    IN p_activo TINYINT(1),
    OUT p_id INT
)
BEGIN
    INSERT INTO mascota (
        id_cliente, nombre, sexo, peso, fecha_nacimiento,
        tipo_mascota, raza, activo
    )
    VALUES (
        p_id_cliente, p_nombre, p_sexo, p_peso, p_fecha_nacimiento,
        p_tipo_mascota, p_raza, p_activo
    );

    SET p_id = LAST_INSERT_ID();
END //

-- Actualiza todos los campos de una mascota existente
CREATE PROCEDURE modificar_mascota (
    IN p_id_cliente INT,
    IN p_nombre VARCHAR(45),
    IN p_sexo VARCHAR(6),
    IN p_peso DECIMAL(5,2),
    IN p_fecha_nacimiento DATE,
    IN p_tipo_mascota VARCHAR(10),
    IN p_raza VARCHAR(45),
    IN p_activo TINYINT(1),
    IN p_id INT
)
BEGIN
    UPDATE mascota
    SET id_cliente = p_id_cliente,
        nombre = p_nombre,
        sexo = p_sexo,
        peso = p_peso,
        fecha_nacimiento = p_fecha_nacimiento,
        tipo_mascota = p_tipo_mascota,
        raza = p_raza,
        activo = p_activo
    WHERE id_mascota = p_id;
END //

-- Borrado logico: la mascota queda inactiva
CREATE PROCEDURE eliminar_mascota (IN p_id INT)
BEGIN
    UPDATE mascota
    SET activo = 0
    WHERE id_mascota = p_id;
END //

-- Borrado logico de todas las mascotas de UN cliente (paso previo a eliminar al cliente,
-- dentro de la misma transaccion, si el BL decide hacerlo en cascada)
CREATE PROCEDURE eliminar_mascotas_por_cliente (IN p_id_cliente INT)
BEGIN
    UPDATE mascota
    SET activo = 0
    WHERE id_cliente = p_id_cliente;
END //

-- Solo devuelve la mascota si esta activa
CREATE PROCEDURE buscar_mascota_por_id (IN p_id INT)
BEGIN
    SELECT *
    FROM mascota
    WHERE id_mascota = p_id
      AND activo = 1;
END //

-- Mascotas activas con ese nombre (el nombre no es UNIQUE: puede haber varias)
CREATE PROCEDURE buscar_mascota_por_nombre (IN p_nombre VARCHAR(45))
BEGIN
    SELECT *
    FROM mascota
    WHERE nombre = p_nombre
      AND activo = 1;
END //

-- Solo lista las mascotas activas (respeta el borrado logico)
CREATE PROCEDURE listar_mascotas ()
BEGIN
    SELECT *
    FROM mascota
    WHERE activo = 1;
END //

-- Mascotas activas de UN cliente (patron maestro-detalle)
CREATE PROCEDURE listar_mascotas_por_cliente (IN p_id_cliente INT)
BEGIN
    SELECT *
    FROM mascota
    WHERE id_cliente = p_id_cliente
      AND activo = 1;
END //

DELIMITER ;
