-- Actualizacion de rendimiento para una base YA instalada (local o la de AWS).
-- Agrega los procedures que filtran citas en la base de datos (listar_cita_por_cliente y
-- listar_cita_por_rango). Ejecutarlo una vez ANTES de desplegar el backend nuevo: sin ellos
-- fallan "Mis citas", "Mi agenda", la agenda de citas y el registro de citas.
-- Solo agrega: no modifica ninguna tabla ni procedure existente. Se genera a partir de
-- procedures-cita.sql; si esos procedures cambian, hay que volver a generarlo.

use patitasarriba;

DROP PROCEDURE IF EXISTS listar_cita_por_cliente;
DROP PROCEDURE IF EXISTS listar_cita_por_rango;

DELIMITER //

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
-- algun servicio activo con ese veterinario; con NULL, de todos (RF11, RF12 y cruces de agenda)
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
