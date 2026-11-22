-- =====================================================================================
-- Patitas Arriba: datos extra para la semana de la exposicion (16 al 26 de noviembre 2026)
--
-- * Correr DESPUES de instalar-desde-cero.sql (necesita sus datos de prueba) y UNA sola vez.
--   Si se corre dos veces, falla en la primera linea (correo repetido) y no cambia nada.
-- * Todas las cuentas nuevas usan el password  Patitas123.
-- * Domingo no hay atencion (no hay horario). Horarios de los datos de prueba:
--     Lucia : mananas de lunes a viernes (08:00-14:00)
--     Martin: tardes de lunes a viernes (14:00-20:00)
--     Carmen: lunes, miercoles y viernes en la manana + sabado (09:00-13:00)
-- * Semana pasada (lun 16 a sab 21): citas COMPLETADO con atencion, diagnosticos, insumos,
--   recetas y boletas. Tres quedan SIN COBRAR para mostrar "pendientes de cobro" en Caja.
--   Tambien cierra las 4 citas de prueba de esa semana que seguian PENDIENTE o APROBADO.
-- * Esta semana (lun 23 a jue 26): citas PENDIENTE y APROBADO dentro del horario y sin cruces.
--   Miercoles 25 (exposicion 18:00): Firulais (Ana) APROBADO 16:30 para atender en vivo y
--   Nala (Ana) PENDIENTE 19:30 para aprobar en vivo.
-- * stock_actual se ajusta con los insumos usados y lo vendido en caja.
-- =====================================================================================
USE patitasarriba;

START TRANSACTION;

SET @pwd = TO_BASE64(UNHEX(SHA2('Patitas123', 256)));

-- -------------------------------------------------------------------------------------
-- CUENTAS Y CLIENTES NUEVOS (5)
-- -------------------------------------------------------------------------------------
INSERT INTO cuenta (correo, password, fecha_creacion, nombre_usuario) VALUES ('carlos.huaman@gmail.com',   @pwd, '2026-10-02', 'carlos.huaman');   SET @cu_cli6  = LAST_INSERT_ID();
INSERT INTO cuenta (correo, password, fecha_creacion, nombre_usuario) VALUES ('valeria.castro@gmail.com',  @pwd, '2026-10-09', 'valeria.castro');  SET @cu_cli7  = LAST_INSERT_ID();
INSERT INTO cuenta (correo, password, fecha_creacion, nombre_usuario) VALUES ('diego.salazar@hotmail.com', @pwd, '2026-10-15', 'diego.salazar');   SET @cu_cli8  = LAST_INSERT_ID();
INSERT INTO cuenta (correo, password, fecha_creacion, nombre_usuario) VALUES ('camila.rojas@gmail.com',    @pwd, '2026-10-28', 'camila.rojas');    SET @cu_cli9  = LAST_INSERT_ID();
INSERT INTO cuenta (correo, password, fecha_creacion, nombre_usuario) VALUES ('renzo.vargas@outlook.com',  @pwd, '2026-11-04', 'renzo.vargas');    SET @cu_cli10 = LAST_INSERT_ID();

INSERT INTO cliente (id_cuenta, dni, nombres, apellido_paterno, apellido_materno, telefono)
VALUES (@cu_cli6,  '75123456', 'Carlos Andres', 'Huaman',  'Rios',   '955300406'); SET @cli6  = LAST_INSERT_ID();
INSERT INTO cliente (id_cuenta, dni, nombres, apellido_paterno, apellido_materno, telefono)
VALUES (@cu_cli7,  '76234567', 'Valeria',       'Castro',  'Ponce',  '955300407'); SET @cli7  = LAST_INSERT_ID();
INSERT INTO cliente (id_cuenta, dni, nombres, apellido_paterno, apellido_materno, telefono)
VALUES (@cu_cli8,  '77345678', 'Diego Martin',  'Salazar', 'Vargas', '955300408'); SET @cli8  = LAST_INSERT_ID();
INSERT INTO cliente (id_cuenta, dni, nombres, apellido_paterno, apellido_materno, telefono)
VALUES (@cu_cli9,  '78456789', 'Camila',        'Rojas',   'Medina', '955300409'); SET @cli9  = LAST_INSERT_ID();
INSERT INTO cliente (id_cuenta, dni, nombres, apellido_paterno, apellido_materno, telefono)
VALUES (@cu_cli10, '79567890', 'Renzo',         'Vargas',  'Leon',   '955300410'); SET @cli10 = LAST_INSERT_ID();

-- -------------------------------------------------------------------------------------
-- IDS QUE YA EXISTEN (de datos-prueba.sql), buscados por datos unicos
-- -------------------------------------------------------------------------------------
SET @adm1 = (SELECT a.id_administrador FROM administrador a JOIN cuenta cu ON cu.id_cuenta = a.id_cuenta WHERE cu.correo = 'rosa.admin@patitas.pe');
SET @adm2 = (SELECT a.id_administrador FROM administrador a JOIN cuenta cu ON cu.id_cuenta = a.id_cuenta WHERE cu.correo = 'jorge.admin@patitas.pe');
SET @vet1 = (SELECT v.id_veterinario FROM veterinario v JOIN cuenta cu ON cu.id_cuenta = v.id_cuenta WHERE cu.correo = 'dra.lucia@patitas.pe');
SET @vet2 = (SELECT v.id_veterinario FROM veterinario v JOIN cuenta cu ON cu.id_cuenta = v.id_cuenta WHERE cu.correo = 'dr.martin@patitas.pe');
SET @vet3 = (SELECT v.id_veterinario FROM veterinario v JOIN cuenta cu ON cu.id_cuenta = v.id_cuenta WHERE cu.correo = 'dra.carmen@patitas.pe');

