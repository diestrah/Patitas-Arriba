-- Procedimientos CRUD para patitasarriba.modelo.producto.CategoriaArticulo

DROP PROCEDURE IF EXISTS insertar_categoria_articulo;
DROP PROCEDURE IF EXISTS modificar_categoria_articulo;
DROP PROCEDURE IF EXISTS eliminar_categoria_articulo;
DROP PROCEDURE IF EXISTS buscar_categoria_articulo_por_id;
DROP PROCEDURE IF EXISTS listar_categoria_articulo;

DELIMITER //

-- Crea una categoria y devuelve el id generado
CREATE PROCEDURE insertar_categoria_articulo (
    IN p_nombre VARCHAR(45),
    IN p_descripcion VARCHAR(200),
    IN p_activo TINYINT(1),
    OUT p_id INT
)
BEGIN
    INSERT INTO CATEGORIA_ARTICULO (nombre, descripcion, activo)
    VALUES (p_nombre, p_descripcion, p_activo);

    SET p_id = LAST_INSERT_ID();
END //

-- Actualiza todos los campos de una categoria existente
CREATE PROCEDURE modificar_categoria_articulo (
    IN p_id INT,
    IN p_nombre VARCHAR(45),
    IN p_descripcion VARCHAR(200),
    IN p_activo TINYINT(1)
)
BEGIN
    UPDATE CATEGORIA_ARTICULO
    SET nombre = p_nombre,
        descripcion = p_descripcion,
        activo = p_activo
    WHERE id_categoria_articulo = p_id;
END //

-- Borrado suave: nunca se elimina la fila, solo se desactiva
CREATE PROCEDURE eliminar_categoria_articulo (IN p_id INT)
BEGIN
    UPDATE CATEGORIA_ARTICULO
    SET activo = 0
    WHERE id_categoria_articulo = p_id;
END //

CREATE PROCEDURE buscar_categoria_articulo_por_id (IN p_id INT)
BEGIN
    SELECT *
    FROM CATEGORIA_ARTICULO
    WHERE id_categoria_articulo = p_id;
END //

-- Solo lista las categorias activas (respeta el borrado suave)
CREATE PROCEDURE listar_categoria_articulo ()
BEGIN
    SELECT *
    FROM CATEGORIA_ARTICULO
    WHERE activo = 1;
END //

DELIMITER ;
