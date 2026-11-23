-- =====================================================================================
-- CARGA DE DATOS de Patitas Arriba (esquema patitasarriba)
--
-- Requiere las tablas y los procedures (crear-tablas-y-procedures.sql).
-- Se corre UNA sola vez sobre tablas vacias: recien creadas o despues de limpiar-datos.sql.
-- Si se corre sobre datos ya cargados, falla en el primer correo repetido.
--
-- Partes:
--   1. Primer administrador del sistema: admin@patitasarriba.pe / Admin123.
--   2. Datos de prueba: horarios, catalogos, inventario, personal, clientes, mascotas,
--      citas, atenciones, recetas y boletas. Todas las cuentas usan la contrasena Patitas123.
--   3. Datos de la semana de la exposicion (16 al 26 de noviembre de 2026).
-- Las 16 cuentas que resultan son las del archivo de usuarios de la entrega.
-- =====================================================================================

-- ==================== 1. Primer administrador ====================
-- Datos iniciales: primer administrador del sistema (RF04)
-- La clinica lo designa al implementar el sistema; los demas usuarios los crea este administrador.
-- IMPORTANTE: reemplazar los datos de ejemplo y cambiar la contrasena tras el primer ingreso.

use patitasarriba;

-- La contrasena se guarda como HashUtil.generarHashSHA256: SHA-256 en Base64 (RN-CU-04)
INSERT INTO cuenta (correo, password, fecha_creacion, nombre_usuario, activo)
VALUES ('admin@patitasarriba.pe', TO_BASE64(UNHEX(SHA2('Admin123', 256))), CURDATE(), 'admin', 1);

INSERT INTO administrador (id_cuenta, dni, nombres, apellido_paterno, apellido_materno, telefono, activo)
VALUES (LAST_INSERT_ID(), '00000001', 'Administrador', 'Principal', 'Sistema', '900000001', 1);

-- ==================== 2. Datos de prueba ====================
-- =====================================================================================
-- Datos de prueba de Patitas-Arriba (esquema patitasarriba)
--
-- * Los ids no son fijos: cada INSERT guarda su id en una variable (@...) con
--   LAST_INSERT_ID(), asi que el script funciona aunque las tablas ya tengan filas.
--   Al final se muestran los ids generados.
-- * dni, telefono, correo, nombre_usuario, colegiatura, nombres de catalogo y serie de
--   boleta son UNIQUE: por eso este archivo se corre una sola vez sobre tablas vacias.
-- * Todas las cuentas usan el password  Patitas123  , guardado igual que
--   HashUtil.generarHashSHA256 (SHA-256 en Base64).
-- * Fecha de referencia: 2026-11-09 (lunes).
--     - Citas COMPLETADO: pasadas, con atencion, diagnostico, receta, insumos y boleta.
--     - Citas CANCELADO / RECHAZADO: pasadas, sin atencion.
--     - Citas PENDIENTE / APROBADO: futuras y dentro del horario del veterinario
--       (CitaBLImpl rechaza fechas pasadas y horas fuera de horario).
-- * Boletas: precio_unitario = precio_base actual y total = suma de subtotales, que es
--   lo mismo que recalcula BoletaBLImpl. Serie con formato B001-000001.
-- * stock_actual ya refleja lo vendido y lo usado como insumo. Hay articulos por debajo
--   de su stock_minimo y un articulo inactivo, para probar alertas y filtros.
-- =====================================================================================
USE patitasarriba;

START TRANSACTION;

SET @pwd = TO_BASE64(UNHEX(SHA2('Patitas123', 256)));

-- -------------------------------------------------------------------------------------
-- HORARIOS (turno manana y turno tarde de lunes a viernes; sabado medio dia)
-- -------------------------------------------------------------------------------------
INSERT INTO horario (dia_semana, hora_inicio, hora_fin) VALUES ('LUNES',     '08:00', '14:00'); SET @h_lun_m = LAST_INSERT_ID();
INSERT INTO horario (dia_semana, hora_inicio, hora_fin) VALUES ('LUNES',     '14:00', '20:00'); SET @h_lun_t = LAST_INSERT_ID();
INSERT INTO horario (dia_semana, hora_inicio, hora_fin) VALUES ('MARTES',    '08:00', '14:00'); SET @h_mar_m = LAST_INSERT_ID();
INSERT INTO horario (dia_semana, hora_inicio, hora_fin) VALUES ('MARTES',    '14:00', '20:00'); SET @h_mar_t = LAST_INSERT_ID();
INSERT INTO horario (dia_semana, hora_inicio, hora_fin) VALUES ('MIERCOLES', '08:00', '14:00'); SET @h_mie_m = LAST_INSERT_ID();
INSERT INTO horario (dia_semana, hora_inicio, hora_fin) VALUES ('MIERCOLES', '14:00', '20:00'); SET @h_mie_t = LAST_INSERT_ID();
INSERT INTO horario (dia_semana, hora_inicio, hora_fin) VALUES ('JUEVES',    '08:00', '14:00'); SET @h_jue_m = LAST_INSERT_ID();
INSERT INTO horario (dia_semana, hora_inicio, hora_fin) VALUES ('JUEVES',    '14:00', '20:00'); SET @h_jue_t = LAST_INSERT_ID();
INSERT INTO horario (dia_semana, hora_inicio, hora_fin) VALUES ('VIERNES',   '08:00', '14:00'); SET @h_vie_m = LAST_INSERT_ID();
INSERT INTO horario (dia_semana, hora_inicio, hora_fin) VALUES ('VIERNES',   '14:00', '20:00'); SET @h_vie_t = LAST_INSERT_ID();
INSERT INTO horario (dia_semana, hora_inicio, hora_fin) VALUES ('SABADO',    '09:00', '13:00'); SET @h_sab   = LAST_INSERT_ID();

