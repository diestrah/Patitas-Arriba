USE mydb;

DROP PROCEDURE IF EXISTS insertar_atencion_tratamiento;
DROP PROCEDURE IF EXISTS listar_atencion_tratamientos_por_atencion_medica;
DROP PROCEDURE IF EXISTS eliminar_atencion_tratamientos_por_atencion_medica;


CREATE PROCEDURE insertar_atencion_tratamiento (
    IN p_id_atencion_medica INT,
    IN p_id_tratamiento INT,
    IN p_activo TINYINT(1),
    OUT p_id INT
)
BEGIN
    INSERT INTO atencion_tratamiento (
        id_atencion_medica,
        id_tratamiento,
        activo
    )
    VALUES (
        p_id_atencion_medica,
        p_id_tratamiento,
        p_activo
    );

    SET p_id = LAST_INSERT_ID();
END;

CREATE PROCEDURE listar_atencion_tratamientos_por_atencion_medica (IN p_id_atencion_medica INT)
BEGIN
    SELECT *
    FROM atencion_tratamiento
    WHERE id_atencion_medica = p_id_atencion_medica;
END;

CREATE PROCEDURE eliminar_atencion_tratamientos_por_atencion_medica (IN p_id_atencion_medica INT)
BEGIN
    DELETE FROM atencion_tratamiento
    WHERE id_atencion_medica = p_id_atencion_medica;
END;

