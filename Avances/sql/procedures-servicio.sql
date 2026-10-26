-- Procedimientos CRUD para patitasarriba.modelo.producto.Servicio

use patitasarriba;

DROP PROCEDURE IF EXISTS insertar_servicio;
DROP PROCEDURE IF EXISTS modificar_servicio;
DROP PROCEDURE IF EXISTS eliminar_servicio;
DROP PROCEDURE IF EXISTS buscar_servicio_por_id;
DROP PROCEDURE IF EXISTS buscar_servicio_por_nombre;
DROP PROCEDURE IF EXISTS listar_servicios;

DELIMITER //

-- Crea un servicio y devuelve el id generado
CREATE PROCEDURE insertar_servicio (
    IN p_nombre VARCHAR(45),
    IN p_precio_base DECIMAL(10,2),
    IN p_descripcion VARCHAR(200),
    IN p_activo TINYINT(1),
    IN p_duracion_estimada INT,
    IN p_tipo_servicio VARCHAR(20),
    OUT p_id INT
)
BEGIN
    INSERT INTO servicio (
        nombre, precio_base, descripcion, activo,
        duracion_estimada, tipo_servicio
    )
    VALUES (
        p_nombre, p_precio_base, p_descripcion, p_activo,
        p_duracion_estimada, p_tipo_servicio
    );

    SET p_id = LAST_INSERT_ID();
END //

-- Actualiza todos los campos de un servicio existente
CREATE PROCEDURE modificar_servicio (
    IN p_nombre VARCHAR(45),
    IN p_precio_base DECIMAL(10,2),
    IN p_descripcion VARCHAR(200),
    IN p_activo TINYINT(1),
    IN p_duracion_estimada INT,
    IN p_tipo_servicio VARCHAR(20),
    IN p_id INT
)
BEGIN
    UPDATE servicio
    SET nombre = p_nombre,
        precio_base = p_precio_base,
        descripcion = p_descripcion,
        activo = p_activo,
        duracion_estimada = p_duracion_estimada,
        tipo_servicio = p_tipo_servicio
    WHERE id_servicio = p_id;
END //

-- Borrado logico: el servicio queda inactivo
CREATE PROCEDURE eliminar_servicio (IN p_id INT)
BEGIN
    UPDATE servicio
    SET activo = 0
    WHERE id_servicio = p_id;
END //

-- Solo devuelve el servicio si esta activo
CREATE PROCEDURE buscar_servicio_por_id (IN p_id INT)
BEGIN
    SELECT *
    FROM servicio
    WHERE id_servicio = p_id
      AND activo = 1;
END //

-- nombre es UNIQUE: no filtra activo, para detectar tambien los servicios inactivos
CREATE PROCEDURE buscar_servicio_por_nombre (IN p_nombre VARCHAR(45))
BEGIN
    SELECT *
    FROM servicio
    WHERE nombre = p_nombre;
END //

-- Solo lista los servicios activos (respeta el borrado logico)
CREATE PROCEDURE listar_servicios ()
BEGIN
    SELECT *
    FROM servicio
    WHERE activo = 1;
END //

DELIMITER ;
