-- Procedimientos CRUD para patitasarriba.modelo.horario.Horario

use patitasarriba;

DROP PROCEDURE IF EXISTS insertar_horario;
DROP PROCEDURE IF EXISTS modificar_horario;
DROP PROCEDURE IF EXISTS eliminar_horario;
DROP PROCEDURE IF EXISTS buscar_horario_por_id;
DROP PROCEDURE IF EXISTS buscar_horario_por_dia;
DROP PROCEDURE IF EXISTS listar_horarios;

DELIMITER //

-- Crea un horario y devuelve el id generado
CREATE PROCEDURE insertar_horario (
    IN p_dia_semana VARCHAR(10),
    IN p_hora_inicio TIME,
    IN p_hora_fin TIME,
    IN p_activo TINYINT(1),
    OUT p_id INT
)
BEGIN
    INSERT INTO horario (dia_semana, hora_inicio, hora_fin, activo)
    VALUES (p_dia_semana, p_hora_inicio, p_hora_fin, p_activo);

    SET p_id = LAST_INSERT_ID();
END //

-- Actualiza todos los campos de un horario existente
CREATE PROCEDURE modificar_horario (
    IN p_dia_semana VARCHAR(10),
    IN p_hora_inicio TIME,
    IN p_hora_fin TIME,
    IN p_activo TINYINT(1),
    IN p_id INT
)
BEGIN
    UPDATE horario
    SET dia_semana = p_dia_semana,
        hora_inicio = p_hora_inicio,
        hora_fin = p_hora_fin,
        activo = p_activo
    WHERE id_horario = p_id;
END //

-- Borrado logico: el horario queda inactivo
CREATE PROCEDURE eliminar_horario (IN p_id INT)
BEGIN
    UPDATE horario
    SET activo = 0
    WHERE id_horario = p_id;
END //

-- Solo devuelve el horario si esta activo
CREATE PROCEDURE buscar_horario_por_id (IN p_id INT)
BEGIN
    SELECT *
    FROM horario
    WHERE id_horario = p_id
      AND activo = 1;
END //

-- Horarios activos de un dia de la semana
CREATE PROCEDURE buscar_horario_por_dia (IN p_dia_semana VARCHAR(10))
BEGIN
    SELECT *
    FROM horario
    WHERE dia_semana = p_dia_semana
      AND activo = 1;
END //

-- Solo lista los horarios activos (respeta el borrado logico)
CREATE PROCEDURE listar_horarios ()
BEGIN
    SELECT *
    FROM horario
    WHERE activo = 1;
END //

DELIMITER ;
