-- Procedimientos CRUD para patitasarriba.modelo.atencion.InsumoUtilizado

DROP PROCEDURE IF EXISTS insertar_insumo_utilizado;
DROP PROCEDURE IF EXISTS modificar_insumo_utilizado;
DROP PROCEDURE IF EXISTS eliminar_insumo_utilizado;
DROP PROCEDURE IF EXISTS buscar_insumo_utilizado_por_id;
DROP PROCEDURE IF EXISTS listar_insumo_utilizado_por_atencion_tratamiento;

DELIMITER //

-- Registra un insumo utilizado en un tratamiento y devuelve el id generado
CREATE PROCEDURE insertar_insumo_utilizado (
    IN p_id_atencion_tratamiento INT,
    IN p_id_articulo INT,
    IN p_cantidad_utilizada INT,
    IN p_activo TINYINT(1),
    OUT p_id INT
)
BEGIN
    INSERT INTO insumo_utilizado (id_atencion_tratamiento, id_articulo, cantidad_utilizada, activo)
    VALUES (p_id_atencion_tratamiento, p_id_articulo, p_cantidad_utilizada, p_activo);

    SET p_id = LAST_INSERT_ID();
END //

-- Actualiza un insumo utilizado existente (id_atencion_tratamiento no cambia: un insumo no se muda de tratamiento)
CREATE PROCEDURE modificar_insumo_utilizado (
    IN p_id INT,
    IN p_id_articulo INT,
    IN p_cantidad_utilizada INT,
    IN p_activo TINYINT(1)
)
BEGIN
    UPDATE insumo_utilizado
    SET id_articulo = p_id_articulo,
        cantidad_utilizada = p_cantidad_utilizada,
        activo = p_activo
    WHERE id_insumo_utilizado = p_id;
END //

CREATE PROCEDURE eliminar_insumo_utilizado (IN p_id INT)
BEGIN
    DELETE FROM insumo_utilizado
    WHERE id_insumo_utilizado = p_id;
END //

CREATE PROCEDURE buscar_insumo_utilizado_por_id (IN p_id INT)
BEGIN
    SELECT *
    FROM insumo_utilizado
    WHERE id_insumo_utilizado = p_id;
END //

-- Lista los insumos utilizados en UN tratamiento especifico (patron maestro-detalle)
CREATE PROCEDURE listar_insumo_utilizado_por_atencion_tratamiento (IN p_id_atencion_tratamiento INT)
BEGIN
    SELECT *
    FROM insumo_utilizado
    WHERE id_atencion_tratamiento = p_id_atencion_tratamiento;
END //

DELIMITER ;
