-- Procedimientos CRUD para patitasarriba.modelo.boleta.Boleta
-- Las lineas de la boleta se manejan en procedures-detalle_boleta_serv.sql
-- y procedures-detalle_boleta_art.sql

use patitasarriba;

DROP PROCEDURE IF EXISTS insertar_boleta;
DROP PROCEDURE IF EXISTS modificar_boleta;
DROP PROCEDURE IF EXISTS eliminar_boleta;
DROP PROCEDURE IF EXISTS buscar_boleta_por_id;
DROP PROCEDURE IF EXISTS buscar_boleta_por_serie_correlativo;
DROP PROCEDURE IF EXISTS obtener_ultimo_correlativo;
DROP PROCEDURE IF EXISTS listar_boletas;
DROP PROCEDURE IF EXISTS listar_boletas_por_cliente;

DELIMITER //

-- Crea una boleta y devuelve el id generado
CREATE PROCEDURE insertar_boleta (
    IN p_id_cliente INT,
    IN p_serie_correlativo VARCHAR(15),
    IN p_fecha DATETIME,
    IN p_total DECIMAL(10,2),
    IN p_metodo_pago VARCHAR(20),
    IN p_activo TINYINT(1),
    OUT p_id INT
)
BEGIN
    INSERT INTO boleta (
        id_cliente, serie_correlativo, fecha, total, metodo_pago, activo
    )
    VALUES (
        p_id_cliente, p_serie_correlativo, p_fecha, p_total, p_metodo_pago, p_activo
    );

    SET p_id = LAST_INSERT_ID();
END //

-- Actualiza todos los campos de una boleta existente
CREATE PROCEDURE modificar_boleta (
    IN p_id_cliente INT,
    IN p_serie_correlativo VARCHAR(15),
    IN p_fecha DATETIME,
    IN p_total DECIMAL(10,2),
    IN p_metodo_pago VARCHAR(20),
    IN p_activo TINYINT(1),
    IN p_id INT
)
BEGIN
    UPDATE boleta
    SET id_cliente = p_id_cliente,
        serie_correlativo = p_serie_correlativo,
        fecha = p_fecha,
        total = p_total,
        metodo_pago = p_metodo_pago,
        activo = p_activo
    WHERE id_boleta = p_id;
END //

-- Anulacion (borrado logico): la boleta queda inactiva
-- (sus lineas se desactivan antes, en la misma transaccion del BL)
CREATE PROCEDURE eliminar_boleta (IN p_id INT)
BEGIN
    UPDATE boleta
    SET activo = 0
    WHERE id_boleta = p_id;
END //

-- Solo devuelve la boleta si esta activa
CREATE PROCEDURE buscar_boleta_por_id (IN p_id INT)
BEGIN
    SELECT *
    FROM boleta
    WHERE id_boleta = p_id
      AND activo = 1;
END //

-- serie_correlativo es UNIQUE: no filtra activo, para detectar tambien las boletas anuladas
CREATE PROCEDURE buscar_boleta_por_serie_correlativo (IN p_serie_correlativo VARCHAR(15))
BEGIN
    SELECT *
    FROM boleta
    WHERE serie_correlativo = p_serie_correlativo;
END //

-- Ultimo numero usado de una serie (p_serie = 'B001' -> serie_correlativo 'B001-000123' devuelve 123).
-- Devuelve 0 si la serie aun no tiene boletas. Incluye las anuladas: un correlativo no se reutiliza
CREATE PROCEDURE obtener_ultimo_correlativo (
    IN p_serie VARCHAR(4),
    OUT p_ultimo INT
)
BEGIN
    SELECT COALESCE(MAX(CAST(SUBSTRING(serie_correlativo, 6) AS UNSIGNED)), 0)
    INTO p_ultimo
    FROM boleta
    WHERE serie_correlativo LIKE CONCAT(p_serie, '-%');
END //

-- Solo lista las boletas activas (respeta el borrado logico)
CREATE PROCEDURE listar_boletas ()
BEGIN
    SELECT *
    FROM boleta
    WHERE activo = 1;
END //

-- Boletas activas de un cliente (util para la regla "no eliminar un cliente con boletas")
CREATE PROCEDURE listar_boletas_por_cliente (IN p_id_cliente INT)
BEGIN
    SELECT *
    FROM boleta
    WHERE id_cliente = p_id_cliente
      AND activo = 1;
END //

DELIMITER ;
