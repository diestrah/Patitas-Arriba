-- Procedimientos CRUD para patitasarriba.modelo.atencion.InsumoUtilizado
-- Un insumo cuelga de una atencion (id_atencion). es_cobrable = 1: lo paga el cliente aparte

use patitasarriba;

DROP PROCEDURE IF EXISTS insertar_insumo_utilizado;
DROP PROCEDURE IF EXISTS modificar_insumo_utilizado;
DROP PROCEDURE IF EXISTS eliminar_insumo_utilizado;
DROP PROCEDURE IF EXISTS eliminar_insumos_por_atencion;
DROP PROCEDURE IF EXISTS eliminar_insumos_por_atencion_tratamiento;
DROP PROCEDURE IF EXISTS buscar_insumo_utilizado_por_id;
DROP PROCEDURE IF EXISTS listar_insumos_por_atencion;
DROP PROCEDURE IF EXISTS listar_insumos_por_atencion_tratamiento;
DROP PROCEDURE IF EXISTS listar_insumos_cobrables_pendientes_por_atencion;

DELIMITER //

-- Registra un insumo usado en una atencion y devuelve el id generado
CREATE PROCEDURE insertar_insumo_utilizado (
    IN p_id_atencion INT,
    IN p_id_articulo INT,
    IN p_cantidad_utilizada INT,
    IN p_es_cobrable TINYINT(1),
    IN p_activo TINYINT(1),
    OUT p_id INT
)
BEGIN
    INSERT INTO insumo_utilizado (
        id_atencion, id_articulo, cantidad_utilizada, es_cobrable, activo
    )
    VALUES (
        p_id_atencion, p_id_articulo, p_cantidad_utilizada, p_es_cobrable, p_activo
    );

    SET p_id = LAST_INSERT_ID();
END //

-- Actualiza todos los campos de un insumo existente (la atencion no cambia)
CREATE PROCEDURE modificar_insumo_utilizado (
    IN p_id INT,
    IN p_id_articulo INT,
    IN p_cantidad_utilizada INT,
    IN p_es_cobrable TINYINT(1),
    IN p_activo TINYINT(1)
)
BEGIN
    UPDATE insumo_utilizado
    SET id_articulo = p_id_articulo,
        cantidad_utilizada = p_cantidad_utilizada,
        es_cobrable = p_es_cobrable,
        activo = p_activo
    WHERE id_insumo_utilizado = p_id;
END //

-- Borrado logico de un insumo
CREATE PROCEDURE eliminar_insumo_utilizado (IN p_id INT)
BEGIN
    UPDATE insumo_utilizado
    SET activo = 0
    WHERE id_insumo_utilizado = p_id;
END //

-- Borrado logico de todos los insumos de UNA atencion (paso previo a eliminar la atencion,
-- dentro de la misma transaccion)
CREATE PROCEDURE eliminar_insumos_por_atencion (IN p_id_atencion INT)
BEGIN
    UPDATE insumo_utilizado
    SET activo = 0
    WHERE id_atencion = p_id_atencion;
END //

-- Solo devuelve el insumo si esta activo
CREATE PROCEDURE buscar_insumo_utilizado_por_id (IN p_id INT)
BEGIN
    SELECT *
    FROM insumo_utilizado
    WHERE id_insumo_utilizado = p_id
      AND activo = 1;
END //

-- Insumos activos de UNA atencion (patron maestro-detalle)
CREATE PROCEDURE listar_insumos_por_atencion (IN p_id_atencion INT)
BEGIN
    SELECT *
    FROM insumo_utilizado
    WHERE id_atencion = p_id_atencion
      AND activo = 1;
END //

-- Insumos cobrables de una atencion que todavia no estan en ninguna linea de boleta activa
-- (paso 2 del armado de la boleta)
CREATE PROCEDURE listar_insumos_cobrables_pendientes_por_atencion (IN p_id_atencion INT)
BEGIN
    SELECT iu.*
    FROM insumo_utilizado iu
    WHERE iu.id_atencion = p_id_atencion
      AND iu.es_cobrable = 1
      AND iu.activo = 1
      AND NOT EXISTS (
          SELECT 1
          FROM detalle_boleta_art dba
          WHERE dba.id_insumo_utilizado = iu.id_insumo_utilizado
            AND dba.activo = 1
      );
END //

DELIMITER ;
