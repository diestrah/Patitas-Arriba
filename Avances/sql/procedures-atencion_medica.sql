USE mydb;

DROP PROCEDURE IF EXISTS insertar_atencion_medica;
DROP PROCEDURE IF EXISTS actualizar_atencion_medica;
DROP PROCEDURE IF EXISTS eliminar_atencion_medica;
DROP PROCEDURE IF EXISTS buscar_atencion_medica_por_id;
DROP PROCEDURE IF EXISTS listar_atenciones_medicas;


CREATE PROCEDURE insertar_atencion_medica (
    IN p_fecha_hora DATETIME,
    IN p_motivo_consulta VARCHAR(45),
    IN p_peso_fisico DECIMAL(5,2),
    IN p_observaciones VARCHAR(45),
    IN p_id_mascota INT,
    IN p_id_cita_medica INT,
    IN p_activo TINYINT(1),
    OUT p_id INT
)
BEGIN
    INSERT INTO ATENCION_MEDICA (
        fecha_hora,
        motivo_consulta,
        peso_fisico,
        observaciones,
        id_mascota,
        id_cita_medica,
        activo
    )
    VALUES (
        p_fecha_hora,
        p_motivo_consulta,
        p_peso_fisico,
        p_observaciones,
        p_id_mascota,
        p_id_cita_medica,
        p_activo
    );

    SET p_id = LAST_INSERT_ID();
END;

CREATE PROCEDURE actualizar_atencion_medica (
    IN p_fecha_hora DATETIME,
    IN p_motivo_consulta VARCHAR(45),
    IN p_peso_fisico DECIMAL(5,2),
    IN p_observaciones VARCHAR(45),
    IN p_id_mascota INT,
    IN p_id_cita_medica INT,
    IN p_activo TINYINT(1),
    IN p_id INT
)
BEGIN
    UPDATE ATENCION_MEDICA
    SET
        fecha_hora = p_fecha_hora,
        motivo_consulta = p_motivo_consulta,
        peso_fisico = p_peso_fisico,
        observaciones = p_observaciones,
        id_mascota = p_id_mascota,
        id_cita_medica = p_id_cita_medica,
        activo = p_activo
    WHERE id_atencion_medica = p_id;
END;

CREATE PROCEDURE eliminar_atencion_medica (IN p_id INT)
BEGIN
    DELETE FROM ATENCION_MEDICA
    WHERE id_atencion_medica = p_id;
END;

CREATE PROCEDURE buscar_atencion_medica_por_id (IN p_id INT)
BEGIN
    SELECT *
    FROM ATENCION_MEDICA
    WHERE id_atencion_medica = p_id;
END;

CREATE PROCEDURE listar_atenciones_medicas ()
BEGIN
    SELECT *
    FROM ATENCION_MEDICA;
END;

