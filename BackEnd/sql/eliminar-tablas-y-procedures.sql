-- Borra TODAS las tablas (estructura y datos) y TODOS los procedures del esquema patitasarriba.
-- No se puede deshacer. El esquema queda vacio.
-- Para reinstalar: crear-tablas-y-procedures.sql y despues cargar-datos.sql.
-- Para solo vaciar los datos y conservar tablas y procedures, usar limpiar-datos.sql.
-- Tablas: primero las hijas; las llaves foraneas se desactivan mientras se borran.
-- Procedures: todos los de los procedures-*.sql, incluidos algunos antiguos que ya no se usan.

USE patitasarriba;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS detalle_boleta_art;
DROP TABLE IF EXISTS detalle_boleta_serv;
DROP TABLE IF EXISTS insumo_utilizado;
DROP TABLE IF EXISTS atencion_diagnostico;
DROP TABLE IF EXISTS detalle_receta;
DROP TABLE IF EXISTS receta;
DROP TABLE IF EXISTS atencion;
DROP TABLE IF EXISTS detalle_cita;
DROP TABLE IF EXISTS cita;
DROP TABLE IF EXISTS boleta;
DROP TABLE IF EXISTS articulo_categoria;
DROP TABLE IF EXISTS horario_veterinario;
DROP TABLE IF EXISTS horario_administrador;
DROP TABLE IF EXISTS mascota;
DROP TABLE IF EXISTS cliente;
DROP TABLE IF EXISTS veterinario;
DROP TABLE IF EXISTS administrador;
DROP TABLE IF EXISTS token_recuperacion;
DROP TABLE IF EXISTS registro_pendiente;
DROP TABLE IF EXISTS cuenta;
DROP TABLE IF EXISTS horario;
DROP TABLE IF EXISTS diagnostico;
DROP TABLE IF EXISTS servicio;
DROP TABLE IF EXISTS categoria;
DROP TABLE IF EXISTS articulo;

SET FOREIGN_KEY_CHECKS = 1;


-- =====================================================================================
-- PROCEDIMIENTOS ALMACENADOS
-- =====================================================================================

-- procedures-administrador.sql
DROP PROCEDURE IF EXISTS insertar_administrador;
DROP PROCEDURE IF EXISTS modificar_administrador;
DROP PROCEDURE IF EXISTS eliminar_administrador;
DROP PROCEDURE IF EXISTS buscar_administrador_por_id;
DROP PROCEDURE IF EXISTS buscar_administrador_por_dni;
DROP PROCEDURE IF EXISTS buscar_administrador_por_telefono;
DROP PROCEDURE IF EXISTS buscar_administrador_por_cuenta;
DROP PROCEDURE IF EXISTS listar_administradores;

-- procedures-articulo.sql
DROP PROCEDURE IF EXISTS insertar_articulo;
DROP PROCEDURE IF EXISTS modificar_articulo;
DROP PROCEDURE IF EXISTS eliminar_articulo;
DROP PROCEDURE IF EXISTS buscar_articulo_por_id;
DROP PROCEDURE IF EXISTS listar_articulo;

-- procedures-articulo_categoria.sql
DROP PROCEDURE IF EXISTS insertar_articulo_categoria;
DROP PROCEDURE IF EXISTS eliminar_articulo_categoria;
DROP PROCEDURE IF EXISTS eliminar_categorias_por_articulo;
DROP PROCEDURE IF EXISTS listar_categorias_por_articulo;
DROP PROCEDURE IF EXISTS listar_articulos_por_categoria;

-- procedures-atencion.sql
DROP PROCEDURE IF EXISTS insertar_atencion;
DROP PROCEDURE IF EXISTS modificar_atencion;
DROP PROCEDURE IF EXISTS eliminar_atencion;
DROP PROCEDURE IF EXISTS buscar_atencion_por_id;
DROP PROCEDURE IF EXISTS buscar_atencion_por_detalle_cita;
DROP PROCEDURE IF EXISTS listar_atenciones;
DROP PROCEDURE IF EXISTS listar_atenciones_por_mascota;
DROP PROCEDURE IF EXISTS listar_estados_atencion;

