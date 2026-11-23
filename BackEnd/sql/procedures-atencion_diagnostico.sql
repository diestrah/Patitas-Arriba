-- Procedimientos para patitasarriba.modelo.atencion.AtencionDiagnostico
-- Tabla de enlace atencion_diagnostico: clave compuesta (id_atencion, id_diagnostico),
-- con datos propios (nivel_gravedad, detalle_diagnostico)

use patitasarriba;

DROP PROCEDURE IF EXISTS insertar_atencion_diagnostico;
DROP PROCEDURE IF EXISTS modificar_atencion_diagnostico;
DROP PROCEDURE IF EXISTS eliminar_atencion_diagnostico;
DROP PROCEDURE IF EXISTS eliminar_diagnosticos_por_atencion;
DROP PROCEDURE IF EXISTS eliminar_atencion_diagnosticos_por_atencion_medica;
DROP PROCEDURE IF EXISTS buscar_atencion_diagnostico;
DROP PROCEDURE IF EXISTS listar_diagnosticos_por_atencion;
DROP PROCEDURE IF EXISTS listar_atencion_diagnosticos_por_atencion_medica;
DROP PROCEDURE IF EXISTS listar_atenciones_por_diagnostico;

DELIMITER //

-- Asigna un diagnostico a una atencion; si el enlace ya existia (inactivo), lo reactiva
-- con la gravedad y el detalle nuevos
CREATE PROCEDURE insertar_atencion_diagnostico (
    IN p_id_atencion INT,
    IN p_id_diagnostico INT,
    IN p_nivel_gravedad VARCHAR(10),
    IN p_detalle_diagnostico VARCHAR(200)
)
BEGIN
    INSERT INTO atencion_diagnostico (
        id_atencion, id_diagnostico, nivel_gravedad, detalle_diagnostico, activo
    )
    VALUES (
        p_id_atencion, p_id_diagnostico, p_nivel_gravedad, p_detalle_diagnostico, 1
    )
    ON DUPLICATE KEY UPDATE
        nivel_gravedad = p_nivel_gravedad,
        detalle_diagnostico = p_detalle_diagnostico,
        activo = 1;
END //

-- Actualiza la gravedad y el detalle de un diagnostico ya asignado a una atencion
CREATE PROCEDURE modificar_atencion_diagnostico (
    IN p_id_atencion INT,
    IN p_id_diagnostico INT,
    IN p_nivel_gravedad VARCHAR(10),
    IN p_detalle_diagnostico VARCHAR(200),
    IN p_activo TINYINT(1)
)
BEGIN
    UPDATE atencion_diagnostico
    SET nivel_gravedad = p_nivel_gravedad,
        detalle_diagnostico = p_detalle_diagnostico,
        activo = p_activo
    WHERE id_atencion = p_id_atencion
      AND id_diagnostico = p_id_diagnostico;
END //

-- Borrado logico de un diagnostico de una atencion
CREATE PROCEDURE eliminar_atencion_diagnostico (
    IN p_id_atencion INT,
    IN p_id_diagnostico INT
)
BEGIN
    UPDATE atencion_diagnostico
    SET activo = 0
    WHERE id_atencion = p_id_atencion
      AND id_diagnostico = p_id_diagnostico;
END //

-- Borrado logico de todos los diagnosticos de UNA atencion (paso previo a eliminar la atencion,
-- dentro de la misma transaccion)
CREATE PROCEDURE eliminar_diagnosticos_por_atencion (IN p_id_atencion INT)
BEGIN
    UPDATE atencion_diagnostico
    SET activo = 0
    WHERE id_atencion = p_id_atencion;
END //

-- Solo devuelve el enlace si esta activo
CREATE PROCEDURE buscar_atencion_diagnostico (
    IN p_id_atencion INT,
    IN p_id_diagnostico INT
)
BEGIN
    SELECT *
    FROM atencion_diagnostico
    WHERE id_atencion = p_id_atencion
      AND id_diagnostico = p_id_diagnostico
      AND activo = 1;
END //

-- Diagnosticos activos de UNA atencion (devuelve las filas del enlace, con gravedad y detalle)
CREATE PROCEDURE listar_diagnosticos_por_atencion (IN p_id_atencion INT)
BEGIN
    SELECT *
    FROM atencion_diagnostico
    WHERE id_atencion = p_id_atencion
      AND activo = 1;
END //

-- Enlaces activos de un diagnostico (util para la regla "no eliminar un diagnostico en uso")
CREATE PROCEDURE listar_atenciones_por_diagnostico (IN p_id_diagnostico INT)
BEGIN
    SELECT *
    FROM atencion_diagnostico
    WHERE id_diagnostico = p_id_diagnostico
      AND activo = 1;
END //

DELIMITER ;
