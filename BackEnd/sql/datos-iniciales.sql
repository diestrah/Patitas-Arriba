-- Datos iniciales: primer administrador del sistema (RF04)
-- La clinica lo designa al implementar el sistema; los demas usuarios los crea este administrador.
-- Ejecutar una sola vez, despues de patitasarriba-mysql.sql y de los procedures-*.sql.
-- IMPORTANTE: reemplazar los datos de ejemplo y cambiar la contrasena tras el primer ingreso.

use patitasarriba;

-- La contrasena se guarda como HashUtil.generarHashSHA256: SHA-256 en Base64 (RN-CU-04)
INSERT INTO cuenta (correo, password, fecha_creacion, nombre_usuario, activo)
VALUES ('admin@patitasarriba.pe', TO_BASE64(UNHEX(SHA2('Admin123', 256))), CURDATE(), 'admin', 1);

INSERT INTO administrador (id_cuenta, dni, nombres, apellido_paterno, apellido_materno, telefono, activo)
VALUES (LAST_INSERT_ID(), '00000001', 'Administrador', 'Principal', 'Sistema', '900000001', 1);