SET @cli1 = (SELECT id_cliente FROM cliente WHERE dni = '70123456');
SET @cli2 = (SELECT id_cliente FROM cliente WHERE dni = '71234567');
SET @cli3 = (SELECT id_cliente FROM cliente WHERE dni = '72345678');
SET @cli4 = (SELECT id_cliente FROM cliente WHERE dni = '73456789');
SET @cli5 = (SELECT id_cliente FROM cliente WHERE dni = '74567890');

SET @m_firulais = (SELECT id_mascota FROM mascota WHERE nombre = 'Firulais' AND id_cliente = @cli1);
SET @m_nala     = (SELECT id_mascota FROM mascota WHERE nombre = 'Nala'     AND id_cliente = @cli1);
SET @m_michi    = (SELECT id_mascota FROM mascota WHERE nombre = 'Michi'    AND id_cliente = @cli2);
SET @m_rocky    = (SELECT id_mascota FROM mascota WHERE nombre = 'Rocky'    AND id_cliente = @cli2);
SET @m_luna     = (SELECT id_mascota FROM mascota WHERE nombre = 'Luna'     AND id_cliente = @cli3);
SET @m_toby     = (SELECT id_mascota FROM mascota WHERE nombre = 'Toby'     AND id_cliente = @cli4);
SET @m_pelusa   = (SELECT id_mascota FROM mascota WHERE nombre = 'Pelusa'   AND id_cliente = @cli4);
SET @m_max      = (SELECT id_mascota FROM mascota WHERE nombre = 'Max'      AND id_cliente = @cli5);

SET @s_consulta = (SELECT id_servicio FROM servicio WHERE nombre = 'Consulta general');
SET @s_control  = (SELECT id_servicio FROM servicio WHERE nombre = 'Control de seguimiento');
SET @s_rabia    = (SELECT id_servicio FROM servicio WHERE nombre = 'Vacuna antirrabica');
SET @s_triple   = (SELECT id_servicio FROM servicio WHERE nombre = 'Vacuna triple felina');
SET @s_despa    = (SELECT id_servicio FROM servicio WHERE nombre = 'Desparasitacion');
SET @s_bano     = (SELECT id_servicio FROM servicio WHERE nombre = 'Bano y corte');
SET @s_ester    = (SELECT id_servicio FROM servicio WHERE nombre = 'Esterilizacion');
SET @s_emerg    = (SELECT id_servicio FROM servicio WHERE nombre = 'Atencion de emergencia');

SET @a_ali_gato = (SELECT id_articulo FROM articulo WHERE nombre = 'Alimento gato 3kg');
SET @a_snack    = (SELECT id_articulo FROM articulo WHERE nombre = 'Snack dental perro x10');
SET @a_amox     = (SELECT id_articulo FROM articulo WHERE nombre = 'Amoxicilina 250mg x10');
SET @a_probio   = (SELECT id_articulo FROM articulo WHERE nombre = 'Probiotico 30 sobres');
SET @a_pipeta   = (SELECT id_articulo FROM articulo WHERE nombre = 'Pipeta antipulgas 10-20kg');
SET @a_shampoo  = (SELECT id_articulo FROM articulo WHERE nombre = 'Shampoo hipoalergenico 500ml');
SET @a_suero    = (SELECT id_articulo FROM articulo WHERE nombre = 'Suero fisiologico 500ml');
SET @a_cateter  = (SELECT id_articulo FROM articulo WHERE nombre = 'Cateter intravenoso 22G');
SET @a_jeringa  = (SELECT id_articulo FROM articulo WHERE nombre = 'Jeringa descartable 5ml');

SET @cat_med = (SELECT id_categoria FROM categoria WHERE nombre = 'Medicamentos');

SET @d_traq    = (SELECT id_diagnostico FROM diagnostico WHERE nombre_enfermedad = 'Traqueobronquitis infecciosa');
SET @d_gastro  = (SELECT id_diagnostico FROM diagnostico WHERE nombre_enfermedad = 'Gastroenteritis');
SET @d_deshid  = (SELECT id_diagnostico FROM diagnostico WHERE nombre_enfermedad = 'Deshidratacion');
SET @d_otitis  = (SELECT id_diagnostico FROM diagnostico WHERE nombre_enfermedad = 'Otitis externa');
SET @d_parasit = (SELECT id_diagnostico FROM diagnostico WHERE nombre_enfermedad = 'Parasitosis intestinal');
SET @d_sano    = (SELECT id_diagnostico FROM diagnostico WHERE nombre_enfermedad = 'Paciente sano');