-- -------------------------------------------------------------------------------------
-- CUENTAS (2 administradores, 3 veterinarios, 5 clientes)
-- -------------------------------------------------------------------------------------
INSERT INTO cuenta (correo, password, fecha_creacion, nombre_usuario) VALUES ('rosa.admin@patitas.pe',      @pwd, '2026-01-10', 'rosa.admin');    SET @cu_adm1 = LAST_INSERT_ID();
INSERT INTO cuenta (correo, password, fecha_creacion, nombre_usuario) VALUES ('jorge.admin@patitas.pe',     @pwd, '2026-02-03', 'jorge.admin');   SET @cu_adm2 = LAST_INSERT_ID();
INSERT INTO cuenta (correo, password, fecha_creacion, nombre_usuario) VALUES ('dra.lucia@patitas.pe',       @pwd, '2026-01-15', 'dra.lucia');     SET @cu_vet1 = LAST_INSERT_ID();
INSERT INTO cuenta (correo, password, fecha_creacion, nombre_usuario) VALUES ('dr.martin@patitas.pe',       @pwd, '2026-01-20', 'dr.martin');     SET @cu_vet2 = LAST_INSERT_ID();
INSERT INTO cuenta (correo, password, fecha_creacion, nombre_usuario) VALUES ('dra.carmen@patitas.pe',      @pwd, '2026-03-01', 'dra.carmen');    SET @cu_vet3 = LAST_INSERT_ID();
INSERT INTO cuenta (correo, password, fecha_creacion, nombre_usuario) VALUES ('ana.torres@gmail.com',       @pwd, '2026-04-11', 'ana.torres');    SET @cu_cli1 = LAST_INSERT_ID();
INSERT INTO cuenta (correo, password, fecha_creacion, nombre_usuario) VALUES ('luis.quispe@gmail.com',      @pwd, '2026-05-02', 'luis.quispe');   SET @cu_cli2 = LAST_INSERT_ID();
INSERT INTO cuenta (correo, password, fecha_creacion, nombre_usuario) VALUES ('maria.flores@hotmail.com',   @pwd, '2026-06-18', 'maria.flores');  SET @cu_cli3 = LAST_INSERT_ID();
INSERT INTO cuenta (correo, password, fecha_creacion, nombre_usuario) VALUES ('pedro.ramos@outlook.com',    @pwd, '2026-07-07', 'pedro.ramos');   SET @cu_cli4 = LAST_INSERT_ID();
INSERT INTO cuenta (correo, password, fecha_creacion, nombre_usuario) VALUES ('sofia.mendoza@gmail.com',    @pwd, '2026-09-21', 'sofia.mendoza'); SET @cu_cli5 = LAST_INSERT_ID();

-- -------------------------------------------------------------------------------------
-- ADMINISTRADORES, VETERINARIOS Y CLIENTES (dni de 8 digitos, telefono de 9)
-- -------------------------------------------------------------------------------------
INSERT INTO administrador (id_cuenta, dni, nombres, apellido_paterno, apellido_materno, telefono)
VALUES (@cu_adm1, '41235678', 'Rosa Elena', 'Gutierrez', 'Salas', '987100201'); SET @adm1 = LAST_INSERT_ID();
INSERT INTO administrador (id_cuenta, dni, nombres, apellido_paterno, apellido_materno, telefono)
VALUES (@cu_adm2, '43567812', 'Jorge Luis', 'Paredes', 'Huaman', '987100202'); SET @adm2 = LAST_INSERT_ID();

INSERT INTO veterinario (id_cuenta, dni, nombres, apellido_paterno, apellido_materno, telefono, numero_colegiatura)
VALUES (@cu_vet1, '40123456', 'Lucia', 'Fernandez', 'Rojas', '986200301', 'CMVP-08451'); SET @vet1 = LAST_INSERT_ID();
INSERT INTO veterinario (id_cuenta, dni, nombres, apellido_paterno, apellido_materno, telefono, numero_colegiatura)
VALUES (@cu_vet2, '42789012', 'Martin', 'Castillo', 'Vega', '986200302', 'CMVP-09127'); SET @vet2 = LAST_INSERT_ID();
INSERT INTO veterinario (id_cuenta, dni, nombres, apellido_paterno, apellido_materno, telefono, numero_colegiatura)
VALUES (@cu_vet3, '45901234', 'Carmen Rosa', 'Delgado', 'Pinto', '986200303', 'CMVP-10388'); SET @vet3 = LAST_INSERT_ID();

INSERT INTO cliente (id_cuenta, dni, nombres, apellido_paterno, apellido_materno, telefono)
VALUES (@cu_cli1, '70123456', 'Ana Maria', 'Torres', 'Lopez', '955300401'); SET @cli1 = LAST_INSERT_ID();
INSERT INTO cliente (id_cuenta, dni, nombres, apellido_paterno, apellido_materno, telefono)
VALUES (@cu_cli2, '71234567', 'Luis Alberto', 'Quispe', 'Mamani', '955300402'); SET @cli2 = LAST_INSERT_ID();
INSERT INTO cliente (id_cuenta, dni, nombres, apellido_paterno, apellido_materno, telefono)
VALUES (@cu_cli3, '72345678', 'Maria Jose', 'Flores', 'Chavez', '955300403'); SET @cli3 = LAST_INSERT_ID();
INSERT INTO cliente (id_cuenta, dni, nombres, apellido_paterno, apellido_materno, telefono)
VALUES (@cu_cli4, '73456789', 'Pedro Pablo', 'Ramos', 'Diaz', '955300404'); SET @cli4 = LAST_INSERT_ID();
INSERT INTO cliente (id_cuenta, dni, nombres, apellido_paterno, apellido_materno, telefono)
VALUES (@cu_cli5, '74567890', 'Sofia', 'Mendoza', 'Arce', '955300405'); SET @cli5 = LAST_INSERT_ID();

