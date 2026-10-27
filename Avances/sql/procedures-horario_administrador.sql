-- Procedimientos para patitasarriba.modelo.usuario.Administrador (lista de horarios)
-- Tabla de enlace horario_administrador: clave compuesta (id_administrador, id_horario)

use patitasarriba;

DROP PROCEDURE IF EXISTS insertar_horario_administrador;
DROP PROCEDURE IF EXISTS modificar_horario_administrador;
DROP PROCEDURE IF EXISTS eliminar_horario_administrador;
DROP PROCEDURE IF EXISTS buscar_horario_administrador_por_id;
DROP PROCEDURE IF EXISTS listar_horarios_administrador;
DROP PROCEDURE IF EXISTS eliminar_horarios_por_administrador;
DROP PROCEDURE IF EXISTS listar_horarios_por_administrador;
DROP PROCEDURE IF EXISTS listar_administradores_por_horario;

DELIMITER //

-- Asigna un horario a un administrador; si el enlace ya existia (inactivo), lo reactiva
CREATE PROCEDURE insertar_horario_administrador (
    IN p_id_administrador INT,
    IN p_id_horario INT
)
BEGIN
    INSERT INTO horario_administrador (id_administrador, id_horario, activo)
    VALUES (p_id_administrador, p_id_horario, 1)
    ON DUPLICATE KEY UPDATE activo = 1;
END //

-- Borrado logico de una asignacion
CREATE PROCEDURE eliminar_horario_administrador (
    IN p_id_administrador INT,
    IN p_id_horario INT
)
BEGIN
    UPDATE horario_administrador
    SET activo = 0
    WHERE id_administrador = p_id_administrador
      AND id_horario = p_id_horario;
END //

-- Borrado logico de todos los horarios de un administrador
CREATE PROCEDURE eliminar_horarios_por_administrador (IN p_id_administrador INT)
BEGIN
    UPDATE horario_administrador
    SET activo = 0
    WHERE id_administrador = p_id_administrador;
END //

-- Horarios activos de un administrador (devuelve las columnas de horario)
CREATE PROCEDURE listar_horarios_por_administrador (IN p_id_administrador INT)
BEGIN
    SELECT h.*
    FROM horario h
    INNER JOIN horario_administrador ha ON ha.id_horario = h.id_horario
    WHERE ha.id_administrador = p_id_administrador
      AND ha.activo = 1
      AND h.activo = 1;
END //

-- Administradores activos que tienen asignado un horario (devuelve las columnas de administrador)
CREATE PROCEDURE listar_administradores_por_horario (IN p_id_horario INT)
BEGIN
    SELECT a.*
    FROM administrador a
    INNER JOIN horario_administrador ha ON ha.id_administrador = a.id_administrador
    WHERE ha.id_horario = p_id_horario
      AND ha.activo = 1
      AND a.activo = 1;
END //

DELIMITER ;
