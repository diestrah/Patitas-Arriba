use mydb;

DROP PROCEDURE IF EXISTS insertar_tratamiento;
DROP PROCEDURE IF EXISTS actualizar_tratamiento;
DROP PROCEDURE IF EXISTS eliminar_tratamiento;
DROP PROCEDURE IF EXISTS buscar_tratamiento_por_id;
DROP PROCEDURE IF EXISTS buscar_tratamiento_por_nombre;
DROP PROCEDURE IF EXISTS listar_tratamientos;


DELIMITER //

CREATE PROCEDURE insertar_tratamiento(
	IN p_nombre_procedimiento VARCHAR(45),
	IN p_descripcion VARCHAR(200),
	IN p_activo TINYINT(1),
	OUT p_id INT)
BEGIN
	INSERT INTO TRATAMIENTO (
		activo,
		nombre_procedimiento,
		descripcion)
	VALUES(
		p_activo,
		p_nombre_procedimiento,
		p_descripcion);
		
	SET p_id = LAST_INSERT_ID();
END //

CREATE PROCEDURE actualizar_tratamiento(
	IN p_nombre_procedimiento VARCHAR(45),
	IN p_descripcion VARCHAR(200),
	IN p_activo TINYINT(1),
	IN p_id INT)
BEGIN
	UPDATE TRATAMIENTO
	SET 
		nombre_procedimiento = p_nombre_procedimiento,
		descripcion = p_descripcion,
		activo = p_activo
	WHERE id_tratamiento = p_id;
END //

CREATE PROCEDURE eliminar_tratamiento(IN p_id INT)
BEGIN
	DELETE FROM TRATAMIENTO
	WHERE id_tratamiento = p_id;
END //

CREATE PROCEDURE buscar_tratamiento_por_id(IN p_id INT)
BEGIN
	SELECT * FROM TRATAMIENTO
	WHERE id_tratamiento = p_id;
END //


CREATE PROCEDURE buscar_tratamiento_por_nombre(IN p_nombre VARCHAR(45))
BEGIN
	SELECT * FROM TRATAMIENTO
	WHERE nombre_procedimiento = p_nombre;
END //

CREATE PROCEDURE listar_tratamientos()
BEGIN
	SELECT * FROM TRATAMIENTO;
END //