-- -------------------------------------------------------------------------------------
-- HORARIOS ASIGNADOS
--   vet1 Lucia : mananas de lunes a viernes
--   vet2 Martin: tardes de lunes a viernes
--   vet3 Carmen: lunes, miercoles y viernes en la manana + sabado
--   adm1 Rosa  : mananas de lunes a viernes
--   adm2 Jorge : tardes de lunes a viernes + sabado
-- -------------------------------------------------------------------------------------
INSERT INTO horario_veterinario (id_veterinario, id_horario) VALUES
  (@vet1, @h_lun_m), (@vet1, @h_mar_m), (@vet1, @h_mie_m), (@vet1, @h_jue_m), (@vet1, @h_vie_m),
  (@vet2, @h_lun_t), (@vet2, @h_mar_t), (@vet2, @h_mie_t), (@vet2, @h_jue_t), (@vet2, @h_vie_t),
  (@vet3, @h_lun_m), (@vet3, @h_mie_m), (@vet3, @h_vie_m), (@vet3, @h_sab);

INSERT INTO horario_administrador (id_administrador, id_horario) VALUES
  (@adm1, @h_lun_m), (@adm1, @h_mar_m), (@adm1, @h_mie_m), (@adm1, @h_jue_m), (@adm1, @h_vie_m),
  (@adm2, @h_lun_t), (@adm2, @h_mar_t), (@adm2, @h_mie_t), (@adm2, @h_jue_t), (@adm2, @h_vie_t), (@adm2, @h_sab);

-- -------------------------------------------------------------------------------------
-- MASCOTAS
-- -------------------------------------------------------------------------------------
INSERT INTO mascota (id_cliente, nombre, sexo, peso, fecha_nacimiento, tipo_mascota, raza)
VALUES (@cli1, 'Firulais', 'MACHO',  12.40, '2021-03-14', 'PERRO', 'Mestizo');          SET @m1 = LAST_INSERT_ID();
INSERT INTO mascota (id_cliente, nombre, sexo, peso, fecha_nacimiento, tipo_mascota, raza)
VALUES (@cli1, 'Nala',     'HEMBRA',  4.10, '2023-07-02', 'GATO',  'Siames');           SET @m2 = LAST_INSERT_ID();
INSERT INTO mascota (id_cliente, nombre, sexo, peso, fecha_nacimiento, tipo_mascota, raza)
VALUES (@cli2, 'Michi',    'MACHO',   5.30, '2020-11-20', 'GATO',  'Persa');            SET @m3 = LAST_INSERT_ID();
INSERT INTO mascota (id_cliente, nombre, sexo, peso, fecha_nacimiento, tipo_mascota, raza)
VALUES (@cli2, 'Rocky',    'MACHO',  31.00, '2019-05-08', 'PERRO', 'Labrador Retriever'); SET @m4 = LAST_INSERT_ID();
INSERT INTO mascota (id_cliente, nombre, sexo, peso, fecha_nacimiento, tipo_mascota, raza)
VALUES (@cli3, 'Luna',     'HEMBRA',  7.80, '2022-01-30', 'PERRO', 'Beagle');           SET @m5 = LAST_INSERT_ID();
INSERT INTO mascota (id_cliente, nombre, sexo, peso, fecha_nacimiento, tipo_mascota, raza)
VALUES (@cli4, 'Toby',     'MACHO',   3.20, '2024-02-11', 'PERRO', 'Chihuahua');        SET @m6 = LAST_INSERT_ID();
INSERT INTO mascota (id_cliente, nombre, sexo, peso, fecha_nacimiento, tipo_mascota, raza)
VALUES (@cli4, 'Pelusa',   'HEMBRA',  NULL, '2025-12-01', 'GATO',  NULL);               SET @m7 = LAST_INSERT_ID();
INSERT INTO mascota (id_cliente, nombre, sexo, peso, fecha_nacimiento, tipo_mascota, raza)
VALUES (@cli5, 'Max',      'MACHO',  24.50, '2018-09-17', 'PERRO', 'Golden Retriever'); SET @m8 = LAST_INSERT_ID();

-- -------------------------------------------------------------------------------------
-- CATALOGO: CATEGORIAS, ARTICULOS, SERVICIOS Y DIAGNOSTICOS
-- -------------------------------------------------------------------------------------
INSERT INTO categoria (nombre, descripcion) VALUES ('Alimentos',        'Alimento seco, humedo y snacks para perros y gatos');  SET @cat_ali = LAST_INSERT_ID();
INSERT INTO categoria (nombre, descripcion) VALUES ('Medicamentos',     'Farmacos de venta con receta veterinaria');            SET @cat_med = LAST_INSERT_ID();
INSERT INTO categoria (nombre, descripcion) VALUES ('Antiparasitarios', 'Pipetas, tabletas y collares contra pulgas y parasitos'); SET @cat_ant = LAST_INSERT_ID();
INSERT INTO categoria (nombre, descripcion) VALUES ('Accesorios',       'Collares, correas, camas y juguetes');                 SET @cat_acc = LAST_INSERT_ID();
INSERT INTO categoria (nombre, descripcion) VALUES ('Higiene',          'Shampoo, arena sanitaria y productos de aseo');        SET @cat_hig = LAST_INSERT_ID();
INSERT INTO categoria (nombre, descripcion) VALUES ('Insumos medicos',  'Material de uso clinico durante las atenciones');      SET @cat_ins = LAST_INSERT_ID();

