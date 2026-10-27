-- Procedimientos CRUD para patitasarriba.modelo.producto.Categoria

use patitasarriba;

DROP PROCEDURE IF EXISTS insertar_categoria;
DROP PROCEDURE IF EXISTS modificar_categoria;
DROP PROCEDURE IF EXISTS eliminar_categoria;
DROP PROCEDURE IF EXISTS buscar_categoria_por_id;
DROP PROCEDURE IF EXISTS buscar_categoria_por_nombre;
DROP PROCEDURE IF EXISTS listar_categoria;

DELIMITER //

-- Crea una categoria y devuelve el id generado
CREATE PROCEDURE insertar_categoria (
    IN p_nombre VARCHAR(45),
    IN p_descripcion VARCHAR(200),
    IN p_activo TINYINT(1),
    OUT p_id INT
)
BEGIN
    INSERT INTO categoria (nombre, descripcion, activo)
    VALUES (p_nombre, p_descripcion, p_activo);

    SET p_id = LAST_INSERT_ID();
END //

-- Actualiza todos los campos de una categoria existente
CREATE PROCEDURE modificar_categoria (
    IN p_id INT,
    IN p_nombre VARCHAR(45),
    IN p_descripcion VARCHAR(200),
    IN p_activo TINYINT(1)
)
BEGIN
    UPDATE categoria
    SET nombre = p_nombre,
        descripcion = p_descripcion,
        activo = p_activo
    WHERE id_categoria = p_id;
END //

-- Borrado logico: la categoria queda inactiva
CREATE PROCEDURE eliminar_categoria (IN p_id INT)
BEGIN
    UPDATE categoria
    SET activo = 0
    WHERE id_categoria = p_id;
END //

-- Solo devuelve la categoria si esta activa
CREATE PROCEDURE buscar_categoria_por_id (IN p_id INT)
BEGIN
    SELECT *
    FROM categoria
    WHERE id_categoria = p_id
      AND activo = 1;
END //

-- El nombre es UNIQUE: no filtra activo para detectar tambien las categorias inactivas (validacion de duplicados en el BL)
CREATE PROCEDURE buscar_categoria_por_nombre (IN p_nombre VARCHAR(45))
BEGIN
    SELECT *
    FROM categoria
    WHERE nombre = p_nombre;
END //

-- Solo lista las categorias activas (respeta el borrado logico)
CREATE PROCEDURE listar_categoria ()
BEGIN
    SELECT *
    FROM categoria
    WHERE activo = 1;
END //

DELIMITER ;