-- -------------------------------------------------------------------------------------
-- MASCOTAS NUEVAS (8)
-- -------------------------------------------------------------------------------------
INSERT INTO mascota (id_cliente, nombre, sexo, peso, fecha_nacimiento, tipo_mascota, raza)
VALUES (@cli6,  'Bruno', 'MACHO',  11.80, '2022-06-10', 'PERRO', 'Bulldog Frances');  SET @m_bruno = LAST_INSERT_ID();
INSERT INTO mascota (id_cliente, nombre, sexo, peso, fecha_nacimiento, tipo_mascota, raza)
VALUES (@cli6,  'Kira',  'HEMBRA',  3.90, '2021-04-22', 'GATO',  'Mestizo');          SET @m_kira  = LAST_INSERT_ID();
INSERT INTO mascota (id_cliente, nombre, sexo, peso, fecha_nacimiento, tipo_mascota, raza)
VALUES (@cli7,  'Coco',  'HEMBRA',  6.20, '2020-08-15', 'PERRO', 'Shih Tzu');         SET @m_coco  = LAST_INSERT_ID();
INSERT INTO mascota (id_cliente, nombre, sexo, peso, fecha_nacimiento, tipo_mascota, raza)
VALUES (@cli8,  'Thor',  'MACHO',  34.50, '2019-12-03', 'PERRO', 'Pastor Aleman');    SET @m_thor  = LAST_INSERT_ID();
INSERT INTO mascota (id_cliente, nombre, sexo, peso, fecha_nacimiento, tipo_mascota, raza)
VALUES (@cli8,  'Simba', 'MACHO',   4.80, '2022-02-14', 'GATO',  'Mestizo');          SET @m_simba = LAST_INSERT_ID();
INSERT INTO mascota (id_cliente, nombre, sexo, peso, fecha_nacimiento, tipo_mascota, raza)
VALUES (@cli9,  'Mia',   'HEMBRA',  3.10, '2024-05-30', 'GATO',  'Bengala');          SET @m_mia   = LAST_INSERT_ID();
INSERT INTO mascota (id_cliente, nombre, sexo, peso, fecha_nacimiento, tipo_mascota, raza)
VALUES (@cli10, 'Zeus',  'MACHO',  23.70, '2021-09-08', 'PERRO', 'Husky Siberiano');  SET @m_zeus  = LAST_INSERT_ID();
INSERT INTO mascota (id_cliente, nombre, sexo, peso, fecha_nacimiento, tipo_mascota, raza)
VALUES (@cli10, 'Lola',  'HEMBRA',  5.40, '2016-03-19', 'PERRO', 'Poodle');           SET @m_lola  = LAST_INSERT_ID();

-- -------------------------------------------------------------------------------------
-- CATALOGO NUEVO: 3 medicamentos y 3 diagnosticos
-- (stock ya descuenta lo vendido en este script; las gotas oticas quedan bajo el minimo)
-- -------------------------------------------------------------------------------------
INSERT INTO articulo (nombre, descripcion, precio_base, stock_actual, stock_minimo)
VALUES ('Meloxicam 1.5mg/ml 10ml', 'Antiinflamatorio oral para dolor articular', 30.00, 9, 3); SET @a_melox = LAST_INSERT_ID();
INSERT INTO articulo (nombre, descripcion, precio_base, stock_actual, stock_minimo)
VALUES ('Colirio antibiotico 10ml', 'Gotas oftalmicas con tobramicina', 26.00, 7, 3);           SET @a_colirio = LAST_INSERT_ID();
INSERT INTO articulo (nombre, descripcion, precio_base, stock_actual, stock_minimo)
VALUES ('Gotas oticas 15ml', 'Limpiador y antiinflamatorio para el oido', 28.00, 2, 4);         SET @a_otico = LAST_INSERT_ID();
INSERT INTO articulo_categoria (id_articulo, id_categoria) VALUES (@a_melox, @cat_med), (@a_colirio, @cat_med), (@a_otico, @cat_med);

INSERT INTO diagnostico (nombre_enfermedad, descripcion) VALUES ('Esguince',                'Lesion de ligamentos por torcedura');      SET @d_esguince = LAST_INSERT_ID();
INSERT INTO diagnostico (nombre_enfermedad, descripcion) VALUES ('Conjuntivitis',           'Inflamacion de la conjuntiva del ojo');    SET @d_conjunt = LAST_INSERT_ID();
INSERT INTO diagnostico (nombre_enfermedad, descripcion) VALUES ('Enfermedad periodontal',  'Sarro e inflamacion de las encias');       SET @d_periodo = LAST_INSERT_ID();

-- =====================================================================================
-- SEMANA PASADA (lunes 16 a sabado 21)
-- =====================================================================================

-- ---- Citas de datos-prueba.sql que seguian abiertas -----------------------------------
-- lun 16/11 09:30 Pelusa, triple felina con Lucia: PENDIENTE -> COMPLETADO (pagada)
SET @c_pelusa16 = (SELECT id_cita FROM cita WHERE id_mascota = @m_pelusa AND fecha_hora = '2026-11-16 09:30:00');
SET @dc_pelusa16 = (SELECT id_detalle_cita FROM detalle_cita WHERE id_cita = @c_pelusa16);
UPDATE cita SET estado = 'COMPLETADO', id_administrador = @adm1 WHERE id_cita = @c_pelusa16;

-- mar 17/11 15:00 Firulais, control con Martin: APROBADO -> COMPLETADO (pagada)
SET @c_firu17 = (SELECT id_cita FROM cita WHERE id_mascota = @m_firulais AND fecha_hora = '2026-11-17 15:00:00');
SET @dc_firu17 = (SELECT id_detalle_cita FROM detalle_cita WHERE id_cita = @c_firu17);
UPDATE cita SET estado = 'COMPLETADO' WHERE id_cita = @c_firu17;

-- vie 20/11 10:00 Max, consulta (Lucia) + antirrabica (Carmen): APROBADO -> COMPLETADO (SIN COBRAR)
SET @c_max20 = (SELECT id_cita FROM cita WHERE id_mascota = @m_max AND fecha_hora = '2026-11-20 10:00:00');
SET @dc_max20_cons  = (SELECT id_detalle_cita FROM detalle_cita WHERE id_cita = @c_max20 AND id_servicio = @s_consulta);
SET @dc_max20_rabia = (SELECT id_detalle_cita FROM detalle_cita WHERE id_cita = @c_max20 AND id_servicio = @s_rabia);
UPDATE cita SET estado = 'COMPLETADO' WHERE id_cita = @c_max20;

-- sab 21/11 10:00 Michi, control con Carmen: PENDIENTE -> CANCELADO por el cliente
SET @c_michi21 = (SELECT id_cita FROM cita WHERE id_mascota = @m_michi AND fecha_hora = '2026-11-21 10:00:00');
UPDATE cita SET estado = 'CANCELADO' WHERE id_cita = @c_michi21;

