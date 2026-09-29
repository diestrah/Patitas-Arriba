-- Procedimientos CRUD para patitasarriba.modelo.atencion.AtencionDiagnostico

DROP PROCEDURE IF EXISTS insertar_atencion_diagnostico;
DROP PROCEDURE IF EXISTS modificar_atencion_diagnostico;
DROP PROCEDURE IF EXISTS eliminar_atencion_diagnostico;
DROP PROCEDURE IF EXISTS buscar_atencion_diagnostico_por_id;
DROP PROCEDURE IF EXISTS listar_atencion_diagnostico_por_atencion_medica;

DELIMITER //

-- Crea un diagnostico asociado a una atencion medica y devuelve el id generado
CREATE PROCEDURE insertar_atencion_diagnostico (
    IN p_id_atencion_medica INT,
    IN p_id_diagnostico INT,
    IN p_nivel_gravedad VARCHAR(45),
    IN p_detalle_diagnostico VARCHAR(200),
    IN p_activo TINYINT(1),
    OUT p_id INT
)
BEGIN
    INSERT INTO atencion_diagnostico (id_atencion_medica, id_diagnostico, nivel_gravedad, detalle_diagnostico, activo)
    VALUES (p_id_atencion_medica, p_id_diagnostico, p_nivel_gravedad, p_detalle_diagnostico, p_activo);

    SET p_id = LAST_INSERT_ID();
END //

-- Actualiza un diagnostico de atencion existente (id_atencion_medica no cambia: un diagnostico no se muda de atencion)
CREATE PROCEDURE modificar_atencion_diagnostico (
    IN p_id INT,
    IN p_id_diagnostico INT,
    IN p_nivel_gravedad VARCHAR(45),
    IN p_detalle_diagnostico VARCHAR(200),
    IN p_activo TINYINT(1)
)
BEGIN
    UPDATE atencion_diagnostico
    SET id_diagnostico = p_id_diagnostico,
        nivel_gravedad = p_nivel_gravedad,
        detalle_diagnostico = p_detalle_diagnostico,
        activo = p_activo
    WHERE id_atencion_diagnostico = p_id;
END //

CREATE PROCEDURE eliminar_atencion_diagnostico (IN p_id INT)
BEGIN
    DELETE FROM atencion_diagnostico
    WHERE id_atencion_diagnostico = p_id;
END //

CREATE PROCEDURE buscar_atencion_diagnostico_por_id (IN p_id INT)
BEGIN
    SELECT *
    FROM atencion_diagnostico
    WHERE id_atencion_diagnostico = p_id;
END //

-- Lista los diagnosticos de UNA atencion medica especifica (patron maestro-detalle)
CREATE PROCEDURE listar_atencion_diagnostico_por_atencion_medica (IN p_id_atencion_medica INT)
BEGIN
    SELECT *
    FROM atencion_diagnostico
    WHERE id_atencion_medica = p_id_atencion_medica;
END //

DELIMITER ;
