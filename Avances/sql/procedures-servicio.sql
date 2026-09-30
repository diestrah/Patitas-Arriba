USE mydb;

DROP PROCEDURE IF EXISTS insertar_servicio;
DROP PROCEDURE IF EXISTS modificar_servicio;
DROP PROCEDURE IF EXISTS eliminar_servicio;
DROP PROCEDURE IF EXISTS buscar_servicio_por_id;
DROP PROCEDURE IF EXISTS buscar_servicio_por_nombre;
DROP PROCEDURE IF EXISTS listar_servicios;

DELIMITER //

CREATE PROCEDURE insertar_servicio (
    IN p_nombre VARCHAR(45),
    IN p_precio_base DECIMAL(10,2),
    IN p_descripcion VARCHAR(45),
    IN p_estado TINYINT(1),
    IN p_duracion_estimada INT,
    IN p_servicio_medico VARCHAR(45),
    IN p_requiere_triaje TINYINT(1),
    IN p_requiere_vacuna TINYINT(1),
    OUT p_id INT)
BEGIN
    INSERT INTO SERVICIO (
        nombre,
        precio_base,
        descripcion,
        activo,
        duracion_estimada,
        tipo_servicio_medico,
        requiere_triaje,
        requiere_vacuna)
    VALUES (
        p_nombre,
        p_precio_base,
        p_descripcion,
        p_estado,
        p_duracion_estimada,
        p_servicio_medico,
        p_requiere_triaje,
        p_requiere_vacuna);

    SET p_id = LAST_INSERT_ID();
END //

CREATE PROCEDURE modificar_servicio(
    IN p_nombre VARCHAR(45),
    IN p_precio_base DECIMAL(10,2),
    IN p_descripcion VARCHAR(45),
    IN p_estado TINYINT(1),
    IN p_duracion_estimada INT,
    IN p_servicio_medico VARCHAR(45),
    IN p_requiere_triaje TINYINT(1),
    IN p_requiere_vacuna TINYINT(1),
    IN p_id INT
)
BEGIN
    UPDATE SERVICIO
    SET 
        nombre = p_nombre,
        precio_base = p_precio_base,
        descripcion = p_descripcion,
        activo = p_estado,
        duracion_estimada = p_duracion_estimada,
        tipo_servicio_medico = p_servicio_medico,
        requiere_triaje = p_requiere_triaje,
        requiere_vacuna = p_requiere_vacuna
    WHERE id_servicio = p_id;
END //

CREATE PROCEDURE eliminar_servicio(IN p_id INT)
BEGIN
    DELETE FROM SERVICIO
    WHERE id_servicio = p_id;
END //

CREATE PROCEDURE buscar_servicio_por_id(IN p_id INT)
BEGIN
    SELECT *
    FROM SERVICIO
    WHERE id_servicio = p_id;
END //

CREATE PROCEDURE buscar_servicio_por_nombre(IN p_nombre VARCHAR(45))
BEGIN
    SELECT *
    FROM SERVICIO
    WHERE nombre = p_nombre;
END //

CREATE PROCEDURE listar_servicios()
BEGIN
    SELECT *
    FROM SERVICIO;
END //