-- procedures-atencion_diagnostico.sql
DROP PROCEDURE IF EXISTS insertar_atencion_diagnostico;
DROP PROCEDURE IF EXISTS modificar_atencion_diagnostico;
DROP PROCEDURE IF EXISTS eliminar_atencion_diagnostico;
DROP PROCEDURE IF EXISTS eliminar_diagnosticos_por_atencion;
DROP PROCEDURE IF EXISTS eliminar_atencion_diagnosticos_por_atencion_medica;
DROP PROCEDURE IF EXISTS buscar_atencion_diagnostico;
DROP PROCEDURE IF EXISTS listar_diagnosticos_por_atencion;
DROP PROCEDURE IF EXISTS listar_atencion_diagnosticos_por_atencion_medica;
DROP PROCEDURE IF EXISTS listar_atenciones_por_diagnostico;

-- procedures-boleta.sql
DROP PROCEDURE IF EXISTS insertar_boleta;
DROP PROCEDURE IF EXISTS modificar_boleta;
DROP PROCEDURE IF EXISTS eliminar_boleta;
DROP PROCEDURE IF EXISTS buscar_boleta_por_id;
DROP PROCEDURE IF EXISTS buscar_boleta_por_serie_correlativo;
DROP PROCEDURE IF EXISTS obtener_ultimo_correlativo;
DROP PROCEDURE IF EXISTS listar_boletas;
DROP PROCEDURE IF EXISTS listar_boletas_por_cliente;

-- procedures-categoria.sql
DROP PROCEDURE IF EXISTS insertar_categoria;
DROP PROCEDURE IF EXISTS modificar_categoria;
DROP PROCEDURE IF EXISTS eliminar_categoria;
DROP PROCEDURE IF EXISTS buscar_categoria_por_id;
DROP PROCEDURE IF EXISTS buscar_categoria_por_nombre;
DROP PROCEDURE IF EXISTS listar_categoria;

-- procedures-cita.sql
DROP PROCEDURE IF EXISTS insertar_cita;
DROP PROCEDURE IF EXISTS modificar_cita;
DROP PROCEDURE IF EXISTS eliminar_cita;
DROP PROCEDURE IF EXISTS buscar_cita_por_id;
DROP PROCEDURE IF EXISTS listar_cita;
DROP PROCEDURE IF EXISTS listar_cita_por_mascota;
DROP PROCEDURE IF EXISTS listar_cita_por_cliente;
DROP PROCEDURE IF EXISTS listar_cita_por_rango;

-- procedures-cliente.sql
DROP PROCEDURE IF EXISTS insertar_cliente;
DROP PROCEDURE IF EXISTS modificar_cliente;
DROP PROCEDURE IF EXISTS eliminar_cliente;
DROP PROCEDURE IF EXISTS buscar_cliente_por_id;
DROP PROCEDURE IF EXISTS buscar_cliente_por_dni;
DROP PROCEDURE IF EXISTS buscar_cliente_por_telefono;
DROP PROCEDURE IF EXISTS buscar_cliente_por_cuenta;
DROP PROCEDURE IF EXISTS listar_clientes;

-- procedures-cuenta.sql
DROP PROCEDURE IF EXISTS insertar_cuenta;
DROP PROCEDURE IF EXISTS modificar_cuenta;
DROP PROCEDURE IF EXISTS eliminar_cuenta;
DROP PROCEDURE IF EXISTS buscar_cuenta_por_id;
DROP PROCEDURE IF EXISTS buscar_cuenta_por_nombre_usuario;
DROP PROCEDURE IF EXISTS buscar_cuenta_por_correo;
DROP PROCEDURE IF EXISTS listar_cuentas;

-- procedures-detalle_boleta_art.sql
DROP PROCEDURE IF EXISTS insertar_detalle_boleta_art;
DROP PROCEDURE IF EXISTS modificar_detalle_boleta_art;
DROP PROCEDURE IF EXISTS eliminar_detalle_boleta_art;
DROP PROCEDURE IF EXISTS eliminar_detalles_boleta_art_por_boleta;
DROP PROCEDURE IF EXISTS buscar_detalle_boleta_art_por_id;
DROP PROCEDURE IF EXISTS buscar_detalle_boleta_art_por_insumo;
DROP PROCEDURE IF EXISTS listar_detalles_boleta_art;
DROP PROCEDURE IF EXISTS listar_detalles_boleta_art_por_boleta;

