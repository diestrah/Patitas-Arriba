-- Procedimientos para los reportes JasperReports (RF12 agenda diaria, RF15 historial clinico y RF16 boleta).
-- Cada reporte los llama con: CALL reporte_xxx(...)
-- Estilo tomado del profesor: un procedimiento por "cabecera" y otro por "detalle".

use patitasarriba;

DROP PROCEDURE IF EXISTS reporte_agenda_diaria;
DROP PROCEDURE IF EXISTS reporte_historial_clinico;
DROP PROCEDURE IF EXISTS reporte_detalle_historial;
DROP PROCEDURE IF EXISTS reporte_boleta;
DROP PROCEDURE IF EXISTS reporte_detalle_boleta;
-- El reporte de receta se retiro (RF16 ahora es la boleta); se borran sus procedures si quedaron en la base
DROP PROCEDURE IF EXISTS reporte_receta;
DROP PROCEDURE IF EXISTS reporte_detalle_receta;

DELIMITER //

-- Agenda de un dia (RF12). Misma regla que la pantalla y que CitaBLImpl (RN-CI-05, RN-CI-14):
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

-- Historial clinico de una mascota (RF15), en estructura maestro-detalle (igual que la boleta):
--  * reporte_historial_clinico (maestro): una fila por atencion, en orden cronologico. Los datos
--    de la mascota y del dueno se repiten en cada fila; el reporte los pone en la cabecera.
--    Devuelve id_atencion, que el reporte maestro le pasa al subreporte.
--  * reporte_detalle_historial (detalle): diagnosticos, insumos y medicamentos de UNA atencion.
-- Solo salen atenciones realizadas: una cita aprobada sin atencion todavia no es historial.
CREATE PROCEDURE reporte_historial_clinico (
    IN p_id_mascota INT
)
BEGIN
    SELECT
        m.nombre AS mascota,
        m.tipo_mascota AS tipo,
        m.raza AS raza,
        m.sexo AS sexo,
        m.fecha_nacimiento AS fecha_nacimiento,
        m.peso AS peso,
        CONCAT(cl.nombres, ' ', cl.apellido_paterno, ' ', cl.apellido_materno) AS cliente,
        cl.dni AS dni_cliente,
        cl.telefono AS telefono_cliente,
        a.id_atencion AS id_atencion,
        a.fecha_hora AS fecha_atencion,
        s.nombre AS servicio,
        CONCAT(v.nombres, ' ', v.apellido_paterno) AS veterinario,
        a.motivo_consulta AS motivo,
        a.observaciones AS observaciones,
        (SELECT GROUP_CONCAT(r.indicaciones_generales SEPARATOR '\n')
           FROM receta r
          WHERE r.id_atencion = a.id_atencion
            AND r.activo = 1) AS indicaciones
    FROM mascota m
    INNER JOIN cliente cl ON cl.id_cliente = m.id_cliente
    INNER JOIN cita c ON c.id_mascota = m.id_mascota AND c.activo = 1
    INNER JOIN detalle_cita dc ON dc.id_cita = c.id_cita AND dc.activo = 1
    INNER JOIN atencion a ON a.id_detalle_cita = dc.id_detalle_cita AND a.activo = 1
    INNER JOIN servicio s ON s.id_servicio = dc.id_servicio
    INNER JOIN veterinario v ON v.id_veterinario = dc.id_veterinario
    WHERE m.id_mascota = p_id_mascota
    ORDER BY a.fecha_hora;
END //

-- Detalle del historial: lo que se registro en una atencion, en tres secciones.
--  * seccion: 1 = diagnosticos, 2 = insumos utilizados, 3 = medicamentos de la receta.
--    El reporte agrupa por seccion y pone el titulo de cada una.
--  * descripcion: diagnostico o articulo; detalle: gravedad, cantidad o indicacion de la toma.
--  * Si una seccion no tiene registros sale una fila con descripcion NULL, para que el reporte
--    escriba "Sin diagnosticos registrados", "Sin insumos utilizados" o "Sin receta".
CREATE PROCEDURE reporte_detalle_historial (
    IN p_id_atencion INT
)
BEGIN
    SELECT t.seccion, t.descripcion, t.detalle
    FROM (
        SELECT 1 AS seccion, d.nombre_enfermedad AS descripcion,
               ad.nivel_gravedad AS detalle, d.nombre_enfermedad AS orden
        FROM atencion_diagnostico ad
        INNER JOIN diagnostico d ON d.id_diagnostico = ad.id_diagnostico
        WHERE ad.id_atencion = p_id_atencion
          AND ad.activo = 1
        UNION ALL
        SELECT 2, ar.nombre,
               CONCAT('x', iu.cantidad_utilizada, IF(iu.es_cobrable = 1, ' (cobrable)', '')),
               ar.nombre
        FROM insumo_utilizado iu
        INNER JOIN articulo ar ON ar.id_articulo = iu.id_articulo
        WHERE iu.id_atencion = p_id_atencion
          AND iu.activo = 1
        UNION ALL
        SELECT 3, ar.nombre,
               CONCAT(dr.dosis, ', ', dr.frecuencia, ', ', dr.duracion_dias, ' dias'),
               LPAD(dr.id_detalle_receta, 10, '0')
        FROM receta r
        INNER JOIN detalle_receta dr ON dr.id_receta = r.id_receta AND dr.activo = 1
        INNER JOIN articulo ar ON ar.id_articulo = dr.id_articulo
        WHERE r.id_atencion = p_id_atencion
          AND r.activo = 1
        UNION ALL
        -- secciones vacias: una fila sin descripcion
        SELECT x.seccion, NULL, NULL, NULL
        FROM (SELECT 1 AS seccion UNION ALL SELECT 2 UNION ALL SELECT 3) x
        WHERE (x.seccion = 1 AND NOT EXISTS (SELECT 1 FROM atencion_diagnostico ad
                                              WHERE ad.id_atencion = p_id_atencion AND ad.activo = 1))
           OR (x.seccion = 2 AND NOT EXISTS (SELECT 1 FROM insumo_utilizado iu
                                              WHERE iu.id_atencion = p_id_atencion AND iu.activo = 1))
           OR (x.seccion = 3 AND NOT EXISTS (SELECT 1 FROM receta r
                                              INNER JOIN detalle_receta dr ON dr.id_receta = r.id_receta
                                                     AND dr.activo = 1
                                              WHERE r.id_atencion = p_id_atencion AND r.activo = 1))
    ) t
    ORDER BY t.seccion, t.orden;
