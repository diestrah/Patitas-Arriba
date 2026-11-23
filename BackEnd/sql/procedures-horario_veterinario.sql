-- Procedimientos para patitasarriba.modelo.usuario.Veterinario (lista de horarios)
-- Tabla de enlace horario_veterinario: clave compuesta (id_veterinario, id_horario)

use patitasarriba;

DROP PROCEDURE IF EXISTS insertar_horario_veterinario;
DROP PROCEDURE IF EXISTS modificar_horario_veterinario;
DROP PROCEDURE IF EXISTS eliminar_horario_veterinario;
DROP PROCEDURE IF EXISTS buscar_horario_veterinario_por_id;
DROP PROCEDURE IF EXISTS listar_horarios_veterinario;
DROP PROCEDURE IF EXISTS eliminar_horarios_por_veterinario;
DROP PROCEDURE IF EXISTS listar_horarios_por_veterinario;
DROP PROCEDURE IF EXISTS listar_veterinarios_por_horario;

DELIMITER //

-- Asigna un horario a un veterinario; si el enlace ya existia (inactivo), lo reactiva
CREATE PROCEDURE insertar_horario_veterinario (
    IN p_id_veterinario INT,
    IN p_id_horario INT
)
BEGIN
    INSERT INTO horario_veterinario (id_veterinario, id_horario, activo)
    VALUES (p_id_veterinario, p_id_horario, 1)
    ON DUPLICATE KEY UPDATE activo = 1;
END //

-- Borrado logico de una asignacion
CREATE PROCEDURE eliminar_horario_veterinario (
    IN p_id_veterinario INT,
    IN p_id_horario INT
)
BEGIN
    UPDATE horario_veterinario
    SET activo = 0
    WHERE id_veterinario = p_id_veterinario
      AND id_horario = p_id_horario;
END //

-- Borrado logico de todos los horarios de un veterinario
CREATE PROCEDURE eliminar_horarios_por_veterinario (IN p_id_veterinario INT)
BEGIN
    UPDATE horario_veterinario
    SET activo = 0
    WHERE id_veterinario = p_id_veterinario;
END //

-- Horarios activos de un veterinario (devuelve las columnas de horario)
CREATE PROCEDURE listar_horarios_por_veterinario (IN p_id_veterinario INT)
BEGIN
    SELECT h.*
    FROM horario h
    INNER JOIN horario_veterinario hv ON hv.id_horario = h.id_horario
    WHERE hv.id_veterinario = p_id_veterinario
      AND hv.activo = 1
      AND h.activo = 1;
END //

-- Veterinarios activos que tienen asignado un horario (devuelve las columnas de veterinario).
-- Util para validar la disponibilidad al reservar un servicio
CREATE PROCEDURE listar_veterinarios_por_horario (IN p_id_horario INT)
BEGIN
    SELECT v.*
    FROM veterinario v
    INNER JOIN horario_veterinario hv ON hv.id_veterinario = v.id_veterinario
    WHERE hv.id_horario = p_id_horario
      AND hv.activo = 1
      AND v.activo = 1;
END //

DELIMITER ;