-- ---- Citas nuevas de la semana pasada -------------------------------------------------
-- lun 16/11 15:00 Bruno, consulta con Martin: COMPLETADO (pagada)
INSERT INTO cita (id_administrador, id_mascota, fecha_hora, estado, es_emergencia)
VALUES (@adm2, @m_bruno, '2026-11-16 15:00:00', 'COMPLETADO', 0); SET @c = LAST_INSERT_ID();
INSERT INTO detalle_cita (id_cita, id_veterinario, id_servicio, observaciones) VALUES (@c, @vet2, @s_consulta, 'Cojea desde hace 2 dias'); SET @dc_bruno16 = LAST_INSERT_ID();

-- mar 17/11 10:00 Coco, consulta + desparasitacion con Lucia: COMPLETADO (pagada)
INSERT INTO cita (id_administrador, id_mascota, fecha_hora, estado, es_emergencia)
VALUES (@adm1, @m_coco, '2026-11-17 10:00:00', 'COMPLETADO', 0); SET @c = LAST_INSERT_ID();
INSERT INTO detalle_cita (id_cita, id_veterinario, id_servicio, observaciones) VALUES (@c, @vet1, @s_consulta, 'Chequeo anual'); SET @dc_coco17_cons = LAST_INSERT_ID();
INSERT INTO detalle_cita (id_cita, id_veterinario, id_servicio, observaciones) VALUES (@c, @vet1, @s_despa, NULL);            SET @dc_coco17_despa = LAST_INSERT_ID();

-- mie 18/11 09:00 Thor, esterilizacion con Carmen: COMPLETADO (SIN COBRAR)
INSERT INTO cita (id_administrador, id_mascota, fecha_hora, estado, es_emergencia)
VALUES (@adm1, @m_thor, '2026-11-18 09:00:00', 'COMPLETADO', 0); SET @c = LAST_INSERT_ID();
INSERT INTO detalle_cita (id_cita, id_veterinario, id_servicio, observaciones) VALUES (@c, @vet3, @s_ester, 'Ayuno de 12 horas cumplido'); SET @dc_thor18 = LAST_INSERT_ID();

-- jue 19/11 10:00 Lola, control con Lucia: CANCELADO por el cliente
INSERT INTO cita (id_administrador, id_mascota, fecha_hora, estado, es_emergencia)
VALUES (NULL, @m_lola, '2026-11-19 10:00:00', 'CANCELADO', 0); SET @c = LAST_INSERT_ID();
INSERT INTO detalle_cita (id_cita, id_veterinario, id_servicio, observaciones) VALUES (@c, @vet1, @s_control, 'Cancelada por el cliente');

-- jue 19/11 16:00 Kira, bano y corte con Martin: COMPLETADO (pagada)
INSERT INTO cita (id_administrador, id_mascota, fecha_hora, estado, es_emergencia)
VALUES (@adm2, @m_kira, '2026-11-19 16:00:00', 'COMPLETADO', 0); SET @c = LAST_INSERT_ID();
INSERT INTO detalle_cita (id_cita, id_veterinario, id_servicio, observaciones) VALUES (@c, @vet2, @s_bano, 'Pelo largo con nudos'); SET @dc_kira19 = LAST_INSERT_ID();

-- vie 20/11 09:00 Simba, bano y corte con Carmen: RECHAZADO
INSERT INTO cita (id_administrador, id_mascota, fecha_hora, estado, es_emergencia)
VALUES (@adm1, @m_simba, '2026-11-20 09:00:00', 'RECHAZADO', 0); SET @c = LAST_INSERT_ID();
INSERT INTO detalle_cita (id_cita, id_veterinario, id_servicio, observaciones) VALUES (@c, @vet3, @s_bano, 'Rechazada: el cliente pidio otro horario');

-- vie 20/11 15:30 Zeus, consulta con Martin: COMPLETADO (pagada)
INSERT INTO cita (id_administrador, id_mascota, fecha_hora, estado, es_emergencia)
VALUES (@adm2, @m_zeus, '2026-11-20 15:30:00', 'COMPLETADO', 0); SET @c = LAST_INSERT_ID();
INSERT INTO detalle_cita (id_cita, id_veterinario, id_servicio, observaciones) VALUES (@c, @vet2, @s_consulta, 'Ojos rojos'); SET @dc_zeus20 = LAST_INSERT_ID();

-- sab 21/11 09:30 Mia, EMERGENCIA con Carmen: COMPLETADO (SIN COBRAR)
INSERT INTO cita (id_administrador, id_mascota, fecha_hora, estado, es_emergencia)
VALUES (@adm2, @m_mia, '2026-11-21 09:30:00', 'COMPLETADO', 1); SET @c = LAST_INSERT_ID();
INSERT INTO detalle_cita (id_cita, id_veterinario, id_servicio, observaciones) VALUES (@c, @vet3, @s_emerg, 'Se trago un hilo'); SET @dc_mia21 = LAST_INSERT_ID();

