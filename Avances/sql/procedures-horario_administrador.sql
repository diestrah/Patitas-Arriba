use mydb;

DROP PROCEDURE IF EXISTS insertar_horario_administrador;
DROP PROCEDURE IF EXISTS modificar_horario_administrador;
DROP PROCEDURE IF EXISTS eliminar_horario_administrador;
DROP PROCEDURE IF EXISTS buscar_horario_administrador_por_id;
DROP PROCEDURE IF EXISTS listar_horarios_administrador;
DROP PROCEDURE IF EXISTS listar_horarios_por_administrador;
DROP PROCEDURE IF EXISTS eliminar_horarios_por_administrador;

DELIMITER //
CREATE PROCEDURE insertar_horario_administrador(
    IN p_id_administrador INT,
    IN p_id_horario INT,
    IN p_activo BOOLEAN,
    OUT p_id INT)
BEGIN
    INSERT INTO horario_administrador (
        id_administrador,
        id_horario,
        activo)
    VALUES (
        p_id_administrador,
        p_id_horario,
        p_activo);

    SET p_id = LAST_INSERT_ID();
END //

CREATE PROCEDURE modificar_horario_administrador(
    IN p_id_administrador INT,
    IN p_id_horario INT,
    IN p_activo BOOLEAN,
    IN p_id INT)
BEGIN
    UPDATE horario_administrador
    SET
        id_administrador = p_id_administrador,
        id_horario = p_id_horario,
        activo = p_activo
    WHERE id_horario_admin = p_id;
END //

CREATE PROCEDURE eliminar_horario_administrador(IN p_id INT)
BEGIN
    DELETE FROM horario_administrador WHERE id_horario_admin = p_id;
END //

CREATE PROCEDURE buscar_horario_administrador_por_id(IN p_id INT)
BEGIN
    SELECT * FROM horario_administrador WHERE id_horario_admin = p_id;
END //

CREATE PROCEDURE listar_horarios_administrador()
BEGIN
    SELECT * FROM horario_administrador;
END //

CREATE PROCEDURE listar_horarios_por_administrador(IN p_id_administrador INT)
BEGIN
    SELECT * FROM horario_administrador WHERE id_administrador = p_id_administrador;
END //

CREATE PROCEDURE eliminar_horarios_por_administrador(IN p_id_administrador INT)
BEGIN
    DELETE FROM horario_administrador WHERE id_administrador = p_id_administrador;
END //

DELIMITER ;