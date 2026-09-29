-- Procedimientos CRUD para patitasarriba.modelo.atencion.Diagnostico

DROP PROCEDURE IF EXISTS insertar_diagnostico;
DROP PROCEDURE IF EXISTS modificar_diagnostico;
DROP PROCEDURE IF EXISTS eliminar_diagnostico;
DROP PROCEDURE IF EXISTS buscar_diagnostico_por_id;
DROP PROCEDURE IF EXISTS listar_diagnostico;

DELIMITER //

-- Crea un diagnostico y devuelve el id generado
CREATE PROCEDURE insertar_diagnostico (
    IN p_nombre_enfermedad VARCHAR(45),
    IN p_descripcion VARCHAR(45),
    IN p_activo TINYINT(1),
    OUT p_id INT
)
BEGIN
    INSERT INTO diagnostico (nombre_enfermedad, descripcion, activo)
    VALUES (p_nombre_enfermedad, p_descripcion, p_activo);

    SET p_id = LAST_INSERT_ID();
END //

-- Actualiza todos los campos de un diagnostico existente
CREATE PROCEDURE modificar_diagnostico (
    IN p_id INT,
    IN p_nombre_enfermedad VARCHAR(45),
    IN p_descripcion VARCHAR(45),
    IN p_activo TINYINT(1)
)
BEGIN
    UPDATE diagnostico
    SET nombre_enfermedad = p_nombre_enfermedad,
        descripcion = p_descripcion,
        activo = p_activo
    WHERE id_diagnostico = p_id;
END //

CREATE PROCEDURE eliminar_diagnostico (IN p_id INT)
BEGIN
    DELETE FROM diagnostico
    WHERE id_diagnostico = p_id;
END //

CREATE PROCEDURE buscar_diagnostico_por_id (IN p_id INT)
BEGIN
    SELECT *
    FROM diagnostico
    WHERE id_diagnostico = p_id;
END //

CREATE PROCEDURE listar_diagnostico ()
BEGIN
    SELECT *
    FROM diagnostico;
END //

DELIMITER ;