-- ---- Atenciones de la semana pasada ---------------------------------------------------
INSERT INTO atencion (id_detalle_cita, fecha_hora, motivo_consulta, observaciones)
VALUES (@dc_pelusa16, '2026-11-16 09:35:00', 'Primera vacuna triple felina', 'Vacuna aplicada sin reacciones. Refuerzo programado'); SET @at_pelusa = LAST_INSERT_ID();
INSERT INTO atencion (id_detalle_cita, fecha_hora, motivo_consulta, observaciones)
VALUES (@dc_bruno16, '2026-11-16 15:05:00', 'Cojea de la pata trasera izquierda', 'Dolor a la palpacion de la rodilla, sin fractura aparente'); SET @at_bruno = LAST_INSERT_ID();
INSERT INTO atencion (id_detalle_cita, fecha_hora, motivo_consulta, observaciones)
VALUES (@dc_coco17_cons, '2026-11-17 10:05:00', 'Chequeo general anual', 'Buen estado general y peso adecuado'); SET @at_coco_cons = LAST_INSERT_ID();
INSERT INTO atencion (id_detalle_cita, fecha_hora, motivo_consulta, observaciones)
VALUES (@dc_coco17_despa, '2026-11-17 10:35:00', 'Desparasitacion interna', 'Se administro antiparasitario oral'); SET @at_coco_despa = LAST_INSERT_ID();
INSERT INTO atencion (id_detalle_cita, fecha_hora, motivo_consulta, observaciones)
VALUES (@dc_firu17, '2026-11-17 15:05:00', 'Control de la tos', 'Tos resuelta. Pulmones limpios a la auscultacion'); SET @at_firu = LAST_INSERT_ID();
INSERT INTO atencion (id_detalle_cita, fecha_hora, motivo_consulta, observaciones)
VALUES (@dc_thor18, '2026-11-18 09:10:00', 'Esterilizacion programada', 'Cirugia sin complicaciones. Recuperacion de la anestesia normal'); SET @at_thor = LAST_INSERT_ID();
INSERT INTO atencion (id_detalle_cita, fecha_hora, motivo_consulta, observaciones)
VALUES (@dc_kira19, '2026-11-19 16:05:00', 'Bano y corte', 'Se retiraron nudos; se recomienda cepillado semanal'); SET @at_kira = LAST_INSERT_ID();
INSERT INTO atencion (id_detalle_cita, fecha_hora, motivo_consulta, observaciones)
VALUES (@dc_max20_cons, '2026-11-20 10:05:00', 'Se rasca las orejas', 'Cerumen abundante y enrojecimiento en el oido derecho'); SET @at_max_cons = LAST_INSERT_ID();
INSERT INTO atencion (id_detalle_cita, fecha_hora, motivo_consulta, observaciones)
VALUES (@dc_max20_rabia, '2026-11-20 10:35:00', 'Vacunacion anual', 'Vacuna aplicada sin reacciones'); SET @at_max_rabia = LAST_INSERT_ID();
INSERT INTO atencion (id_detalle_cita, fecha_hora, motivo_consulta, observaciones)
VALUES (@dc_zeus20, '2026-11-20 15:35:00', 'Ojos llorosos y enrojecidos', 'Secrecion en ambos ojos'); SET @at_zeus = LAST_INSERT_ID();
INSERT INTO atencion (id_detalle_cita, fecha_hora, motivo_consulta, observaciones)
VALUES (@dc_mia21, '2026-11-21 09:35:00', 'Vomitos tras tragarse un hilo', 'Se indujo el vomito y salio el hilo. Fluidoterapia subcutanea'); SET @at_mia = LAST_INSERT_ID();

-- Diagnosticos (el bano y corte es estetico: no lleva diagnostico)
INSERT INTO atencion_diagnostico (id_atencion, id_diagnostico, nivel_gravedad, detalle_diagnostico) VALUES
  (@at_pelusa,     @d_sano,     'LEVE',     'Apta para vacunacion'),
  (@at_bruno,      @d_esguince, 'LEVE',     'Rodilla izquierda'),
  (@at_coco_cons,  @d_sano,     'LEVE',     NULL),
  (@at_coco_cons,  @d_periodo,  'LEVE',     'Sarro leve; se recomienda limpieza dental'),
  (@at_coco_despa, @d_parasit,  'LEVE',     'Tratamiento preventivo'),
  (@at_firu,       @d_traq,     'LEVE',     'En remision: alta del tratamiento'),
  (@at_thor,       @d_sano,     'LEVE',     'Apto para cirugia'),
  (@at_max_cons,   @d_otitis,   'MODERADO', 'Oido derecho'),
  (@at_max_rabia,  @d_sano,     'LEVE',     'Apto para vacunacion'),
  (@at_zeus,       @d_conjunt,  'MODERADO', 'Ambos ojos'),
  (@at_mia,        @d_gastro,   'MODERADO', 'Por cuerpo extrano (hilo)'),
  (@at_mia,        @d_deshid,   'LEVE',     NULL);

-- Insumos (los cobrables de Thor y Mia quedan pendientes de cobro)
INSERT INTO insumo_utilizado (id_atencion, id_articulo, cantidad_utilizada, es_cobrable) VALUES (@at_pelusa,    @a_jeringa, 1, 0);
INSERT INTO insumo_utilizado (id_atencion, id_articulo, cantidad_utilizada, es_cobrable) VALUES (@at_thor,      @a_cateter, 1, 1);
INSERT INTO insumo_utilizado (id_atencion, id_articulo, cantidad_utilizada, es_cobrable) VALUES (@at_thor,      @a_suero,   1, 1);
INSERT INTO insumo_utilizado (id_atencion, id_articulo, cantidad_utilizada, es_cobrable) VALUES (@at_thor,      @a_jeringa, 2, 0);
INSERT INTO insumo_utilizado (id_atencion, id_articulo, cantidad_utilizada, es_cobrable) VALUES (@at_kira,      @a_shampoo, 1, 0);
INSERT INTO insumo_utilizado (id_atencion, id_articulo, cantidad_utilizada, es_cobrable) VALUES (@at_max_rabia, @a_jeringa, 1, 0);
INSERT INTO insumo_utilizado (id_atencion, id_articulo, cantidad_utilizada, es_cobrable) VALUES (@at_mia,       @a_suero,   1, 1);
INSERT INTO insumo_utilizado (id_atencion, id_articulo, cantidad_utilizada, es_cobrable) VALUES (@at_mia,       @a_jeringa, 1, 0);

