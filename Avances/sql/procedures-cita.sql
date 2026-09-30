-- Procedimientos CRUD para patitasarriba.modelo.cita.Cita

DROP PROCEDURE IF EXISTS insertar_cita;
DROP PROCEDURE IF EXISTS modificar_cita;
DROP PROCEDURE IF EXISTS eliminar_cita;
DROP PROCEDURE IF EXISTS buscar_cita_por_id;
DROP PROCEDURE IF EXISTS listar_cita;

DELIMITER //

-- Crea una cita y devuelve el id generado
CREATE PROCEDURE insertar_cita (
    IN p_fecha_hora DATETIME,
    IN p_estado VARCHAR(20),
    IN p_id_mascota INT,
    IN p_id_veterinario INT,
    IN p_activo TINYINT(1),
    OUT p_id INT
)
BEGIN
    INSERT INTO cita (fecha_hora, estado, id_mascota, id_veterinario, activo)
    VALUES (p_fecha_hora, p_estado, p_id_mascota, p_id_veterinario, p_activo);

    SET p_id = LAST_INSERT_ID();
END //

-- Actualiza todos los campos de una cita existente
CREATE PROCEDURE modificar_cita (
    IN p_id INT,
    IN p_fecha_hora DATETIME,
    IN p_estado VARCHAR(20),
    IN p_id_mascota INT,
    IN p_id_veterinario INT,
    IN p_activo TINYINT(1)
)
BEGIN
    UPDATE cita
    SET fecha_hora = p_fecha_hora,
        estado = p_estado,
        id_mascota = p_id_mascota,
        id_veterinario = p_id_veterinario,
        activo = p_activo
    WHERE id_cita = p_id;
END //

CREATE PROCEDURE eliminar_cita (IN p_id INT)
BEGIN
    DELETE FROM cita
    WHERE id_cita = p_id;
END //

CREATE PROCEDURE buscar_cita_por_id (IN p_id INT)
BEGIN
    SELECT *
    FROM cita
    WHERE id_cita = p_id;
END //

-- Solo lista las citas activas (respeta el borrado suave)
CREATE PROCEDURE listar_cita ()
BEGIN
    SELECT *
    FROM cita
    WHERE activo = 1;
END //

DELIMITER ;
