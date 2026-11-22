-- Procedimientos CRUD para patitasarriba.modelo.receta.Receta
-- Las lineas de la receta (detalles) se manejan en procedures-detalle_receta.sql

use patitasarriba;

DROP PROCEDURE IF EXISTS insertar_receta;
DROP PROCEDURE IF EXISTS modificar_receta;
DROP PROCEDURE IF EXISTS eliminar_receta;
DROP PROCEDURE IF EXISTS buscar_receta_por_id;
DROP PROCEDURE IF EXISTS listar_recetas;
DROP PROCEDURE IF EXISTS listar_recetas_por_atencion;

DELIMITER //

-- Crea una receta y devuelve el id generado
CREATE PROCEDURE insertar_receta (
    IN p_id_atencion INT,
    IN p_fecha_emision DATE,
    IN p_indicaciones_generales VARCHAR(500),
    IN p_activo TINYINT(1),
    OUT p_id INT
)
BEGIN
    INSERT INTO receta (id_atencion, fecha_emision, indicaciones_generales, activo)
    VALUES (p_id_atencion, p_fecha_emision, p_indicaciones_generales, p_activo);

    SET p_id = LAST_INSERT_ID();
END //

-- Actualiza todos los campos de una receta existente
CREATE PROCEDURE modificar_receta (
    IN p_id_atencion INT,
    IN p_fecha_emision DATE,
    IN p_indicaciones_generales VARCHAR(500),
    IN p_activo TINYINT(1),
    IN p_id INT
)
BEGIN
    UPDATE receta
    SET id_atencion = p_id_atencion,
        fecha_emision = p_fecha_emision,
        indicaciones_generales = p_indicaciones_generales,
        activo = p_activo
    WHERE id_receta = p_id;
END //

-- Borrado logico: la receta queda inactiva
-- (sus detalles se desactivan antes, en la misma transaccion del BL)
CREATE PROCEDURE eliminar_receta (IN p_id INT)
BEGIN
    UPDATE receta
    SET activo = 0
    WHERE id_receta = p_id;
END //

-- Solo devuelve la receta si esta activa
CREATE PROCEDURE buscar_receta_por_id (IN p_id INT)
BEGIN
    SELECT *
    FROM receta
    WHERE id_receta = p_id
      AND activo = 1;
END //

-- Solo lista las recetas activas (respeta el borrado logico)
CREATE PROCEDURE listar_recetas ()
BEGIN
    SELECT *
    FROM receta
    WHERE activo = 1;
END //

-- Recetas activas de UNA atencion
CREATE PROCEDURE listar_recetas_por_atencion (IN p_id_atencion INT)
BEGIN
    SELECT *
    FROM receta
    WHERE id_atencion = p_id_atencion
      AND activo = 1;
END //

DELIMITER ;
