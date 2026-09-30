USE mydb;

DROP PROCEDURE IF EXISTS insertar_diagnostico;
DROP PROCEDURE IF EXISTS actualizar_diagnostico;
DROP PROCEDURE IF EXISTS eliminar_diagnostico;
DROP PROCEDURE IF EXISTS buscar_diagnostico_por_id;
DROP PROCEDURE IF EXISTS buscar_diagnostico_por_nombre_enfermedad;
DROP PROCEDURE IF EXISTS listar_diagnosticos;


CREATE PROCEDURE insertar_diagnostico (
    IN p_nombre_enfermedad VARCHAR(45),
    IN p_descripcion VARCHAR(45),
    IN p_estado TINYINT(1),
    OUT p_id INT
)
BEGIN
    INSERT INTO DIAGNOSTICO (
        nombre_enfermedad,
        descripcion,
        activo
    )
    VALUES (
        p_nombre_enfermedad,
        p_descripcion,
        p_estado
    );

    SET p_id = LAST_INSERT_ID();
END;

CREATE PROCEDURE actualizar_diagnostico (
    IN p_id INT,
    IN p_nombre_enfermedad VARCHAR(45),
    IN p_descripcion VARCHAR(45),
    IN p_estado TINYINT(1)
)
BEGIN
    UPDATE DIAGNOSTICO
    SET
        nombre_enfermedad = p_nombre_enfermedad,
        descripcion = p_descripcion,
        activo = p_estado
    WHERE id_diagnostico = p_id;
END;

CREATE PROCEDURE eliminar_diagnostico (IN p_id INT)
BEGIN
    DELETE FROM DIAGNOSTICO
    WHERE id_diagnostico = p_id;
END;

CREATE PROCEDURE buscar_diagnostico_por_id (IN p_id INT)
BEGIN
    SELECT *
    FROM DIAGNOSTICO
    WHERE id_diagnostico = p_id;
END;

CREATE PROCEDURE buscar_diagnostico_por_nombre_enfermedad (IN p_nombre_enfermedad VARCHAR(45))
BEGIN
    SELECT *
    FROM DIAGNOSTICO
    WHERE nombre_enfermedad = p_nombre_enfermedad;
END;

CREATE PROCEDURE listar_diagnosticos ()
BEGIN
    SELECT *
    FROM DIAGNOSTICO;
END;
