USE mydb;

DROP PROCEDURE IF EXISTS insertar_insumo_utilizado;
DROP PROCEDURE IF EXISTS listar_insumos_por_atencion_tratamiento;
DROP PROCEDURE IF EXISTS eliminar_insumos_por_atencion_tratamiento;


CREATE PROCEDURE insertar_insumo_utilizado (
    IN p_id_atencion_tratamiento INT,
    IN p_id_articulo INT,
    IN p_cantidad_utilizada INT,
    IN p_activo TINYINT(1),
    OUT p_id INT
)
BEGIN
    INSERT INTO insumo_utilizado (
        id_atencion_tratamiento,
        id_articulo,
        cantidad_utilizada,
        activo
    )
    VALUES (
        p_id_atencion_tratamiento,
        p_id_articulo,
        p_cantidad_utilizada,
        p_activo
    );

    SET p_id = LAST_INSERT_ID();
END;

CREATE PROCEDURE listar_insumos_por_atencion_tratamiento (IN p_id_atencion_tratamiento INT)
BEGIN
    SELECT *
    FROM insumo_utilizado
    WHERE id_atencion_tratamiento = p_id_atencion_tratamiento;
END;

CREATE PROCEDURE eliminar_insumos_por_atencion_tratamiento (IN p_id_atencion_tratamiento INT)
BEGIN
    DELETE FROM insumo_utilizado
    WHERE id_atencion_tratamiento = p_id_atencion_tratamiento;
END;

