USE mydb;

DROP PROCEDURE IF EXISTS insertar_horario;
DROP PROCEDURE IF EXISTS modificar_horario;
DROP PROCEDURE IF EXISTS eliminar_horario;
DROP PROCEDURE IF EXISTS buscar_horario_por_id;
DROP PROCEDURE IF EXISTS buscar_horario_por_dia;
DROP PROCEDURE IF EXISTS listar_horarios;

DELIMITER //

CREATE PROCEDURE insertar_horario(
    IN p_dia_semana VARCHAR(10),
    IN p_hora_inicio TIME,
    IN p_hora_fin TIME,
    IN p_activo TINYINT(1),
    OUT p_id INT
)
BEGIN
    INSERT INTO HORARIO (
        dia_semana,
        hora_inicio,
        hora_fin,
        activo
    )
    VALUES (
        p_dia_semana,
        p_hora_inicio,
        p_hora_fin,
        p_activo
    );

    SET p_id = LAST_INSERT_ID();
END //

CREATE PROCEDURE modificar_horario(
    IN p_dia_semana VARCHAR(10),
    IN p_hora_inicio TIME,
    IN p_hora_fin TIME,
    IN p_activo TINYINT(1),
    IN p_id INT
)
BEGIN
    UPDATE HORARIO
    SET 
        dia_semana = p_dia_semana,
        hora_inicio = p_hora_inicio,
        hora_fin = p_hora_fin,
        activo = p_activo
    WHERE id_horario = p_id;
END //

CREATE PROCEDURE eliminar_horario(IN p_id INT)
BEGIN
    DELETE FROM HORARIO
    WHERE id_horario = p_id;
END //

CREATE PROCEDURE buscar_horario_por_id(IN p_id INT)
BEGIN
    SELECT *
    FROM HORARIO
    WHERE id_horario = p_id;
END //

CREATE PROCEDURE buscar_horario_por_dia(IN p_dia_semana VARCHAR(10))
BEGIN
    SELECT *
    FROM HORARIO
    WHERE dia_semana = p_dia_semana;
END //

CREATE PROCEDURE listar_horarios()
BEGIN
    SELECT *
    FROM HORARIO;
END //

DELIMITER ;