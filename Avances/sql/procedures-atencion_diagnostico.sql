USE mydb;

DROP PROCEDURE IF EXISTS insertar_atencion_diagnostico;
DROP PROCEDURE IF EXISTS listar_atencion_diagnosticos_por_atencion_medica;
DROP PROCEDURE IF EXISTS eliminar_atencion_diagnosticos_por_atencion_medica;


CREATE PROCEDURE insertar_atencion_diagnostico (
    IN p_id_atencion_medica INT,
    IN p_id_diagnostico INT,
    IN p_nivel_gravedad VARCHAR(45),
    IN p_detalle_diagnostico VARCHAR(200),
    IN p_activo TINYINT(1),
    OUT p_id INT
)
BEGIN
    INSERT INTO ATENCION_DIAGNOSTICO (
        id_atencion_medica,
        id_diagnostico,
        nivel_gravedad,
        detalle_diagnostico,
        activo
    )
    VALUES (
        p_id_atencion_medica,
        p_id_diagnostico,
        p_nivel_gravedad,
        p_detalle_diagnostico,
        p_activo
    );

    SET p_id = LAST_INSERT_ID();
END;

CREATE PROCEDURE listar_atencion_diagnosticos_por_atencion_medica (IN p_id_atencion_medica INT)
BEGIN
    SELECT *
    FROM ATENCION_DIAGNOSTICO
    WHERE id_atencion_medica = p_id_atencion_medica;
END;

CREATE PROCEDURE eliminar_atencion_diagnosticos_por_atencion_medica (IN p_id_atencion_medica INT)
BEGIN
    DELETE FROM ATENCION_DIAGNOSTICO
    WHERE id_atencion_medica = p_id_atencion_medica;
END ;

