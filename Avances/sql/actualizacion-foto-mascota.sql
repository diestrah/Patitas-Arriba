-- Actualizacion para una base YA INSTALADA: agrega la foto opcional de la mascota (portal del cliente).
-- 1) Agrega la columna mascota.foto (MEDIUMTEXT, puede ser NULL): guarda la imagen como texto "data:image/jpeg;base64,...".
-- 2) Vuelve a crear insertar_mascota y modificar_mascota, que ahora reciben p_foto.
-- EJECUTAR UNA SOLA VEZ y ANTES de correr el backend nuevo: sin este script, leer o guardar una mascota falla.
-- Se genera a partir del DDL de patitasarriba-mysql.sql y de procedures-mascota.sql: si cambian, hay que volver a generarlo.

USE patitasarriba;

ALTER TABLE `patitasarriba`.`mascota` ADD COLUMN `foto` MEDIUMTEXT NULL AFTER `raza`;

DROP PROCEDURE IF EXISTS insertar_mascota;
DROP PROCEDURE IF EXISTS modificar_mascota;

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
    IN p_foto MEDIUMTEXT,
    IN p_activo TINYINT(1),
    OUT p_id INT
)
BEGIN
    INSERT INTO mascota (
        id_cliente, nombre, sexo, peso, fecha_nacimiento,
        tipo_mascota, raza, foto, activo
    )
    VALUES (
        p_id_cliente, p_nombre, p_sexo, p_peso, p_fecha_nacimiento,
        p_tipo_mascota, p_raza, p_foto, p_activo
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
    IN p_foto MEDIUMTEXT,
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
        foto = p_foto,
        activo = p_activo
    WHERE id_mascota = p_id;
END //

DELIMITER ;