-- stock_actual ya descuenta ventas e insumos de las atenciones de este script
INSERT INTO articulo (nombre, descripcion, precio_base, stock_actual, stock_minimo)
VALUES ('Alimento perro adulto 15kg', 'Croquetas para perro adulto de raza mediana', 189.90, 14, 5);   SET @a_ali_perro = LAST_INSERT_ID();
INSERT INTO articulo (nombre, descripcion, precio_base, stock_actual, stock_minimo)
VALUES ('Alimento gato 3kg', 'Croquetas para gato adulto sabor salmon', 89.90, 21, 8);                 SET @a_ali_gato = LAST_INSERT_ID();
INSERT INTO articulo (nombre, descripcion, precio_base, stock_actual, stock_minimo)
VALUES ('Snack dental perro x10', 'Barras dentales para limpieza de sarro', 24.50, 40, 10);            SET @a_snack = LAST_INSERT_ID();
INSERT INTO articulo (nombre, descripcion, precio_base, stock_actual, stock_minimo)
VALUES ('Amoxicilina 250mg x10', 'Antibiotico en tabletas', 32.00, 17, 6);                             SET @a_amox = LAST_INSERT_ID();
INSERT INTO articulo (nombre, descripcion, precio_base, stock_actual, stock_minimo)
VALUES ('Metronidazol 250mg x10', 'Antibiotico y antiparasitario intestinal', 25.00, 4, 6);            SET @a_metro = LAST_INSERT_ID();
INSERT INTO articulo (nombre, descripcion, precio_base, stock_actual, stock_minimo)
VALUES ('Probiotico 30 sobres', 'Suplemento para flora intestinal', 45.00, 9, 4);                      SET @a_probio = LAST_INSERT_ID();
INSERT INTO articulo (nombre, descripcion, precio_base, stock_actual, stock_minimo)
VALUES ('Pipeta antipulgas 10-20kg', 'Pipeta mensual contra pulgas y garrapatas', 38.50, 29, 10);      SET @a_pipeta = LAST_INSERT_ID();
INSERT INTO articulo (nombre, descripcion, precio_base, stock_actual, stock_minimo)
VALUES ('Collar nylon mediano', 'Collar regulable con hebilla', 35.00, 11, 3);                         SET @a_collar = LAST_INSERT_ID();
INSERT INTO articulo (nombre, descripcion, precio_base, stock_actual, stock_minimo)
VALUES ('Pelota de goma', 'Juguete resistente para perro', 15.00, 23, 5);                              SET @a_pelota = LAST_INSERT_ID();
INSERT INTO articulo (nombre, descripcion, precio_base, stock_actual, stock_minimo)
VALUES ('Shampoo hipoalergenico 500ml', 'Shampoo para piel sensible', 42.00, 7, 3);                    SET @a_shampoo = LAST_INSERT_ID();
INSERT INTO articulo (nombre, descripcion, precio_base, stock_actual, stock_minimo)
VALUES ('Suero fisiologico 500ml', 'Cloruro de sodio 0.9% para fluidoterapia', 18.00, 4, 10);          SET @a_suero = LAST_INSERT_ID();
INSERT INTO articulo (nombre, descripcion, precio_base, stock_actual, stock_minimo)
VALUES ('Cateter intravenoso 22G', 'Cateter para via periferica', 12.00, 25, 10);                     SET @a_cateter = LAST_INSERT_ID();
INSERT INTO articulo (nombre, descripcion, precio_base, stock_actual, stock_minimo)
VALUES ('Jeringa descartable 5ml', 'Jeringa esteril con aguja', 1.50, 186, 50);                       SET @a_jeringa = LAST_INSERT_ID();
INSERT INTO articulo (nombre, descripcion, precio_base, stock_actual, stock_minimo, activo)
VALUES ('Correa retractil 5m', 'Producto descontinuado', 55.00, 0, 2, 0);                             SET @a_correa = LAST_INSERT_ID();

INSERT INTO articulo_categoria (id_articulo, id_categoria) VALUES
  (@a_ali_perro, @cat_ali), (@a_ali_gato, @cat_ali), (@a_snack, @cat_ali),
  (@a_amox, @cat_med), (@a_metro, @cat_med), (@a_metro, @cat_ant), (@a_probio, @cat_med),
  (@a_pipeta, @cat_ant),
  (@a_collar, @cat_acc), (@a_pelota, @cat_acc), (@a_correa, @cat_acc),
  (@a_shampoo, @cat_hig),
  (@a_suero, @cat_ins), (@a_cateter, @cat_ins), (@a_jeringa, @cat_ins);

