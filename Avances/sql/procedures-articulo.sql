-- Procedimientos CRUD para patitasarriba.modelo.producto.Articulo

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
    IN p_marca VARCHAR(45),
    IN p_id_categoria_articulo INT,
    OUT p_id INT
)
BEGIN
    INSERT INTO articulo (
        nombre, precio_base, descripcion, activo,
        stock_actual, stock_minimo, marca, id_categoria_articulo
    )
    VALUES (
        p_nombre, p_precio_base, p_descripcion, p_activo,
        p_stock_actual, p_stock_minimo, p_marca, p_id_categoria_articulo
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
    IN p_stock_minimo INT,
    IN p_marca VARCHAR(45),
    IN p_id_categoria_articulo INT
)
BEGIN
    UPDATE articulo
    SET nombre = p_nombre,
        precio_base = p_precio_base,
        descripcion = p_descripcion,
        activo = p_activo,
        stock_actual = p_stock_actual,
        stock_minimo = p_stock_minimo,
        marca = p_marca,
        id_categoria_articulo = p_id_categoria_articulo
    WHERE id_articulo = p_id;
END //

CREATE PROCEDURE eliminar_articulo (IN p_id INT)
BEGIN
    DELETE FROM articulo
    WHERE id_articulo = p_id;
END //

CREATE PROCEDURE buscar_articulo_por_id (IN p_id INT)
BEGIN
    SELECT *
    FROM articulo
    WHERE id_articulo = p_id;
END //

-- Solo lista los articulos activos (respeta el borrado suave)
CREATE PROCEDURE listar_articulo ()
BEGIN
    SELECT *
    FROM articulo
    WHERE activo = 1;
END //

DELIMITER ;