END //

-- Boleta de venta (RF16), en estructura maestro-detalle:
--  * reporte_boleta (maestro): datos de la boleta y del cliente. Solo boletas emitidas: una
--    anulada ya no se imprime (RN-BO-12), asi que el reporte sale sin datos.
--  * reporte_detalle_boleta (detalle): servicios y articulos de la boleta.
-- Los precios incluyen IGV; el reporte calcula la operacion gravada y el IGV a partir del total.
CREATE PROCEDURE reporte_boleta (
    IN p_id_boleta INT
)
BEGIN
    SELECT
        b.id_boleta AS id_boleta,
        b.serie_correlativo AS serie_correlativo,
        b.fecha AS fecha,
        b.total AS total,
        b.metodo_pago AS metodo_pago,
        CONCAT(cl.nombres, ' ', cl.apellido_paterno, ' ', cl.apellido_materno) AS cliente,
        cl.dni AS dni_cliente,
        cl.telefono AS telefono_cliente
    FROM boleta b
    INNER JOIN cliente cl ON cl.id_cliente = b.id_cliente
    WHERE b.id_boleta = p_id_boleta
      AND b.activo = 1;
END //

-- Detalle de la boleta: un renglon por servicio o articulo.
--  * tipo: 1 = servicio (con la mascota y la fecha de la cita), 2 = articulo. El reporte agrupa
--    por tipo y pone el titulo de cada grupo.
--  * insumo = 1 cuando el articulo se uso en una atencion (insumo cobrable), 0 si se vendio en caja.
--  * subtotal es la columna generada cantidad * precio_unitario.
CREATE PROCEDURE reporte_detalle_boleta (
    IN p_id_boleta INT
)
BEGIN
    SELECT t.tipo, t.descripcion, t.insumo, t.cantidad, t.precio_unitario, t.subtotal
    FROM (
        SELECT 1 AS tipo,
               CONCAT(s.nombre, ' - ', m.nombre, ' (', DATE_FORMAT(c.fecha_hora, '%d/%m/%Y'), ')') AS descripcion,
               0 AS insumo,
               ds.cantidad AS cantidad,
               ds.precio_unitario AS precio_unitario,
               ds.subtotal AS subtotal,
               ds.id_detalle_boleta_serv AS orden
        FROM detalle_boleta_serv ds
        INNER JOIN detalle_cita dc ON dc.id_detalle_cita = ds.id_detalle_cita
        INNER JOIN servicio s ON s.id_servicio = dc.id_servicio
        INNER JOIN cita c ON c.id_cita = dc.id_cita
        INNER JOIN mascota m ON m.id_mascota = c.id_mascota
        WHERE ds.id_boleta = p_id_boleta
          AND ds.activo = 1
        UNION ALL
        SELECT 2, ar.nombre, IF(da.id_insumo_utilizado IS NULL, 0, 1),
               da.cantidad, da.precio_unitario, da.subtotal, da.id_detalle_boleta_art
        FROM detalle_boleta_art da
        INNER JOIN articulo ar ON ar.id_articulo = da.id_articulo
        WHERE da.id_boleta = p_id_boleta
          AND da.activo = 1
    ) t
    ORDER BY t.tipo, t.orden;
END //

DELIMITER ;

-- Pruebas (ajusta los ids a datos que existan):
-- CALL reporte_agenda_diaria('2026-11-21', NULL);
-- CALL reporte_historial_clinico(1);
-- CALL reporte_detalle_historial(1);   -- usa un id_atencion que devuelva la consulta anterior
-- CALL reporte_boleta(1);
-- CALL reporte_detalle_boleta(1);
