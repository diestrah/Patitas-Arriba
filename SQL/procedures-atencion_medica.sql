-- Procedimientos CRUD para patitasarriba.modelo.atencion.AtencionMedica

DROP PROCEDURE IF EXISTS insertar_atencion_medica;
DROP PROCEDURE IF EXISTS modificar_atencion_medica;
DROP PROCEDURE IF EXISTS eliminar_atencion_medica;
DROP PROCEDURE IF EXISTS buscar_atencion_medica_por_id;
DROP PROCEDURE IF EXISTS listar_atencion_medica;
DROP PROCEDURE IF EXISTS listar_atencion_medica_por_mascota;

DELIMITER //

-- Crea una atencion medica y devuelve el id generado
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
    INSERT INTO atencion_medica (
        fecha_hora, motivo_consulta, peso_fisico, observaciones,
        id_mascota, id_cita_medica, activo
    )
    VALUES (
        p_fecha_hora, p_motivo_consulta, p_peso_fisico, p_observaciones,
        p_id_mascota, p_id_cita_medica, p_activo
    );

    SET p_id = LAST_INSERT_ID();
END //

-- Actualiza todos los campos de una atencion medica existente
CREATE PROCEDURE modificar_atencion_medica (
    IN p_id INT,
    IN p_fecha_hora DATETIME,
    IN p_motivo_consulta VARCHAR(45),
    IN p_peso_fisico DECIMAL(5,2),
    IN p_observaciones VARCHAR(45),
    IN p_id_mascota INT,
    IN p_id_cita_medica INT,
    IN p_activo TINYINT(1)
)
BEGIN
    UPDATE atencion_medica
    SET fecha_hora = p_fecha_hora,
        motivo_consulta = p_motivo_consulta,
        peso_fisico = p_peso_fisico,
        observaciones = p_observaciones,
        id_mascota = p_id_mascota,
        id_cita_medica = p_id_cita_medica,
        activo = p_activo
    WHERE id_atencion_medica = p_id;
END //

CREATE PROCEDURE eliminar_atencion_medica (IN p_id INT)
BEGIN
    DELETE FROM atencion_medica
    WHERE id_atencion_medica = p_id;
END //

CREATE PROCEDURE buscar_atencion_medica_por_id (IN p_id INT)
BEGIN
    SELECT *
    FROM atencion_medica
    WHERE id_atencion_medica = p_id;
END //

CREATE PROCEDURE listar_atencion_medica ()
BEGIN
    SELECT *
    FROM atencion_medica;
END //

-- Historial clinico de UNA mascota especifica
CREATE PROCEDURE listar_atencion_medica_por_mascota (IN p_id_mascota INT)
BEGIN
    SELECT *
    FROM atencion_medica
    WHERE id_mascota = p_id_mascota;
END //

DELIMITER ;