-- procedures-detalle_boleta_serv.sql
DROP PROCEDURE IF EXISTS insertar_detalle_boleta_serv;
DROP PROCEDURE IF EXISTS modificar_detalle_boleta_serv;
DROP PROCEDURE IF EXISTS eliminar_detalle_boleta_serv;
DROP PROCEDURE IF EXISTS eliminar_detalles_boleta_serv_por_boleta;
DROP PROCEDURE IF EXISTS buscar_detalle_boleta_serv_por_id;
DROP PROCEDURE IF EXISTS buscar_detalle_boleta_serv_por_detalle_cita;
DROP PROCEDURE IF EXISTS listar_detalles_boleta_serv;
DROP PROCEDURE IF EXISTS listar_detalles_boleta_serv_por_boleta;

-- procedures-detalle_cita.sql
DROP PROCEDURE IF EXISTS insertar_detalle_cita;
DROP PROCEDURE IF EXISTS modificar_detalle_cita;
DROP PROCEDURE IF EXISTS eliminar_detalle_cita;
DROP PROCEDURE IF EXISTS eliminar_detalles_por_cita;
DROP PROCEDURE IF EXISTS buscar_detalle_cita_por_id;
DROP PROCEDURE IF EXISTS listar_detalle_cita_por_cita;
DROP PROCEDURE IF EXISTS listar_detalle_cita_por_veterinario;
DROP PROCEDURE IF EXISTS listar_detalle_cita_por_servicio;
DROP PROCEDURE IF EXISTS listar_servicios_por_cobrar_por_cliente;

-- procedures-detalle_receta.sql
DROP PROCEDURE IF EXISTS insertar_detalle_receta;
DROP PROCEDURE IF EXISTS modificar_detalle_receta;
DROP PROCEDURE IF EXISTS eliminar_detalle_receta;
DROP PROCEDURE IF EXISTS eliminar_detalles_por_receta;
DROP PROCEDURE IF EXISTS buscar_detalle_receta_por_id;
DROP PROCEDURE IF EXISTS listar_detalles_receta;
DROP PROCEDURE IF EXISTS listar_detalles_por_receta;
DROP PROCEDURE IF EXISTS listar_detalles_receta_por_articulo;

-- procedures-diagnostico.sql
DROP PROCEDURE IF EXISTS insertar_diagnostico;
DROP PROCEDURE IF EXISTS modificar_diagnostico;
DROP PROCEDURE IF EXISTS actualizar_diagnostico;
DROP PROCEDURE IF EXISTS eliminar_diagnostico;
DROP PROCEDURE IF EXISTS buscar_diagnostico_por_id;
DROP PROCEDURE IF EXISTS buscar_diagnostico_por_nombre_enfermedad;
DROP PROCEDURE IF EXISTS listar_diagnosticos;

-- procedures-horario.sql
DROP PROCEDURE IF EXISTS insertar_horario;
DROP PROCEDURE IF EXISTS modificar_horario;
DROP PROCEDURE IF EXISTS eliminar_horario;
DROP PROCEDURE IF EXISTS buscar_horario_por_id;
DROP PROCEDURE IF EXISTS buscar_horario_por_dia;
DROP PROCEDURE IF EXISTS listar_horarios;

-- procedures-horario_administrador.sql
DROP PROCEDURE IF EXISTS insertar_horario_administrador;
DROP PROCEDURE IF EXISTS modificar_horario_administrador;
DROP PROCEDURE IF EXISTS eliminar_horario_administrador;
DROP PROCEDURE IF EXISTS buscar_horario_administrador_por_id;
DROP PROCEDURE IF EXISTS listar_horarios_administrador;
DROP PROCEDURE IF EXISTS eliminar_horarios_por_administrador;
DROP PROCEDURE IF EXISTS listar_horarios_por_administrador;
DROP PROCEDURE IF EXISTS listar_administradores_por_horario;

-- procedures-horario_veterinario.sql
DROP PROCEDURE IF EXISTS insertar_horario_veterinario;
DROP PROCEDURE IF EXISTS modificar_horario_veterinario;
DROP PROCEDURE IF EXISTS eliminar_horario_veterinario;
DROP PROCEDURE IF EXISTS buscar_horario_veterinario_por_id;
DROP PROCEDURE IF EXISTS listar_horarios_veterinario;
DROP PROCEDURE IF EXISTS eliminar_horarios_por_veterinario;
DROP PROCEDURE IF EXISTS listar_horarios_por_veterinario;
DROP PROCEDURE IF EXISTS listar_veterinarios_por_horario;

