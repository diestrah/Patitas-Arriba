-- Procedimientos CRUD para patitasarriba.modelo.cita.Cita
-- Los servicios de la cita (detalles) se manejan en procedures-detalle_cita.sql

use patitasarriba;

DROP PROCEDURE IF EXISTS insertar_cita;
DROP PROCEDURE IF EXISTS modificar_cita;
DROP PROCEDURE IF EXISTS eliminar_cita;
DROP PROCEDURE IF EXISTS buscar_cita_por_id;
DROP PROCEDURE IF EXISTS listar_cita;
DROP PROCEDURE IF EXISTS listar_cita_por_mascota;
DROP PROCEDURE IF EXISTS listar_cita_por_cliente;
DROP PROCEDURE IF EXISTS listar_cita_por_rango;

DELIMITER //

-- Crea una cita y devuelve el id generado.
-- p_id_administrador puede ser NULL: lo asigna quien aprueba o rechaza la cita
CREATE PROCEDURE insertar_cita (
    IN p_fecha_hora DATETIME,
    IN p_estado VARCHAR(20),
    IN p_id_mascota INT,
    IN p_id_administrador INT,
    IN p_es_emergencia TINYINT(1),
    IN p_activo TINYINT(1),
    OUT p_id INT
)
BEGIN
    INSERT INTO cita (fecha_hora, estado, id_mascota, id_administrador, es_emergencia, activo)
    VALUES (p_fecha_hora, p_estado, p_id_mascota, p_id_administrador, p_es_emergencia, p_activo);

    SET p_id = LAST_INSERT_ID();
END //

-- Actualiza todos los campos de una cita existente
CREATE PROCEDURE modificar_cita (
    IN p_id INT,
    IN p_fecha_hora DATETIME,
    IN p_estado VARCHAR(20),
    IN p_id_mascota INT,
    IN p_id_administrador INT,
    IN p_es_emergencia TINYINT(1),
    IN p_activo TINYINT(1)
)
BEGIN
    UPDATE cita
    SET fecha_hora = p_fecha_hora,
        estado = p_estado,
        id_mascota = p_id_mascota,
        id_administrador = p_id_administrador,
        es_emergencia = p_es_emergencia,
        activo = p_activo
    WHERE id_cita = p_id;
END //

-- Borrado logico: la cita queda inactiva
-- (cancelar una cita es otra cosa: se hace con modificar_cita, estado = 'CANCELADO')
CREATE PROCEDURE eliminar_cita (IN p_id INT)
BEGIN
    UPDATE cita
    SET activo = 0
    WHERE id_cita = p_id;
END //

-- Solo devuelve la cita si esta activa
CREATE PROCEDURE buscar_cita_por_id (IN p_id INT)
BEGIN
    SELECT *
    FROM cita
    WHERE id_cita = p_id
      AND activo = 1;
END //

-- Solo lista las citas activas (respeta el borrado logico)
CREATE PROCEDURE listar_cita ()
BEGIN
    SELECT *
    FROM cita
    WHERE activo = 1;
END //

-- Citas activas de una mascota (util para la regla "no eliminar una mascota con citas activas")
CREATE PROCEDURE listar_cita_por_mascota (IN p_id_mascota INT)
BEGIN
    SELECT *
    FROM cita
    WHERE id_mascota = p_id_mascota
      AND activo = 1;
END //

-- Citas activas de las mascotas de un cliente (RF10: "Mis citas" y el inicio del cliente)
CREATE PROCEDURE listar_cita_por_cliente (IN p_id_cliente INT)
BEGIN
    SELECT c.*
    FROM cita c
    INNER JOIN mascota m ON m.id_mascota = c.id_mascota
    WHERE m.id_cliente = p_id_cliente
      AND m.activo = 1
      AND c.activo = 1
    ORDER BY c.fecha_hora;
END //

-- Citas activas con fecha_hora en [p_desde, p_hasta). Con p_id_veterinario, solo las que tienen
-- algun servicio activo con ese veterinario; con NULL, de todos (RF11, RF13 y cruces de agenda)
CREATE PROCEDURE listar_cita_por_rango (
    IN p_desde DATETIME,
    IN p_hasta DATETIME,
    IN p_id_veterinario INT
)
BEGIN
    SELECT c.*
    FROM cita c
    WHERE c.fecha_hora >= p_desde
      AND c.fecha_hora < p_hasta
      AND c.activo = 1
      AND (p_id_veterinario IS NULL OR EXISTS (
            SELECT 1
            FROM detalle_cita dc
            WHERE dc.id_cita = c.id_cita
              AND dc.id_veterinario = p_id_veterinario
              AND dc.activo = 1))
    ORDER BY c.fecha_hora;
END //

DELIMITER ;
