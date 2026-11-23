-- Procedimientos para patitasarriba.modelo.usuario.RegistroPendiente
-- Datos de un cliente que se registro y aun no verifica su correo. Es una tabla de paso:
-- al verificar (o al vencer) la fila se borra, por eso aqui si se usa DELETE y no borrado logico.

use patitasarriba;

DROP PROCEDURE IF EXISTS insertar_registro_pendiente;
DROP PROCEDURE IF EXISTS buscar_registro_pendiente_por_token;
DROP PROCEDURE IF EXISTS eliminar_registro_pendiente;
DROP PROCEDURE IF EXISTS eliminar_registros_pendientes_por_correo;
DROP PROCEDURE IF EXISTS eliminar_registros_pendientes_vencidos;

DELIMITER //

-- Guarda el registro pendiente y devuelve el id generado
CREATE PROCEDURE insertar_registro_pendiente (
    IN p_dni VARCHAR(8),
    IN p_nombres VARCHAR(50),
    IN p_apellido_paterno VARCHAR(45),
    IN p_apellido_materno VARCHAR(45),
    IN p_telefono VARCHAR(9),
    IN p_correo VARCHAR(100),
    IN p_nombre_usuario VARCHAR(45),
    IN p_password VARCHAR(255),
    IN p_token_hash VARCHAR(64),
    IN p_fecha_creacion DATETIME,
    IN p_fecha_vencimiento DATETIME,
    OUT p_id INT
)
BEGIN
    INSERT INTO registro_pendiente (dni, nombres, apellido_paterno, apellido_materno, telefono, correo,
                                    nombre_usuario, password, token_hash, fecha_creacion, fecha_vencimiento)
    VALUES (p_dni, p_nombres, p_apellido_paterno, p_apellido_materno, p_telefono, p_correo,
            p_nombre_usuario, p_password, p_token_hash, p_fecha_creacion, p_fecha_vencimiento);

    SET p_id = LAST_INSERT_ID();
END //

-- Busca por el hash del token (el token en claro solo viaja en el correo). El vencimiento lo revisa la capa de negocio
CREATE PROCEDURE buscar_registro_pendiente_por_token (IN p_token_hash VARCHAR(64))
BEGIN
    SELECT *
    FROM registro_pendiente
    WHERE token_hash = p_token_hash;
END //

CREATE PROCEDURE eliminar_registro_pendiente (IN p_id INT)
BEGIN
    DELETE FROM registro_pendiente
    WHERE id_registro_pendiente = p_id;
END //

-- Un nuevo intento con el mismo correo reemplaza al anterior
CREATE PROCEDURE eliminar_registros_pendientes_por_correo (IN p_correo VARCHAR(100))
BEGIN
    DELETE FROM registro_pendiente
    WHERE correo = p_correo;
END //

-- Limpieza de los registros que nunca se verificaron
CREATE PROCEDURE eliminar_registros_pendientes_vencidos (IN p_ahora DATETIME)
BEGIN
    DELETE FROM registro_pendiente
    WHERE fecha_vencimiento < p_ahora;
END //

DELIMITER ;
