-- Procedimientos CRUD para patitasarriba.modelo.boleta.DetalleBoletaArticulo
-- id_insumo_utilizado es opcional: NULL = venta directa de caja; con valor = insumo cobrado de una atencion
-- subtotal es una columna generada (cantidad * precio_unitario): nunca se inserta ni se actualiza

use patitasarriba;

DROP PROCEDURE IF EXISTS insertar_detalle_boleta_art;
DROP PROCEDURE IF EXISTS modificar_detalle_boleta_art;
DROP PROCEDURE IF EXISTS eliminar_detalle_boleta_art;
DROP PROCEDURE IF EXISTS eliminar_detalles_boleta_art_por_boleta;
DROP PROCEDURE IF EXISTS buscar_detalle_boleta_art_por_id;
DROP PROCEDURE IF EXISTS buscar_detalle_boleta_art_por_insumo;
DROP PROCEDURE IF EXISTS listar_detalles_boleta_art;
DROP PROCEDURE IF EXISTS listar_detalles_boleta_art_por_boleta;

DELIMITER //

-- Crea una linea de articulo en una boleta y devuelve el id generado.
-- p_id_insumo_utilizado puede ser NULL (venta directa)
CREATE PROCEDURE insertar_detalle_boleta_art (
    IN p_id_boleta INT,
    IN p_id_articulo INT,
    IN p_id_insumo_utilizado INT,
    IN p_cantidad INT,
    IN p_precio_unitario DECIMAL(10,2),
    IN p_activo TINYINT(1),
    OUT p_id INT
)
BEGIN
    INSERT INTO detalle_boleta_art (
        id_boleta, id_articulo, id_insumo_utilizado, cantidad, precio_unitario, activo
    )
    VALUES (
        p_id_boleta, p_id_articulo, p_id_insumo_utilizado, p_cantidad, p_precio_unitario, p_activo
    );

    SET p_id = LAST_INSERT_ID();
END //

-- Actualiza todos los campos de una linea existente (el subtotal se recalcula solo)
CREATE PROCEDURE modificar_detalle_boleta_art (
    IN p_id_boleta INT,
    IN p_id_articulo INT,
    IN p_id_insumo_utilizado INT,
    IN p_cantidad INT,
    IN p_precio_unitario DECIMAL(10,2),
    IN p_activo TINYINT(1),
    IN p_id INT
)
BEGIN
    UPDATE detalle_boleta_art
    SET id_boleta = p_id_boleta,
        id_articulo = p_id_articulo,
        id_insumo_utilizado = p_id_insumo_utilizado,
        cantidad = p_cantidad,
        precio_unitario = p_precio_unitario,
        activo = p_activo
    WHERE id_detalle_boleta_art = p_id;
END //

-- Borrado logico de una linea
CREATE PROCEDURE eliminar_detalle_boleta_art (IN p_id INT)
BEGIN
    UPDATE detalle_boleta_art
    SET activo = 0
    WHERE id_detalle_boleta_art = p_id;
END //

-- Borrado logico de todas las lineas de articulo de UNA boleta (paso previo a anular la boleta,
-- dentro de la misma transaccion)
CREATE PROCEDURE eliminar_detalles_boleta_art_por_boleta (IN p_id_boleta INT)
BEGIN
    UPDATE detalle_boleta_art
    SET activo = 0
    WHERE id_boleta = p_id_boleta;
END //

-- Solo devuelve la linea si esta activa
CREATE PROCEDURE buscar_detalle_boleta_art_por_id (IN p_id INT)
BEGIN
    SELECT *
    FROM detalle_boleta_art
    WHERE id_detalle_boleta_art = p_id
      AND activo = 1;
END //

-- id_insumo_utilizado es UNIQUE: no filtra activo, para detectar si el insumo ya se cobro
-- (aunque la boleta se haya anulado)
CREATE PROCEDURE buscar_detalle_boleta_art_por_insumo (IN p_id_insumo_utilizado INT)
BEGIN
    SELECT *
    FROM detalle_boleta_art
    WHERE id_insumo_utilizado = p_id_insumo_utilizado;
END //

-- Solo lista las lineas activas (respeta el borrado logico)
CREATE PROCEDURE listar_detalles_boleta_art ()
BEGIN
    SELECT *
    FROM detalle_boleta_art
    WHERE activo = 1;
END //

-- Lineas de articulo activas de UNA boleta (patron maestro-detalle)
CREATE PROCEDURE listar_detalles_boleta_art_por_boleta (IN p_id_boleta INT)
BEGIN
    SELECT *
    FROM detalle_boleta_art
    WHERE id_boleta = p_id_boleta
      AND activo = 1;
END //

DELIMITER ;