INSERT INTO servicio (nombre, descripcion, precio_base, duracion_estimada, tipo_servicio)
VALUES ('Consulta general', 'Evaluacion clinica completa', 60.00, 30, 'CONSULTA_MEDICA');               SET @s_consulta = LAST_INSERT_ID();
INSERT INTO servicio (nombre, descripcion, precio_base, duracion_estimada, tipo_servicio)
VALUES ('Control de seguimiento', 'Revision posterior a un tratamiento', 40.00, 20, 'CONSULTA_MEDICA'); SET @s_control = LAST_INSERT_ID();
INSERT INTO servicio (nombre, descripcion, precio_base, duracion_estimada, tipo_servicio)
VALUES ('Vacuna antirrabica', 'Vacuna anual contra la rabia', 45.00, 15, 'VACUNACION');                 SET @s_rabia = LAST_INSERT_ID();
INSERT INTO servicio (nombre, descripcion, precio_base, duracion_estimada, tipo_servicio)
VALUES ('Vacuna triple felina', 'Vacuna contra panleucopenia, calicivirus y rinotraqueitis', 70.00, 15, 'VACUNACION'); SET @s_triple = LAST_INSERT_ID();
INSERT INTO servicio (nombre, descripcion, precio_base, duracion_estimada, tipo_servicio)
VALUES ('Desparasitacion', 'Desparasitacion interna con evaluacion', 35.00, 15, 'CONSULTA_MEDICA');    SET @s_despa = LAST_INSERT_ID();
INSERT INTO servicio (nombre, descripcion, precio_base, duracion_estimada, tipo_servicio)
VALUES ('Bano y corte', 'Bano medicado, corte de pelo y unas', 50.00, 90, 'ESTETICA');                 SET @s_bano = LAST_INSERT_ID();
INSERT INTO servicio (nombre, descripcion, precio_base, duracion_estimada, tipo_servicio)
VALUES ('Esterilizacion', 'Cirugia de esterilizacion con anestesia general', 350.00, 120, 'OPERACION'); SET @s_ester = LAST_INSERT_ID();
INSERT INTO servicio (nombre, descripcion, precio_base, duracion_estimada, tipo_servicio)
VALUES ('Atencion de emergencia', 'Atencion inmediata sin cita previa', 150.00, NULL, 'EMERGENCIA');   SET @s_emerg = LAST_INSERT_ID();

INSERT INTO diagnostico (nombre_enfermedad, descripcion) VALUES ('Traqueobronquitis infecciosa', 'Tos de las perreras: infeccion respiratoria contagiosa');    SET @d_traq = LAST_INSERT_ID();
INSERT INTO diagnostico (nombre_enfermedad, descripcion) VALUES ('Gastroenteritis',              'Inflamacion del estomago e intestino con vomitos o diarrea'); SET @d_gastro = LAST_INSERT_ID();
INSERT INTO diagnostico (nombre_enfermedad, descripcion) VALUES ('Deshidratacion',               'Perdida de liquidos y electrolitos');                         SET @d_deshid = LAST_INSERT_ID();
INSERT INTO diagnostico (nombre_enfermedad, descripcion) VALUES ('Intoxicacion alimentaria',     'Ingesta de alimento o sustancia toxica');                    SET @d_intox = LAST_INSERT_ID();
INSERT INTO diagnostico (nombre_enfermedad, descripcion) VALUES ('Dermatitis alergica',          'Inflamacion de la piel por alergia');                        SET @d_derma = LAST_INSERT_ID();
INSERT INTO diagnostico (nombre_enfermedad, descripcion) VALUES ('Otitis externa',               'Infeccion o inflamacion del conducto auditivo externo');     SET @d_otitis = LAST_INSERT_ID();
INSERT INTO diagnostico (nombre_enfermedad, descripcion) VALUES ('Parasitosis intestinal',       'Presencia de parasitos en el tracto digestivo');             SET @d_parasit = LAST_INSERT_ID();
INSERT INTO diagnostico (nombre_enfermedad, descripcion) VALUES ('Paciente sano',                'Sin hallazgos patologicos en la evaluacion');                SET @d_sano = LAST_INSERT_ID();

-- -------------------------------------------------------------------------------------
-- CITAS Y SUS SERVICIOS (detalle_cita)
-- -------------------------------------------------------------------------------------
-- c1 lun 05/10 10:00 COMPLETADO: Firulais, consulta + vacuna con Lucia
INSERT INTO cita (id_administrador, id_mascota, fecha_hora, estado, es_emergencia)
VALUES (@adm1, @m1, '2026-10-05 10:00:00', 'COMPLETADO', 0); SET @c1 = LAST_INSERT_ID();
INSERT INTO detalle_cita (id_cita, id_veterinario, id_servicio, observaciones) VALUES (@c1, @vet1, @s_consulta, 'Tos seca hace 3 dias'); SET @dc1 = LAST_INSERT_ID();
INSERT INTO detalle_cita (id_cita, id_veterinario, id_servicio, observaciones) VALUES (@c1, @vet1, @s_rabia, NULL);                    SET @dc2 = LAST_INSERT_ID();

-- c2 mie 14/10 15:30 COMPLETADO: Michi, consulta con Martin
INSERT INTO cita (id_administrador, id_mascota, fecha_hora, estado, es_emergencia)
VALUES (@adm2, @m3, '2026-10-14 15:30:00', 'COMPLETADO', 0); SET @c2 = LAST_INSERT_ID();
INSERT INTO detalle_cita (id_cita, id_veterinario, id_servicio, observaciones) VALUES (@c2, @vet2, @s_consulta, 'Vomitos desde ayer'); SET @dc3 = LAST_INSERT_ID();

-- c3 vie 23/10 09:00 COMPLETADO, EMERGENCIA: Luna con Carmen
INSERT INTO cita (id_administrador, id_mascota, fecha_hora, estado, es_emergencia)
VALUES (@adm1, @m5, '2026-10-23 09:00:00', 'COMPLETADO', 1); SET @c3 = LAST_INSERT_ID();
INSERT INTO detalle_cita (id_cita, id_veterinario, id_servicio, observaciones) VALUES (@c3, @vet3, @s_emerg, 'Comio restos de comida en la calle'); SET @dc4 = LAST_INSERT_ID();

-- c4 mar 03/11 16:00 COMPLETADO: Nala, bano y corte con Martin
INSERT INTO cita (id_administrador, id_mascota, fecha_hora, estado, es_emergencia)
VALUES (@adm2, @m2, '2026-11-03 16:00:00', 'COMPLETADO', 0); SET @c4 = LAST_INSERT_ID();
INSERT INTO detalle_cita (id_cita, id_veterinario, id_servicio, observaciones) VALUES (@c4, @vet2, @s_bano, 'Piel sensible: usar shampoo hipoalergenico'); SET @dc5 = LAST_INSERT_ID();