-- procedures-insumo_utilizado.sql
DROP PROCEDURE IF EXISTS insertar_insumo_utilizado;
DROP PROCEDURE IF EXISTS modificar_insumo_utilizado;
DROP PROCEDURE IF EXISTS eliminar_insumo_utilizado;
DROP PROCEDURE IF EXISTS eliminar_insumos_por_atencion;
DROP PROCEDURE IF EXISTS eliminar_insumos_por_atencion_tratamiento;
DROP PROCEDURE IF EXISTS buscar_insumo_utilizado_por_id;
DROP PROCEDURE IF EXISTS listar_insumos_por_atencion;
DROP PROCEDURE IF EXISTS listar_insumos_por_atencion_tratamiento;
DROP PROCEDURE IF EXISTS listar_insumos_cobrables_pendientes_por_atencion;
DROP PROCEDURE IF EXISTS listar_insumos_por_cobrar_por_cliente;

-- procedures-mascota.sql
DROP PROCEDURE IF EXISTS insertar_mascota;
DROP PROCEDURE IF EXISTS modificar_mascota;
DROP PROCEDURE IF EXISTS eliminar_mascota;
DROP PROCEDURE IF EXISTS eliminar_mascotas_por_cliente;
DROP PROCEDURE IF EXISTS buscar_mascota_por_id;
DROP PROCEDURE IF EXISTS buscar_mascota_por_nombre;
DROP PROCEDURE IF EXISTS listar_mascotas;
DROP PROCEDURE IF EXISTS listar_mascotas_por_cliente;

-- procedures-receta.sql
DROP PROCEDURE IF EXISTS insertar_receta;
DROP PROCEDURE IF EXISTS modificar_receta;
DROP PROCEDURE IF EXISTS eliminar_receta;
DROP PROCEDURE IF EXISTS buscar_receta_por_id;
DROP PROCEDURE IF EXISTS listar_recetas;
DROP PROCEDURE IF EXISTS listar_recetas_por_atencion;

-- procedures-registro_pendiente.sql
DROP PROCEDURE IF EXISTS insertar_registro_pendiente;
DROP PROCEDURE IF EXISTS buscar_registro_pendiente_por_token;
DROP PROCEDURE IF EXISTS eliminar_registro_pendiente;
DROP PROCEDURE IF EXISTS eliminar_registros_pendientes_por_correo;
DROP PROCEDURE IF EXISTS eliminar_registros_pendientes_vencidos;

-- procedures-reportes.sql
DROP PROCEDURE IF EXISTS reporte_agenda_diaria;
DROP PROCEDURE IF EXISTS reporte_historial_clinico;
DROP PROCEDURE IF EXISTS reporte_detalle_historial;
DROP PROCEDURE IF EXISTS reporte_boleta;
DROP PROCEDURE IF EXISTS reporte_detalle_boleta;
DROP PROCEDURE IF EXISTS reporte_receta;
DROP PROCEDURE IF EXISTS reporte_detalle_receta;

-- procedures-servicio.sql
DROP PROCEDURE IF EXISTS insertar_servicio;
DROP PROCEDURE IF EXISTS modificar_servicio;
DROP PROCEDURE IF EXISTS eliminar_servicio;
DROP PROCEDURE IF EXISTS buscar_servicio_por_id;
DROP PROCEDURE IF EXISTS buscar_servicio_por_nombre;
DROP PROCEDURE IF EXISTS listar_servicios;

-- procedures-token_recuperacion.sql
DROP PROCEDURE IF EXISTS insertar_token_recuperacion;
DROP PROCEDURE IF EXISTS buscar_token_recuperacion_por_token;
DROP PROCEDURE IF EXISTS marcar_token_recuperacion_usado;
DROP PROCEDURE IF EXISTS invalidar_tokens_recuperacion_de_cuenta;

-- procedures-veterinario.sql
DROP PROCEDURE IF EXISTS insertar_veterinario;
DROP PROCEDURE IF EXISTS modificar_veterinario;
DROP PROCEDURE IF EXISTS eliminar_veterinario;
DROP PROCEDURE IF EXISTS buscar_veterinario_por_id;
DROP PROCEDURE IF EXISTS buscar_veterinario_por_dni;
DROP PROCEDURE IF EXISTS buscar_veterinario_por_telefono;
DROP PROCEDURE IF EXISTS buscar_veterinario_por_colegiatura;
DROP PROCEDURE IF EXISTS buscar_veterinario_por_cuenta;
DROP PROCEDURE IF EXISTS listar_veterinarios;
