use mydb;

DROP PROCEDURE IF EXISTS insertar_mascota;
DROP PROCEDURE IF EXISTS modificar_mascota;
DROP PROCEDURE IF EXISTS eliminar_mascota;
DROP PROCEDURE IF EXISTS buscar_mascota_por_id;
DROP PROCEDURE IF EXISTS buscar_mascota_por_nombre;
DROP PROCEDURE IF EXISTS listar_mascotas;
DROP PROCEDURE IF EXISTS listar_mascotas_por_cliente;
DROP PROCEDURE IF EXISTS eliminar_mascotas_por_cliente;

DELIMITER //
CREATE PROCEDURE insertar_mascota(
    IN p_id_cliente INT,
    IN p_nombre VARCHAR(45),
    IN p_sexo CHAR(1),
    IN p_peso DECIMAL(5, 2),
    IN p_fecha_nacimiento DATE,
    IN p_tipo_mascota VARCHAR(45),
    IN p_raza VARCHAR(45),
    IN p_activo BOOLEAN,
    OUT p_id INT)
BEGIN
    INSERT INTO mascota (
        id_cliente,
        nombre,
        sexo,
        peso,
        fecha_nacimiento,
        tipo_mascota,
        raza,
        activo)
    VALUES (
        p_id_cliente,
        p_nombre,
        p_sexo,
        p_peso,
        p_fecha_nacimiento,
        p_tipo_mascota,
        p_raza,
        p_activo);

    SET p_id = LAST_INSERT_ID();
END //

CREATE PROCEDURE modificar_mascota(
    IN p_id_cliente INT,
    IN p_nombre VARCHAR(45),
    IN p_sexo CHAR(1),
    IN p_peso DECIMAL(5, 2),
    IN p_fecha_nacimiento DATE,
    IN p_tipo_mascota VARCHAR(45),
    IN p_raza VARCHAR(45),
    IN p_activo BOOLEAN,
    IN p_id INT)
BEGIN
    UPDATE mascota
    SET
        id_cliente = p_id_cliente,
        nombre = p_nombre,
        sexo = p_sexo,
        peso = p_peso,
        fecha_nacimiento = p_fecha_nacimiento,
        tipo_mascota = p_tipo_mascota,
        raza = p_raza,
        activo = p_activo
    WHERE id_mascota = p_id;
END //

CREATE PROCEDURE eliminar_mascota(IN p_id INT)
BEGIN
    DELETE FROM mascota WHERE id_mascota = p_id;
END //

CREATE PROCEDURE buscar_mascota_por_id(IN p_id INT)
BEGIN
    SELECT * FROM mascota WHERE id_mascota = p_id;
END //

CREATE PROCEDURE buscar_mascota_por_nombre(IN p_nombre VARCHAR(45))
BEGIN
    SELECT * FROM mascota WHERE nombre = p_nombre;
END //

CREATE PROCEDURE listar_mascotas()
BEGIN
    SELECT * FROM mascota;
END //

CREATE PROCEDURE listar_mascotas_por_cliente(IN p_id_cliente INT)
BEGIN
    SELECT * FROM mascota WHERE id_cliente = p_id_cliente;
END //

CREATE PROCEDURE eliminar_mascotas_por_cliente(IN p_id_cliente INT)
BEGIN
    DELETE FROM mascota WHERE id_cliente = p_id_cliente;
END //

DELIMITER ;