-- c5 mar 20/10 10:00 CANCELADO por el cliente: Rocky
INSERT INTO cita (id_administrador, id_mascota, fecha_hora, estado, es_emergencia)
VALUES (@adm1, @m4, '2026-10-20 10:00:00', 'CANCELADO', 0); SET @c5 = LAST_INSERT_ID();
INSERT INTO detalle_cita (id_cita, id_veterinario, id_servicio, observaciones) VALUES (@c5, @vet1, @s_despa, NULL); SET @dc6 = LAST_INSERT_ID();

-- c6 mie 28/10 10:00 RECHAZADO por el administrador: Toby
INSERT INTO cita (id_administrador, id_mascota, fecha_hora, estado, es_emergencia)
VALUES (@adm1, @m6, '2026-10-28 10:00:00', 'RECHAZADO', 0); SET @c6 = LAST_INSERT_ID();
INSERT INTO detalle_cita (id_cita, id_veterinario, id_servicio, observaciones) VALUES (@c6, @vet3, @s_ester, 'Rechazada: falta ayuno previo'); SET @dc7 = LAST_INSERT_ID();

-- c7 lun 16/11 09:30 PENDIENTE (sin administrador asignado todavia): Pelusa con Lucia
INSERT INTO cita (id_administrador, id_mascota, fecha_hora, estado, es_emergencia)
VALUES (NULL, @m7, '2026-11-16 09:30:00', 'PENDIENTE', 0); SET @c7 = LAST_INSERT_ID();
INSERT INTO detalle_cita (id_cita, id_veterinario, id_servicio, observaciones) VALUES (@c7, @vet1, @s_triple, 'Primera vacuna'); SET @dc8 = LAST_INSERT_ID();

-- c8 mar 17/11 15:00 APROBADO: Firulais, control con Martin
INSERT INTO cita (id_administrador, id_mascota, fecha_hora, estado, es_emergencia)
VALUES (@adm2, @m1, '2026-11-17 15:00:00', 'APROBADO', 0); SET @c8 = LAST_INSERT_ID();
INSERT INTO detalle_cita (id_cita, id_veterinario, id_servicio, observaciones) VALUES (@c8, @vet2, @s_control, 'Control de la tos'); SET @dc9 = LAST_INSERT_ID();

-- c9 vie 20/11 10:00 APROBADO, dos servicios con dos veterinarios: Max
INSERT INTO cita (id_administrador, id_mascota, fecha_hora, estado, es_emergencia)
VALUES (@adm1, @m8, '2026-11-20 10:00:00', 'APROBADO', 0); SET @c9 = LAST_INSERT_ID();
INSERT INTO detalle_cita (id_cita, id_veterinario, id_servicio, observaciones) VALUES (@c9, @vet1, @s_consulta, 'Se rasca las orejas'); SET @dc10 = LAST_INSERT_ID();
INSERT INTO detalle_cita (id_cita, id_veterinario, id_servicio, observaciones) VALUES (@c9, @vet3, @s_rabia, NULL);                   SET @dc11 = LAST_INSERT_ID();

-- c10 sab 21/11 10:00 PENDIENTE: Michi, control con Carmen
INSERT INTO cita (id_administrador, id_mascota, fecha_hora, estado, es_emergencia)
VALUES (NULL, @m3, '2026-11-21 10:00:00', 'PENDIENTE', 0); SET @c10 = LAST_INSERT_ID();
INSERT INTO detalle_cita (id_cita, id_veterinario, id_servicio, observaciones) VALUES (@c10, @vet3, @s_control, 'Control post gastroenteritis'); SET @dc12 = LAST_INSERT_ID();

-- -------------------------------------------------------------------------------------
-- ATENCIONES (una por cada servicio de las citas COMPLETADO)
-- -------------------------------------------------------------------------------------
INSERT INTO atencion (id_detalle_cita, fecha_hora, motivo_consulta, observaciones)
VALUES (@dc1, '2026-10-05 10:05:00', 'Tos seca y estornudos', 'Temperatura 39.2 C. Pulmones limpios a la auscultacion'); SET @at1 = LAST_INSERT_ID();
INSERT INTO atencion (id_detalle_cita, fecha_hora, motivo_consulta, observaciones)
VALUES (@dc2, '2026-10-05 10:35:00', 'Vacunacion anual', 'Vacuna aplicada sin reacciones');                              SET @at2 = LAST_INSERT_ID();
INSERT INTO atencion (id_detalle_cita, fecha_hora, motivo_consulta, observaciones)
VALUES (@dc3, '2026-10-14 15:35:00', 'Vomitos y decaimiento', 'Deshidratacion leve; se aplico suero subcutaneo');         SET @at3 = LAST_INSERT_ID();
INSERT INTO atencion (id_detalle_cita, fecha_hora, motivo_consulta, observaciones)
VALUES (@dc4, '2026-10-23 09:05:00', 'Vomitos repetidos tras ingerir comida de la calle', 'Fluidoterapia IV por 2 horas; alta con tratamiento'); SET @at4 = LAST_INSERT_ID();
INSERT INTO atencion (id_detalle_cita, fecha_hora, motivo_consulta, observaciones)
VALUES (@dc5, '2026-11-03 16:05:00', 'Bano y corte', 'Leve enrojecimiento en abdomen');                                 SET @at5 = LAST_INSERT_ID();

