USE mydb;

DROP PROCEDURE IF EXISTS insertar_horario_veterinario;
DROP PROCEDURE IF EXISTS modificar_horario_veterinario;
DROP PROCEDURE IF EXISTS eliminar_horario_veterinario;
DROP PROCEDURE IF EXISTS buscar_horario_veterinario_por_id;
DROP PROCEDURE IF EXISTS listar_horarios_veterinario;
DROP PROCEDURE IF EXISTS listar_horarios_por_veterinario;
DROP PROCEDURE IF EXISTS eliminar_horarios_por_veterinario;

DELIMITER //

CREATE PROCEDURE insertar_horario_veterinario(
    IN p_id_veterinario INT,
    IN p_id_horario INT,
    IN p_activo BOOLEAN,
    OUT p_id INT)
BEGIN
    INSERT INTO horario_veterinario (
        id_veterinario,
        id_horario,
        activo)
    VALUES (
        p_id_veterinario,
        p_id_horario,
        p_activo);

    SET p_id = LAST_INSERT_ID();
END //

CREATE PROCEDURE modificar_horario_veterinario(
    IN p_id_veterinario INT,
    IN p_id_horario INT,
    IN p_activo BOOLEAN,
    IN p_id INT)
BEGIN
    UPDATE horario_veterinario
    SET
        id_veterinario = p_id_veterinario,
        id_horario = p_id_horario,
        activo = p_activo
    WHERE id_horario_vet = p_id;
END //

CREATE PROCEDURE eliminar_horario_veterinario(IN p_id INT)
BEGIN
    DELETE FROM horario_veterinario
    WHERE id_horario_vet = p_id;
END //

CREATE PROCEDURE buscar_horario_veterinario_por_id(IN p_id INT)
BEGIN
    SELECT *
    FROM horario_veterinario
    WHERE id_horario_vet = p_id;
END //

CREATE PROCEDURE listar_horarios_veterinario()
BEGIN
    SELECT *
    FROM horario_veterinario;
END //

CREATE PROCEDURE listar_horarios_por_veterinario(IN p_id_veterinario INT)
BEGIN
    SELECT *
    FROM horario_veterinario
    WHERE id_veterinario = p_id_veterinario;
END //

CREATE PROCEDURE eliminar_horarios_por_veterinario(IN p_id_veterinario INT)
BEGIN
    DELETE FROM horario_veterinario
    WHERE id_veterinario = p_id_veterinario;
END //

DELIMITER ;