-- Recetas
INSERT INTO receta (id_atencion, fecha_emision, indicaciones_generales)
VALUES (@at_bruno, '2026-11-16', 'Reposo relativo por 7 dias; paseos cortos con correa'); SET @r = LAST_INSERT_ID();
INSERT INTO detalle_receta (id_receta, id_articulo, dosis, frecuencia, duracion_dias, cantidad_total) VALUES (@r, @a_melox, '0.5 ml', 'Cada 24 horas', 5, 1);

INSERT INTO receta (id_atencion, fecha_emision, indicaciones_generales)
VALUES (@at_thor, '2026-11-18', 'Collar isabelino por 10 dias. Control en 7 dias'); SET @r = LAST_INSERT_ID();
INSERT INTO detalle_receta (id_receta, id_articulo, dosis, frecuencia, duracion_dias, cantidad_total) VALUES (@r, @a_amox,  '1 tableta', 'Cada 12 horas', 7, 14);
INSERT INTO detalle_receta (id_receta, id_articulo, dosis, frecuencia, duracion_dias, cantidad_total) VALUES (@r, @a_melox, '1 ml',      'Cada 24 horas', 3, 1);

INSERT INTO receta (id_atencion, fecha_emision, indicaciones_generales)
VALUES (@at_max_cons, '2026-11-20', 'Limpiar el oido antes de aplicar las gotas. Control en 4 dias'); SET @r = LAST_INSERT_ID();
INSERT INTO detalle_receta (id_receta, id_articulo, dosis, frecuencia, duracion_dias, cantidad_total) VALUES (@r, @a_otico, '3 gotas en el oido derecho', 'Cada 12 horas', 7, 1);

INSERT INTO receta (id_atencion, fecha_emision, indicaciones_generales)
VALUES (@at_zeus, '2026-11-20', 'Limpiar las secreciones con gasa humeda. Control en una semana'); SET @r = LAST_INSERT_ID();
INSERT INTO detalle_receta (id_receta, id_articulo, dosis, frecuencia, duracion_dias, cantidad_total) VALUES (@r, @a_colirio, '2 gotas en cada ojo', 'Cada 8 horas', 7, 1);

INSERT INTO receta (id_atencion, fecha_emision, indicaciones_generales)
VALUES (@at_mia, '2026-11-21', 'Dieta blanda por 3 dias. Volver si vomita de nuevo'); SET @r = LAST_INSERT_ID();
INSERT INTO detalle_receta (id_receta, id_articulo, dosis, frecuencia, duracion_dias, cantidad_total) VALUES (@r, @a_probio, '1 sobre', 'Cada 24 horas', 5, 5);

-- ---- Boletas de la semana pasada (B001-000007 a B001-000013) --------------------------
-- Pedro: triple felina de Pelusa 70.00
INSERT INTO boleta (id_cliente, serie_correlativo, fecha, total, metodo_pago)
VALUES (@cli4, 'B001-000007', '2026-11-16 10:00:00', 70.00, 'EFECTIVO'); SET @b = LAST_INSERT_ID();
INSERT INTO detalle_boleta_serv (id_boleta, id_detalle_cita, cantidad, precio_unitario) VALUES (@b, @dc_pelusa16, 1, 70.00);

-- Carlos: consulta de Bruno 60 + meloxicam 30 = 90.00
INSERT INTO boleta (id_cliente, serie_correlativo, fecha, total, metodo_pago)
VALUES (@cli6, 'B001-000008', '2026-11-16 15:40:00', 90.00, 'TARJETA_DE_DEBITO'); SET @b = LAST_INSERT_ID();
INSERT INTO detalle_boleta_serv (id_boleta, id_detalle_cita, cantidad, precio_unitario) VALUES (@b, @dc_bruno16, 1, 60.00);
INSERT INTO detalle_boleta_art (id_boleta, id_articulo, id_insumo_utilizado, cantidad, precio_unitario) VALUES (@b, @a_melox, NULL, 1, 30.00);

-- Valeria: consulta 60 + desparasitacion 35 de Coco = 95.00
INSERT INTO boleta (id_cliente, serie_correlativo, fecha, total, metodo_pago)
VALUES (@cli7, 'B001-000009', '2026-11-17 10:50:00', 95.00, 'YAPE'); SET @b = LAST_INSERT_ID();
INSERT INTO detalle_boleta_serv (id_boleta, id_detalle_cita, cantidad, precio_unitario) VALUES (@b, @dc_coco17_cons,  1, 60.00);
INSERT INTO detalle_boleta_serv (id_boleta, id_detalle_cita, cantidad, precio_unitario) VALUES (@b, @dc_coco17_despa, 1, 35.00);

-- Ana: control de Firulais 40.00
INSERT INTO boleta (id_cliente, serie_correlativo, fecha, total, metodo_pago)
VALUES (@cli1, 'B001-000010', '2026-11-17 15:40:00', 40.00, 'YAPE'); SET @b = LAST_INSERT_ID();
INSERT INTO detalle_boleta_serv (id_boleta, id_detalle_cita, cantidad, precio_unitario) VALUES (@b, @dc_firu17, 1, 40.00);

-- Carlos: bano y corte de Kira 50 + alimento gato 89.90 = 139.90
INSERT INTO boleta (id_cliente, serie_correlativo, fecha, total, metodo_pago)
VALUES (@cli6, 'B001-000011', '2026-11-19 17:45:00', 139.90, 'EFECTIVO'); SET @b = LAST_INSERT_ID();
INSERT INTO detalle_boleta_serv (id_boleta, id_detalle_cita, cantidad, precio_unitario) VALUES (@b, @dc_kira19, 1, 50.00);
INSERT INTO detalle_boleta_art (id_boleta, id_articulo, id_insumo_utilizado, cantidad, precio_unitario) VALUES (@b, @a_ali_gato, NULL, 1, 89.90);