INSERT INTO atencion_diagnostico (id_atencion, id_diagnostico, nivel_gravedad, detalle_diagnostico) VALUES
  (@at1, @d_traq,   'LEVE',     'Sin compromiso pulmonar'),
  (@at2, @d_sano,   'LEVE',     'Apto para vacunacion'),
  (@at3, @d_gastro, 'MODERADO', 'Probable cambio brusco de alimento'),
  (@at3, @d_deshid, 'LEVE',     NULL),
  (@at4, @d_intox,  'GRAVE',    'Requirio fluidoterapia intravenosa'),
  (@at4, @d_deshid, 'MODERADO', NULL),
  (@at5, @d_derma,  'LEVE',     'Revisar en la proxima consulta');

-- Insumos: los cobrables aparecen luego en la boleta (detalle_boleta_art con id_insumo_utilizado)
INSERT INTO insumo_utilizado (id_atencion, id_articulo, cantidad_utilizada, es_cobrable) VALUES (@at1, @a_jeringa, 1, 0); SET @iu1 = LAST_INSERT_ID();
INSERT INTO insumo_utilizado (id_atencion, id_articulo, cantidad_utilizada, es_cobrable) VALUES (@at2, @a_jeringa, 1, 0); SET @iu2 = LAST_INSERT_ID();
INSERT INTO insumo_utilizado (id_atencion, id_articulo, cantidad_utilizada, es_cobrable) VALUES (@at3, @a_suero,   1, 1); SET @iu3 = LAST_INSERT_ID();
INSERT INTO insumo_utilizado (id_atencion, id_articulo, cantidad_utilizada, es_cobrable) VALUES (@at3, @a_jeringa, 2, 0); SET @iu4 = LAST_INSERT_ID();
INSERT INTO insumo_utilizado (id_atencion, id_articulo, cantidad_utilizada, es_cobrable) VALUES (@at4, @a_suero,   2, 1); SET @iu5 = LAST_INSERT_ID();
INSERT INTO insumo_utilizado (id_atencion, id_articulo, cantidad_utilizada, es_cobrable) VALUES (@at4, @a_cateter, 1, 1); SET @iu6 = LAST_INSERT_ID();
INSERT INTO insumo_utilizado (id_atencion, id_articulo, cantidad_utilizada, es_cobrable) VALUES (@at5, @a_shampoo, 1, 0); SET @iu7 = LAST_INSERT_ID();

-- -------------------------------------------------------------------------------------
-- RECETAS
-- -------------------------------------------------------------------------------------
INSERT INTO receta (id_atencion, fecha_emision, indicaciones_generales)
VALUES (@at1, '2026-10-05', 'Reposo, evitar contacto con otros perros por 7 dias'); SET @r1 = LAST_INSERT_ID();
INSERT INTO detalle_receta (id_receta, id_articulo, dosis, frecuencia, duracion_dias, cantidad_total)
VALUES (@r1, @a_amox, '1 tableta', 'Cada 12 horas', 7, 14);

INSERT INTO receta (id_atencion, fecha_emision, indicaciones_generales)
VALUES (@at3, '2026-10-14', 'Dieta blanda por 3 dias y agua a libre disposicion'); SET @r2 = LAST_INSERT_ID();
INSERT INTO detalle_receta (id_receta, id_articulo, dosis, frecuencia, duracion_dias, cantidad_total)
VALUES (@r2, @a_metro, '1/2 tableta', 'Cada 12 horas', 5, 5);
INSERT INTO detalle_receta (id_receta, id_articulo, dosis, frecuencia, duracion_dias, cantidad_total)
VALUES (@r2, @a_probio, '1 sobre', 'Cada 24 horas', 10, 10);

INSERT INTO receta (id_atencion, fecha_emision, indicaciones_generales)
VALUES (@at4, '2026-10-23', 'Ayuno de 12 horas y luego dieta gastrointestinal. Volver si hay vomitos'); SET @r3 = LAST_INSERT_ID();
INSERT INTO detalle_receta (id_receta, id_articulo, dosis, frecuencia, duracion_dias, cantidad_total)
VALUES (@r3, @a_metro, '1 tableta', 'Cada 12 horas', 5, 10);

-- -------------------------------------------------------------------------------------
-- BOLETAS (subtotal es columna generada: no se inserta)
-- -------------------------------------------------------------------------------------
-- b1 Ana: consulta 60 + vacuna 45 + collar 35 = 140.00
INSERT INTO boleta (id_cliente, serie_correlativo, fecha, total, metodo_pago)
VALUES (@cli1, 'B001-000001', '2026-10-05 11:00:00', 140.00, 'YAPE'); SET @b1 = LAST_INSERT_ID();
INSERT INTO detalle_boleta_serv (id_boleta, id_detalle_cita, cantidad, precio_unitario) VALUES (@b1, @dc1, 1, 60.00);
INSERT INTO detalle_boleta_serv (id_boleta, id_detalle_cita, cantidad, precio_unitario) VALUES (@b1, @dc2, 1, 45.00);
INSERT INTO detalle_boleta_art (id_boleta, id_articulo, id_insumo_utilizado, cantidad, precio_unitario) VALUES (@b1, @a_collar, NULL, 1, 35.00);

-- b2 Luis: consulta 60 + suero (insumo) 18 + metronidazol 25 + probiotico 45 = 148.00
INSERT INTO boleta (id_cliente, serie_correlativo, fecha, total, metodo_pago)
VALUES (@cli2, 'B001-000002', '2026-10-14 16:30:00', 148.00, 'TARJETA_DE_DEBITO'); SET @b2 = LAST_INSERT_ID();
INSERT INTO detalle_boleta_serv (id_boleta, id_detalle_cita, cantidad, precio_unitario) VALUES (@b2, @dc3, 1, 60.00);
INSERT INTO detalle_boleta_art (id_boleta, id_articulo, id_insumo_utilizado, cantidad, precio_unitario) VALUES (@b2, @a_suero,  @iu3, 1, 18.00);
INSERT INTO detalle_boleta_art (id_boleta, id_articulo, id_insumo_utilizado, cantidad, precio_unitario) VALUES (@b2, @a_metro,  NULL, 1, 25.00);
INSERT INTO detalle_boleta_art (id_boleta, id_articulo, id_insumo_utilizado, cantidad, precio_unitario) VALUES (@b2, @a_probio, NULL, 1, 45.00);

