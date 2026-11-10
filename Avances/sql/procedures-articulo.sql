-- Procedimientos CRUD para patitasarriba.modelo.producto.Articulo
-- Las categorias del articulo se manejan en procedures-articulo_categoria.sql

use patitasarriba;

DROP PROCEDURE IF EXISTS insertar_articulo;
DROP PROCEDURE IF EXISTS modificar_articulo;
DROP PROCEDURE IF EXISTS eliminar_articulo;
DROP PROCEDURE IF EXISTS buscar_articulo_por_id;
DROP PROCEDURE IF EXISTS listar_articulo;

DELIMITER //

-- Crea un articulo y devuelve el id generado
CREATE PROCEDURE insertar_articulo (
    IN p_nombre VARCHAR(45),
    IN p_precio_base DECIMAL(10,2),
    IN p_descripcion VARCHAR(200),
    IN p_activo TINYINT(1),
    IN p_stock_actual INT,
    IN p_stock_minimo INT,
    OUT p_id INT
)
BEGIN
    INSERT INTO articulo (
        nombre, precio_base, descripcion, activo,
        stock_actual, stock_minimo
    )
    VALUES (
        p_nombre, p_precio_base, p_descripcion, p_activo,
        p_stock_actual, p_stock_minimo
    );

    SET p_id = LAST_INSERT_ID();
END //

-- Actualiza todos los campos de un articulo existente
CREATE PROCEDURE modificar_articulo (
    IN p_id INT,
    IN p_nombre VARCHAR(45),
    IN p_precio_base DECIMAL(10,2),
    IN p_descripcion VARCHAR(200),
    IN p_activo TINYINT(1),
    IN p_stock_actual INT,
    IN p_stock_minimo INT
)
BEGIN
    UPDATE articulo
    SET nombre = p_nombre,
        precio_base = p_precio_base,
        descripcion = p_descripcion,
        activo = p_activo,
        stock_actual = p_stock_actual,
        stock_minimo = p_stock_minimo
    WHERE id_articulo = p_id;
END //

-- Borrado logico: el articulo queda inactivo
CREATE PROCEDURE eliminar_articulo (IN p_id INT)
BEGIN
    UPDATE articulo
    SET activo = 0
    WHERE id_articulo = p_id;
END //

-- Solo devuelve el articulo si esta activo
CREATE PROCEDURE buscar_articulo_por_id (IN p_id INT)
BEGIN
    SELECT *
    FROM articulo
    WHERE id_articulo = p_id
      AND activo = 1;
END //

-- Solo lista los articulos activos (respeta el borrado logico)
CREATE PROCEDURE listar_articulo ()
BEGIN
    SELECT *
    FROM articulo
    WHERE activo = 1;
END //

DELIMITER ;