-- Renzo: consulta de Zeus 60 + colirio 26 = 86.00
INSERT INTO boleta (id_cliente, serie_correlativo, fecha, total, metodo_pago)
VALUES (@cli10, 'B001-000012', '2026-11-20 16:10:00', 86.00, 'TARJETA_DE_CREDITO'); SET @b = LAST_INSERT_ID();
INSERT INTO detalle_boleta_serv (id_boleta, id_detalle_cita, cantidad, precio_unitario) VALUES (@b, @dc_zeus20, 1, 60.00);
INSERT INTO detalle_boleta_art (id_boleta, id_articulo, id_insumo_utilizado, cantidad, precio_unitario) VALUES (@b, @a_colirio, NULL, 1, 26.00);

-- Luis (venta en caja): pipeta 38.50 + snack 24.50 = 63.00
INSERT INTO boleta (id_cliente, serie_correlativo, fecha, total, metodo_pago)
VALUES (@cli2, 'B001-000013', '2026-11-21 11:00:00', 63.00, 'YAPE'); SET @b = LAST_INSERT_ID();
INSERT INTO detalle_boleta_art (id_boleta, id_articulo, id_insumo_utilizado, cantidad, precio_unitario) VALUES (@b, @a_pipeta, NULL, 1, 38.50);
INSERT INTO detalle_boleta_art (id_boleta, id_articulo, id_insumo_utilizado, cantidad, precio_unitario) VALUES (@b, @a_snack,  NULL, 1, 24.50);

-- Stock: insumos usados y ventas de los articulos que ya existian
UPDATE articulo SET stock_actual = stock_actual - 5 WHERE id_articulo = @a_jeringa;
UPDATE articulo SET stock_actual = stock_actual - 1 WHERE id_articulo = @a_cateter;
UPDATE articulo SET stock_actual = stock_actual - 2 WHERE id_articulo = @a_suero;
UPDATE articulo SET stock_actual = stock_actual - 1 WHERE id_articulo = @a_shampoo;
UPDATE articulo SET stock_actual = stock_actual - 1 WHERE id_articulo = @a_ali_gato;
UPDATE articulo SET stock_actual = stock_actual - 1 WHERE id_articulo = @a_pipeta;
UPDATE articulo SET stock_actual = stock_actual - 1 WHERE id_articulo = @a_snack;

-- =====================================================================================
-- ESTA SEMANA (lunes 23 a jueves 26): citas por atender
-- =====================================================================================
-- ---- Lunes 23 ----
INSERT INTO cita (id_administrador, id_mascota, fecha_hora, estado, es_emergencia)
VALUES (@adm1, @m_luna, '2026-11-23 09:00:00', 'APROBADO', 0); SET @c = LAST_INSERT_ID();
INSERT INTO detalle_cita (id_cita, id_veterinario, id_servicio, observaciones) VALUES (@c, @vet1, @s_consulta, 'Control de peso');

INSERT INTO cita (id_administrador, id_mascota, fecha_hora, estado, es_emergencia)
VALUES (NULL, @m_rocky, '2026-11-23 10:00:00', 'PENDIENTE', 0); SET @c = LAST_INSERT_ID();
INSERT INTO detalle_cita (id_cita, id_veterinario, id_servicio, observaciones) VALUES (@c, @vet1, @s_despa, NULL);

INSERT INTO cita (id_administrador, id_mascota, fecha_hora, estado, es_emergencia)
VALUES (@adm1, @m_kira, '2026-11-23 11:00:00', 'APROBADO', 0); SET @c = LAST_INSERT_ID();
INSERT INTO detalle_cita (id_cita, id_veterinario, id_servicio, observaciones) VALUES (@c, @vet3, @s_triple, 'Refuerzo anual');

INSERT INTO cita (id_administrador, id_mascota, fecha_hora, estado, es_emergencia)
VALUES (@adm2, @m_coco, '2026-11-23 15:00:00', 'APROBADO', 0); SET @c = LAST_INSERT_ID();
INSERT INTO detalle_cita (id_cita, id_veterinario, id_servicio, observaciones) VALUES (@c, @vet2, @s_control, 'Revisar sarro');

INSERT INTO cita (id_administrador, id_mascota, fecha_hora, estado, es_emergencia)
VALUES (NULL, @m_toby, '2026-11-23 16:30:00', 'PENDIENTE', 0); SET @c = LAST_INSERT_ID();
INSERT INTO detalle_cita (id_cita, id_veterinario, id_servicio, observaciones) VALUES (@c, @vet2, @s_bano, NULL);

-- ---- Martes 24 (Carmen no atiende) ----
INSERT INTO cita (id_administrador, id_mascota, fecha_hora, estado, es_emergencia)
VALUES (@adm1, @m_bruno, '2026-11-24 09:30:00', 'APROBADO', 0); SET @c = LAST_INSERT_ID();
INSERT INTO detalle_cita (id_cita, id_veterinario, id_servicio, observaciones) VALUES (@c, @vet1, @s_control, 'Control del esguince');

INSERT INTO cita (id_administrador, id_mascota, fecha_hora, estado, es_emergencia)
VALUES (NULL, @m_simba, '2026-11-24 11:00:00', 'PENDIENTE', 0); SET @c = LAST_INSERT_ID();
INSERT INTO detalle_cita (id_cita, id_veterinario, id_servicio, observaciones) VALUES (@c, @vet1, @s_consulta, 'Estornuda mucho');

INSERT INTO cita (id_administrador, id_mascota, fecha_hora, estado, es_emergencia)
VALUES (@adm2, @m_max, '2026-11-24 14:30:00', 'APROBADO', 0); SET @c = LAST_INSERT_ID();
INSERT INTO detalle_cita (id_cita, id_veterinario, id_servicio, observaciones) VALUES (@c, @vet2, @s_control, 'Control de la otitis');

