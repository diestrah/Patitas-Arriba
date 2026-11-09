-- Procedimientos CRUD para patitasarriba.modelo.cita.DetalleCita

use patitasarriba;

DROP PROCEDURE IF EXISTS insertar_detalle_cita;
DROP PROCEDURE IF EXISTS modificar_detalle_cita;
DROP PROCEDURE IF EXISTS eliminar_detalle_cita;
DROP PROCEDURE IF EXISTS eliminar_detalles_por_cita;
DROP PROCEDURE IF EXISTS buscar_detalle_cita_por_id;
DROP PROCEDURE IF EXISTS listar_detalle_cita_por_cita;
DROP PROCEDURE IF EXISTS listar_detalle_cita_por_veterinario;

DELIMITER //

-- Crea un detalle de cita (un servicio pedido, con su veterinario) y devuelve el id generado
CREATE PROCEDURE insertar_detalle_cita (
    IN p_id_cita INT,
    IN p_id_servicio INT,
    IN p_id_veterinario INT,
    IN p_observaciones VARCHAR(200),
    IN p_activo TINYINT(1),
    OUT p_id INT
)
BEGIN
    INSERT INTO detalle_cita (id_cita, id_servicio, id_veterinario, observaciones, activo)
    VALUES (p_id_cita, p_id_servicio, p_id_veterinario, p_observaciones, p_activo);

    SET p_id = LAST_INSERT_ID();
END //

-- Actualiza un detalle existente (id_cita no cambia: un detalle no se muda de cita)
CREATE PROCEDURE modificar_detalle_cita (
    IN p_id INT,
    IN p_id_servicio INT,
    IN p_id_veterinario INT,
    IN p_observaciones VARCHAR(200),
    IN p_activo TINYINT(1)
)
BEGIN
    UPDATE detalle_cita
    SET id_servicio = p_id_servicio,
        id_veterinario = p_id_veterinario,
        observaciones = p_observaciones,
        activo = p_activo
    WHERE id_detalle_cita = p_id;
END //

-- Borrado logico de un detalle
CREATE PROCEDURE eliminar_detalle_cita (IN p_id INT)
BEGIN
    UPDATE detalle_cita
    SET activo = 0
    WHERE id_detalle_cita = p_id;
END //

-- Borrado logico de todos los detalles de UNA cita (paso previo a eliminar la cita misma,
-- dentro de la misma transaccion)
CREATE PROCEDURE eliminar_detalles_por_cita (IN p_id_cita INT)
BEGIN
    UPDATE detalle_cita
    SET activo = 0
    WHERE id_cita = p_id_cita;
END //

-- Solo devuelve el detalle si esta activo
CREATE PROCEDURE buscar_detalle_cita_por_id (IN p_id INT)
BEGIN
    SELECT *
    FROM detalle_cita
    WHERE id_detalle_cita = p_id
      AND activo = 1;
END //

-- Lista los detalles activos de UNA cita especifica (patron maestro-detalle)
CREATE PROCEDURE listar_detalle_cita_por_cita (IN p_id_cita INT)
BEGIN
    SELECT *
    FROM detalle_cita
    WHERE id_cita = p_id_cita
      AND activo = 1;
END //

-- Detalles activos asignados a un veterinario (util para validar disponibilidad
-- al reservar y para la regla "no eliminar un veterinario con servicios pendientes")
CREATE PROCEDURE listar_detalle_cita_por_veterinario (IN p_id_veterinario INT)
BEGIN
    SELECT *
    FROM detalle_cita
    WHERE id_veterinario = p_id_veterinario
      AND activo = 1;
END //

DELIMITER ;
