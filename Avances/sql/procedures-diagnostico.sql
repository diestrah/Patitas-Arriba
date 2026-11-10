-- Procedimientos CRUD para patitasarriba.modelo.atencion.Diagnostico

use patitasarriba;

DROP PROCEDURE IF EXISTS insertar_diagnostico;
DROP PROCEDURE IF EXISTS modificar_diagnostico;
DROP PROCEDURE IF EXISTS actualizar_diagnostico;
DROP PROCEDURE IF EXISTS eliminar_diagnostico;
DROP PROCEDURE IF EXISTS buscar_diagnostico_por_id;
DROP PROCEDURE IF EXISTS buscar_diagnostico_por_nombre_enfermedad;
DROP PROCEDURE IF EXISTS listar_diagnosticos;

DELIMITER //

-- Crea un diagnostico y devuelve el id generado
CREATE PROCEDURE insertar_diagnostico (
    IN p_nombre_enfermedad VARCHAR(80),
    IN p_descripcion VARCHAR(200),
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
    IN p_nombre_enfermedad VARCHAR(80),
    IN p_descripcion VARCHAR(200),
    IN p_activo TINYINT(1)
)
BEGIN
    UPDATE diagnostico
    SET nombre_enfermedad = p_nombre_enfermedad,
        descripcion = p_descripcion,
        activo = p_activo
    WHERE id_diagnostico = p_id;
END //

-- Borrado logico: el diagnostico queda inactivo
CREATE PROCEDURE eliminar_diagnostico (IN p_id INT)
BEGIN
    UPDATE diagnostico
    SET activo = 0
    WHERE id_diagnostico = p_id;
END //

-- Solo devuelve el diagnostico si esta activo
CREATE PROCEDURE buscar_diagnostico_por_id (IN p_id INT)
BEGIN
    SELECT *
    FROM diagnostico
    WHERE id_diagnostico = p_id
      AND activo = 1;
END //

-- nombre_enfermedad es UNIQUE: no filtra activo, para detectar tambien los diagnosticos inactivos
CREATE PROCEDURE buscar_diagnostico_por_nombre_enfermedad (IN p_nombre_enfermedad VARCHAR(80))
BEGIN
    SELECT *
    FROM diagnostico
    WHERE nombre_enfermedad = p_nombre_enfermedad;
END //

-- Solo lista los diagnosticos activos (respeta el borrado logico)
CREATE PROCEDURE listar_diagnosticos ()
BEGIN
    SELECT *
    FROM diagnostico
    WHERE activo = 1;
END //

DELIMITER ;
