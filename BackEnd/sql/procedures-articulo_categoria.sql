-- Procedimientos para patitasarriba.modelo.producto.Articulo (lista de categorias)
-- Tabla de enlace articulo_categoria: clave compuesta (id_articulo, id_categoria)

use patitasarriba;

DROP PROCEDURE IF EXISTS insertar_articulo_categoria;
DROP PROCEDURE IF EXISTS eliminar_articulo_categoria;
DROP PROCEDURE IF EXISTS eliminar_categorias_por_articulo;
DROP PROCEDURE IF EXISTS listar_categorias_por_articulo;
DROP PROCEDURE IF EXISTS listar_articulos_por_categoria;

DELIMITER //

-- Asocia un articulo con una categoria; si el enlace ya existia (inactivo), lo reactiva
CREATE PROCEDURE insertar_articulo_categoria (
    IN p_id_articulo INT,
    IN p_id_categoria INT
)
BEGIN
    INSERT INTO articulo_categoria (id_articulo, id_categoria, activo)
    VALUES (p_id_articulo, p_id_categoria, 1)
    ON DUPLICATE KEY UPDATE activo = 1;
END //

-- Borrado logico de una asociacion
CREATE PROCEDURE eliminar_articulo_categoria (
    IN p_id_articulo INT,
    IN p_id_categoria INT
)
BEGIN
    UPDATE articulo_categoria
    SET activo = 0
    WHERE id_articulo = p_id_articulo
      AND id_categoria = p_id_categoria;
END //

-- Borrado logico de todas las categorias de un articulo
CREATE PROCEDURE eliminar_categorias_por_articulo (IN p_id_articulo INT)
BEGIN
    UPDATE articulo_categoria
    SET activo = 0
    WHERE id_articulo = p_id_articulo;
END //

-- Categorias activas de un articulo (devuelve las columnas de categoria)
CREATE PROCEDURE listar_categorias_por_articulo (IN p_id_articulo INT)
BEGIN
    SELECT c.*
    FROM categoria c
    INNER JOIN articulo_categoria ac ON ac.id_categoria = c.id_categoria
    WHERE ac.id_articulo = p_id_articulo
      AND ac.activo = 1
      AND c.activo = 1;
END //

-- Articulos activos de una categoria (devuelve las columnas de articulo)
CREATE PROCEDURE listar_articulos_por_categoria (IN p_id_categoria INT)
BEGIN
    SELECT a.*
    FROM articulo a
    INNER JOIN articulo_categoria ac ON ac.id_articulo = a.id_articulo
    WHERE ac.id_categoria = p_id_categoria
      AND ac.activo = 1
      AND a.activo = 1;
END //

DELIMITER ;