INSERT INTO cita (id_administrador, id_mascota, fecha_hora, estado, es_emergencia)
VALUES (@adm2, @m_lola, '2026-11-24 16:00:00', 'APROBADO', 0); SET @c = LAST_INSERT_ID();
INSERT INTO detalle_cita (id_cita, id_veterinario, id_servicio, observaciones) VALUES (@c, @vet2, @s_consulta, 'Perrita mayor: chequeo general');
INSERT INTO detalle_cita (id_cita, id_veterinario, id_servicio, observaciones) VALUES (@c, @vet2, @s_rabia, NULL);

-- ---- Miercoles 25 (dia de la exposicion, 18:00) ----
INSERT INTO cita (id_administrador, id_mascota, fecha_hora, estado, es_emergencia)
VALUES (@adm1, @m_zeus, '2026-11-25 09:00:00', 'APROBADO', 0); SET @c = LAST_INSERT_ID();
INSERT INTO detalle_cita (id_cita, id_veterinario, id_servicio, observaciones) VALUES (@c, @vet1, @s_control, 'Control de la conjuntivitis');

INSERT INTO cita (id_administrador, id_mascota, fecha_hora, estado, es_emergencia)
VALUES (@adm1, @m_mia, '2026-11-25 10:00:00', 'APROBADO', 0); SET @c = LAST_INSERT_ID();
INSERT INTO detalle_cita (id_cita, id_veterinario, id_servicio, observaciones) VALUES (@c, @vet3, @s_control, 'Control despues de la emergencia');

INSERT INTO cita (id_administrador, id_mascota, fecha_hora, estado, es_emergencia)
VALUES (@adm1, @m_rocky, '2026-11-25 11:30:00', 'APROBADO', 0); SET @c = LAST_INSERT_ID();
INSERT INTO detalle_cita (id_cita, id_veterinario, id_servicio, observaciones) VALUES (@c, @vet1, @s_consulta, 'Picazon en el lomo');

INSERT INTO cita (id_administrador, id_mascota, fecha_hora, estado, es_emergencia)
VALUES (@adm2, @m_thor, '2026-11-25 15:00:00', 'APROBADO', 0); SET @c = LAST_INSERT_ID();
INSERT INTO detalle_cita (id_cita, id_veterinario, id_servicio, observaciones) VALUES (@c, @vet2, @s_control, 'Control post operatorio');

-- Para la demo: Martin atiende a Firulais en vivo (cita APROBADO de Ana, 16:30-17:15)
INSERT INTO cita (id_administrador, id_mascota, fecha_hora, estado, es_emergencia)
VALUES (@adm2, @m_firulais, '2026-11-25 16:30:00', 'APROBADO', 0); SET @c = LAST_INSERT_ID();
INSERT INTO detalle_cita (id_cita, id_veterinario, id_servicio, observaciones) VALUES (@c, @vet2, @s_consulta, 'No quiere comer desde ayer');
INSERT INTO detalle_cita (id_cita, id_veterinario, id_servicio, observaciones) VALUES (@c, @vet2, @s_despa, NULL);

-- Para la demo: el administrador aprueba en vivo (cita PENDIENTE de Ana, 19:30)
INSERT INTO cita (id_administrador, id_mascota, fecha_hora, estado, es_emergencia)
VALUES (NULL, @m_nala, '2026-11-25 19:30:00', 'PENDIENTE', 0); SET @c = LAST_INSERT_ID();
INSERT INTO detalle_cita (id_cita, id_veterinario, id_servicio, observaciones) VALUES (@c, @vet2, @s_triple, 'Refuerzo anual');

-- ---- Jueves 26 (Carmen no atiende) ----
INSERT INTO cita (id_administrador, id_mascota, fecha_hora, estado, es_emergencia)
VALUES (@adm1, @m_pelusa, '2026-11-26 09:00:00', 'APROBADO', 0); SET @c = LAST_INSERT_ID();
INSERT INTO detalle_cita (id_cita, id_veterinario, id_servicio, observaciones) VALUES (@c, @vet1, @s_triple, 'Refuerzo de la vacuna');

INSERT INTO cita (id_administrador, id_mascota, fecha_hora, estado, es_emergencia)
VALUES (NULL, @m_coco, '2026-11-26 10:30:00', 'PENDIENTE', 0); SET @c = LAST_INSERT_ID();
INSERT INTO detalle_cita (id_cita, id_veterinario, id_servicio, observaciones) VALUES (@c, @vet1, @s_bano, NULL);

INSERT INTO cita (id_administrador, id_mascota, fecha_hora, estado, es_emergencia)
VALUES (NULL, @m_michi, '2026-11-26 15:00:00', 'PENDIENTE', 0); SET @c = LAST_INSERT_ID();
INSERT INTO detalle_cita (id_cita, id_veterinario, id_servicio, observaciones) VALUES (@c, @vet2, @s_control, 'Control post gastroenteritis (reprogramado)');

INSERT INTO cita (id_administrador, id_mascota, fecha_hora, estado, es_emergencia)
VALUES (@adm2, @m_luna, '2026-11-26 16:00:00', 'APROBADO', 0); SET @c = LAST_INSERT_ID();
INSERT INTO detalle_cita (id_cita, id_veterinario, id_servicio, observaciones) VALUES (@c, @vet2, @s_ester, 'Ayuno de 12 horas');

COMMIT;

-- -------------------------------------------------------------------------------------
-- COMPROBACION: citas por dia y estado desde el lunes 16
-- -------------------------------------------------------------------------------------
SELECT DATE(fecha_hora) AS dia, estado, COUNT(*) AS citas
FROM cita
WHERE fecha_hora >= '2026-11-16' AND activo = 1
GROUP BY DATE(fecha_hora), estado
ORDER BY dia, estado;
