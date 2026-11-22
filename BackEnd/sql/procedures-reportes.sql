-- Procedimientos para los reportes JasperReports (RF13 agenda diaria y RF17 receta medica).
-- Cada reporte los llama con: CALL reporte_xxx(...)
-- Estilo tomado del profesor: un procedimiento por "cabecera" y otro por "detalle".

use patitasarriba;

DROP PROCEDURE IF EXISTS reporte_agenda_diaria;
DROP PROCEDURE IF EXISTS reporte_receta;
DROP PROCEDURE IF EXISTS reporte_detalle_receta;

DELIMITER //

-- Agenda de un dia (RF13). Misma regla que la pantalla y que CitaBLImpl (RN-CI-05, RN-CI-14):
--  * no entran las citas RECHAZADO ni CANCELADO (igual que AgendaClinica.OcupaAgenda del front);
--  * los servicios de un mismo veterinario en una cita van uno tras otro desde la hora de la
--    cita, cada uno con su duracion estimada (30 minutos si no la tiene);
--  * estado: Atendido (tiene atencion), Por aprobar (cita PENDIENTE) o Por atender;
--  * salen TODOS los veterinarios activos; el que no tiene servicios ese dia trae una fila con
--    horario, mascota, cliente, servicio y estado en NULL (el reporte escribe "sin servicios");
--  * p_id_veterinario es opcional: NULL trae a todos los veterinarios.
CREATE PROCEDURE reporte_agenda_diaria (
    IN p_fecha DATE,
    IN p_id_veterinario INT
)
BEGIN
    SELECT
        t.veterinario AS veterinario,
        CONCAT(TIME_FORMAT(t.inicio, '%H:%i'), ' - ',
               TIME_FORMAT(DATE_ADD(t.inicio, INTERVAL t.duracion MINUTE), '%H:%i')) AS horario,
        t.mascota AS mascota,
        t.cliente AS cliente,
        t.servicio AS servicio,
        t.estado AS estado,
        t.es_emergencia AS emergencia
    FROM (
        SELECT
            CONCAT(v.nombres, ' ', v.apellido_paterno) AS veterinario,
            m.nombre AS mascota,
            CONCAT(cl.nombres, ' ', cl.apellido_paterno, ' ', cl.apellido_materno) AS cliente,
            s.nombre AS servicio,
            x.es_emergencia AS es_emergencia,
            COALESCE(s.duracion_estimada, 30) AS duracion,
            -- inicio = hora de la cita + duracion de los servicios anteriores del mismo veterinario
            DATE_ADD(x.fecha_hora, INTERVAL COALESCE(
                SUM(COALESCE(s.duracion_estimada, 30)) OVER (
                    PARTITION BY x.id_cita, v.id_veterinario
                    ORDER BY x.id_detalle_cita
                    ROWS BETWEEN UNBOUNDED PRECEDING AND 1 PRECEDING), 0) MINUTE) AS inicio,
            CASE
                WHEN x.id_detalle_cita IS NULL THEN NULL
                WHEN EXISTS (SELECT 1 FROM atencion a
                             WHERE a.id_detalle_cita = x.id_detalle_cita AND a.activo = 1) THEN 'Atendido'
                WHEN x.estado = 'PENDIENTE' THEN 'Por aprobar'
                ELSE 'Por atender'
            END AS estado,
            v.id_veterinario AS id_veterinario
        FROM veterinario v
        LEFT JOIN (
            SELECT dc.id_detalle_cita, dc.id_cita, dc.id_veterinario, dc.id_servicio,
                   c.fecha_hora, c.estado, c.es_emergencia, c.id_mascota
            FROM detalle_cita dc
            INNER JOIN cita c ON c.id_cita = dc.id_cita
            WHERE dc.activo = 1
              AND c.activo = 1
              AND c.estado NOT IN ('RECHAZADO', 'CANCELADO')
              AND c.fecha_hora >= p_fecha
              AND c.fecha_hora < DATE_ADD(p_fecha, INTERVAL 1 DAY)
        ) x ON x.id_veterinario = v.id_veterinario
        LEFT JOIN mascota m ON m.id_mascota = x.id_mascota
        LEFT JOIN cliente cl ON cl.id_cliente = m.id_cliente
        LEFT JOIN servicio s ON s.id_servicio = x.id_servicio
        WHERE v.activo = 1
          AND (p_id_veterinario IS NULL OR v.id_veterinario = p_id_veterinario)
    ) t
    ORDER BY t.veterinario, t.inicio, t.mascota;
END //

-- Cabecera de la receta: datos de la receta, mascota, dueno y veterinario.
CREATE PROCEDURE reporte_receta (
    IN p_id_receta INT
)
BEGIN
    SELECT
        r.id_receta AS id_receta,
        r.fecha_emision AS fecha_emision,
        r.indicaciones_generales AS indicaciones,
        a.motivo_consulta AS motivo,
        m.nombre AS mascota,
        m.tipo_mascota AS tipo,
        m.raza AS raza,
        CONCAT(cl.nombres, ' ', cl.apellido_paterno, ' ', cl.apellido_materno) AS cliente,
        CONCAT(v.nombres, ' ', v.apellido_paterno, ' ', v.apellido_materno) AS veterinario,
        v.numero_colegiatura AS colegiatura
    FROM receta r
    INNER JOIN atencion a ON a.id_atencion = r.id_atencion
    INNER JOIN detalle_cita dc ON dc.id_detalle_cita = a.id_detalle_cita
    INNER JOIN cita c ON c.id_cita = dc.id_cita
    INNER JOIN mascota m ON m.id_mascota = c.id_mascota
    INNER JOIN cliente cl ON cl.id_cliente = m.id_cliente
    INNER JOIN veterinario v ON v.id_veterinario = dc.id_veterinario
    WHERE r.id_receta = p_id_receta
      AND r.activo = 1;
END //

-- Detalle de la receta: un renglon por medicamento.
CREATE PROCEDURE reporte_detalle_receta (
    IN p_id_receta INT
)
BEGIN
    SELECT
        ar.nombre AS medicamento,
        d.dosis AS dosis,
        d.frecuencia AS frecuencia,
        d.duracion_dias AS dias,
        d.cantidad_total AS cantidad
    FROM detalle_receta d
    INNER JOIN articulo ar ON ar.id_articulo = d.id_articulo
    WHERE d.id_receta = p_id_receta
      AND d.activo = 1
    ORDER BY d.id_detalle_receta;
END //

DELIMITER ;

-- Pruebas (ajusta los ids a datos que existan):
-- CALL reporte_agenda_diaria('2026-11-21', NULL);
-- CALL reporte_receta(1);
-- CALL reporte_detalle_receta(1);
