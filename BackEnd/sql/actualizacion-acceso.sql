-- Actualizacion para una base YA INSTALADA: agrega el registro de clientes con verificacion de correo
-- y la recuperacion de contrasena. Solo AGREGA (no modifica tablas ni procedimientos existentes).
-- Se genera a partir del DDL de patitasarriba-mysql.sql y de procedures-registro_pendiente.sql /
-- procedures-token_recuperacion.sql: si cambian, hay que volver a generarlo.

USE patitasarriba;

-- -----------------------------------------------------
-- Table `patitasarriba`.`registro_pendiente`
-- Datos de un cliente que se registro y aun no verifica su correo (tabla de paso)
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `patitasarriba`.`registro_pendiente` (
  `id_registro_pendiente` INT NOT NULL AUTO_INCREMENT,
  `dni` VARCHAR(8) NOT NULL,
  `nombres` VARCHAR(50) NOT NULL,
  `apellido_paterno` VARCHAR(45) NOT NULL,
  `apellido_materno` VARCHAR(45) NOT NULL,
  `telefono` VARCHAR(9) NOT NULL,
  `correo` VARCHAR(100) NOT NULL,
  `nombre_usuario` VARCHAR(45) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `token_hash` VARCHAR(64) NOT NULL,
  `fecha_creacion` DATETIME NOT NULL,
  `fecha_vencimiento` DATETIME NOT NULL,
  PRIMARY KEY (`id_registro_pendiente`),
  UNIQUE INDEX `token_hash_UNIQUE` (`token_hash` ASC) VISIBLE,
  INDEX `registro_pendiente_correo_idx` (`correo` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `patitasarriba`.`token_recuperacion`
-- Codigo de un solo uso con vencimiento para restablecer la contrasena de una cuenta
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `patitasarriba`.`token_recuperacion` (
  `id_token_recuperacion` INT NOT NULL AUTO_INCREMENT,
  `id_cuenta` INT NOT NULL,
  `token_hash` VARCHAR(64) NOT NULL,
  `fecha_creacion` DATETIME NOT NULL,
  `fecha_vencimiento` DATETIME NOT NULL,
  `usado` TINYINT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id_token_recuperacion`),
  UNIQUE INDEX `token_hash_UNIQUE` (`token_hash` ASC) VISIBLE,
  INDEX `fk_token_recuperacion_cuenta1_idx` (`id_cuenta` ASC) VISIBLE,
  CONSTRAINT `fk_token_recuperacion_cuenta1`
    FOREIGN KEY (`id_cuenta`)
    REFERENCES `patitasarriba`.`cuenta` (`id_cuenta`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


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