-- b3 Maria: emergencia 150 + suero (insumo) 2x18 + cateter (insumo) 12 + metronidazol 25 = 223.00
INSERT INTO boleta (id_cliente, serie_correlativo, fecha, total, metodo_pago)
VALUES (@cli3, 'B001-000003', '2026-10-23 11:30:00', 223.00, 'TARJETA_DE_CREDITO'); SET @b3 = LAST_INSERT_ID();
INSERT INTO detalle_boleta_serv (id_boleta, id_detalle_cita, cantidad, precio_unitario) VALUES (@b3, @dc4, 1, 150.00);
INSERT INTO detalle_boleta_art (id_boleta, id_articulo, id_insumo_utilizado, cantidad, precio_unitario) VALUES (@b3, @a_suero,   @iu5, 2, 18.00);
INSERT INTO detalle_boleta_art (id_boleta, id_articulo, id_insumo_utilizado, cantidad, precio_unitario) VALUES (@b3, @a_cateter, @iu6, 1, 12.00);
INSERT INTO detalle_boleta_art (id_boleta, id_articulo, id_insumo_utilizado, cantidad, precio_unitario) VALUES (@b3, @a_metro,   NULL, 1, 25.00);

-- b4 Ana: bano y corte 50 = 50.00
INSERT INTO boleta (id_cliente, serie_correlativo, fecha, total, metodo_pago)
VALUES (@cli1, 'B001-000004', '2026-11-03 17:45:00', 50.00, 'EFECTIVO'); SET @b4 = LAST_INSERT_ID();
INSERT INTO detalle_boleta_serv (id_boleta, id_detalle_cita, cantidad, precio_unitario) VALUES (@b4, @dc5, 1, 50.00);

-- b5 Pedro (venta en caja): alimento gato 89.90 + 2 pelotas 30.00 = 119.90
INSERT INTO boleta (id_cliente, serie_correlativo, fecha, total, metodo_pago)
VALUES (@cli4, 'B001-000005', '2026-11-06 12:10:00', 119.90, 'EFECTIVO'); SET @b5 = LAST_INSERT_ID();
INSERT INTO detalle_boleta_art (id_boleta, id_articulo, id_insumo_utilizado, cantidad, precio_unitario) VALUES (@b5, @a_ali_gato, NULL, 1, 89.90);
INSERT INTO detalle_boleta_art (id_boleta, id_articulo, id_insumo_utilizado, cantidad, precio_unitario) VALUES (@b5, @a_pelota,   NULL, 2, 15.00);

-- b6 Sofia (venta en caja): alimento perro 189.90 + pipeta 38.50 + snack 24.50 = 252.90
INSERT INTO boleta (id_cliente, serie_correlativo, fecha, total, metodo_pago)
VALUES (@cli5, 'B001-000006', '2026-11-07 10:15:00', 252.90, 'YAPE'); SET @b6 = LAST_INSERT_ID();
INSERT INTO detalle_boleta_art (id_boleta, id_articulo, id_insumo_utilizado, cantidad, precio_unitario) VALUES (@b6, @a_ali_perro, NULL, 1, 189.90);
INSERT INTO detalle_boleta_art (id_boleta, id_articulo, id_insumo_utilizado, cantidad, precio_unitario) VALUES (@b6, @a_pipeta,    NULL, 1, 38.50);
INSERT INTO detalle_boleta_art (id_boleta, id_articulo, id_insumo_utilizado, cantidad, precio_unitario) VALUES (@b6, @a_snack,     NULL, 1, 24.50);

COMMIT;

-- -------------------------------------------------------------------------------------
-- IDS GENERADOS (para usarlos en las pruebas y en las URLs de la API)
-- -------------------------------------------------------------------------------------
SELECT 'administradores' AS entidad, CONCAT_WS(', ', @adm1, @adm2) AS ids
UNION ALL SELECT 'veterinarios', CONCAT_WS(', ', @vet1, @vet2, @vet3)
UNION ALL SELECT 'clientes',     CONCAT_WS(', ', @cli1, @cli2, @cli3, @cli4, @cli5)
UNION ALL SELECT 'mascotas',     CONCAT_WS(', ', @m1, @m2, @m3, @m4, @m5, @m6, @m7, @m8)
UNION ALL SELECT 'citas',        CONCAT_WS(', ', @c1, @c2, @c3, @c4, @c5, @c6, @c7, @c8, @c9, @c10)
UNION ALL SELECT 'atenciones',   CONCAT_WS(', ', @at1, @at2, @at3, @at4, @at5)
UNION ALL SELECT 'recetas',      CONCAT_WS(', ', @r1, @r2, @r3)
UNION ALL SELECT 'boletas',      CONCAT_WS(', ', @b1, @b2, @b3, @b4, @b5, @b6);

-- ==================== 3. Datos de la semana de la exposicion ====================
-- =====================================================================================
-- Patitas Arriba: datos extra para la semana de la exposicion (16 al 26 de noviembre 2026)
--
-- * Usa los datos de la parte 2 (clientes, mascotas, horarios y catalogos), asi que va despues.
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
-- IDS QUE YA EXISTEN (de la parte 2), buscados por datos unicos
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

-- ---- Citas de la parte 2 que seguian abiertas ------------------------------------------
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
