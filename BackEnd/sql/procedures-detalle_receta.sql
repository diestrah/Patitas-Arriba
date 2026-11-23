-- Procedimientos CRUD para patitasarriba.modelo.receta.DetalleReceta

use patitasarriba;

DROP PROCEDURE IF EXISTS insertar_detalle_receta;
DROP PROCEDURE IF EXISTS modificar_detalle_receta;
DROP PROCEDURE IF EXISTS eliminar_detalle_receta;
DROP PROCEDURE IF EXISTS eliminar_detalles_por_receta;
DROP PROCEDURE IF EXISTS buscar_detalle_receta_por_id;
DROP PROCEDURE IF EXISTS listar_detalles_receta;
DROP PROCEDURE IF EXISTS listar_detalles_por_receta;
DROP PROCEDURE IF EXISTS listar_detalles_receta_por_articulo;

DELIMITER //

-- Crea una linea de receta (un medicamento) y devuelve el id generado
CREATE PROCEDURE insertar_detalle_receta (
    IN p_id_receta INT,
    IN p_id_articulo INT,
    IN p_dosis VARCHAR(45),
    IN p_frecuencia VARCHAR(45),
    IN p_duracion_dias INT,
    IN p_cantidad_total INT,
    IN p_activo TINYINT(1),
    OUT p_id INT
)
BEGIN
    INSERT INTO detalle_receta (
        id_receta, id_articulo, dosis, frecuencia,
        duracion_dias, cantidad_total, activo
    )
    VALUES (
        p_id_receta, p_id_articulo, p_dosis, p_frecuencia,
        p_duracion_dias, p_cantidad_total, p_activo
    );

    SET p_id = LAST_INSERT_ID();
END //

-- Actualiza todos los campos de una linea de receta existente
CREATE PROCEDURE modificar_detalle_receta (
    IN p_id_receta INT,
    IN p_id_articulo INT,
    IN p_dosis VARCHAR(45),
    IN p_frecuencia VARCHAR(45),
    IN p_duracion_dias INT,
    IN p_cantidad_total INT,
    IN p_activo TINYINT(1),
    IN p_id INT
)
BEGIN
    UPDATE detalle_receta
    SET id_receta = p_id_receta,
        id_articulo = p_id_articulo,
        dosis = p_dosis,
        frecuencia = p_frecuencia,
        duracion_dias = p_duracion_dias,
        cantidad_total = p_cantidad_total,
        activo = p_activo
    WHERE id_detalle_receta = p_id;
END //

-- Borrado logico de una linea de receta
CREATE PROCEDURE eliminar_detalle_receta (IN p_id INT)
BEGIN
    UPDATE detalle_receta
    SET activo = 0
    WHERE id_detalle_receta = p_id;
END //

-- Borrado logico de todas las lineas de UNA receta (paso previo a eliminar la receta,
-- dentro de la misma transaccion)
CREATE PROCEDURE eliminar_detalles_por_receta (IN p_id_receta INT)
BEGIN
    UPDATE detalle_receta
    SET activo = 0
    WHERE id_receta = p_id_receta;
END //

-- Solo devuelve la linea si esta activa
CREATE PROCEDURE buscar_detalle_receta_por_id (IN p_id INT)
BEGIN
    SELECT *
    FROM detalle_receta
    WHERE id_detalle_receta = p_id
      AND activo = 1;
END //

-- Solo lista las lineas activas (respeta el borrado logico)
CREATE PROCEDURE listar_detalles_receta ()
BEGIN
    SELECT *
    FROM detalle_receta
    WHERE activo = 1;
END //

-- Lineas activas de UNA receta (patron maestro-detalle)
CREATE PROCEDURE listar_detalles_por_receta (IN p_id_receta INT)
BEGIN
    SELECT *
    FROM detalle_receta
    WHERE id_receta = p_id_receta
      AND activo = 1;
END //

-- Lineas activas que usan un articulo (util para la regla "no eliminar un articulo en uso")
CREATE PROCEDURE listar_detalles_receta_por_articulo (IN p_id_articulo INT)
BEGIN
    SELECT *
    FROM detalle_receta
    WHERE id_articulo = p_id_articulo
      AND activo = 1;
END //

DELIMITER ;
