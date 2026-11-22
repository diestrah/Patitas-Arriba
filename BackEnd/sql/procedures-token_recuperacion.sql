-- Procedimientos para patitasarriba.modelo.usuario.TokenRecuperacion
-- Codigo de un solo uso con vencimiento para restablecer la contrasena (olvido o activacion de una
-- cuenta creada por un administrador). Solo se guarda el hash del codigo.

use patitasarriba;

DROP PROCEDURE IF EXISTS insertar_token_recuperacion;
DROP PROCEDURE IF EXISTS buscar_token_recuperacion_por_token;
DROP PROCEDURE IF EXISTS marcar_token_recuperacion_usado;
DROP PROCEDURE IF EXISTS invalidar_tokens_recuperacion_de_cuenta;

DELIMITER //

-- Guarda el token y devuelve el id generado
CREATE PROCEDURE insertar_token_recuperacion (
    IN p_id_cuenta INT,
    IN p_token_hash VARCHAR(64),
    IN p_fecha_creacion DATETIME,
    IN p_fecha_vencimiento DATETIME,
    OUT p_id INT
)
BEGIN
    INSERT INTO token_recuperacion (id_cuenta, token_hash, fecha_creacion, fecha_vencimiento, usado)
    VALUES (p_id_cuenta, p_token_hash, p_fecha_creacion, p_fecha_vencimiento, 0);

    SET p_id = LAST_INSERT_ID();
END //

-- Busca por el hash del token. Si esta usado o vencido lo decide la capa de negocio
CREATE PROCEDURE buscar_token_recuperacion_por_token (IN p_token_hash VARCHAR(64))
BEGIN
    SELECT *
    FROM token_recuperacion
    WHERE token_hash = p_token_hash;
END //

CREATE PROCEDURE marcar_token_recuperacion_usado (IN p_id INT)
BEGIN
    UPDATE token_recuperacion
    SET usado = 1
    WHERE id_token_recuperacion = p_id;
END //

-- Deja inservibles los tokens anteriores de la cuenta (al pedir uno nuevo o al cambiar la contrasena)
CREATE PROCEDURE invalidar_tokens_recuperacion_de_cuenta (IN p_id_cuenta INT)
BEGIN
    UPDATE token_recuperacion
    SET usado = 1
    WHERE id_cuenta = p_id_cuenta
      AND usado = 0;
END //

DELIMITER ;
