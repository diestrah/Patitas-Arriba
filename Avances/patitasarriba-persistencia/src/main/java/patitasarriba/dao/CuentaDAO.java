package patitasarriba.dao;

import patitasarriba.modelo.usuario.Cuenta;

import java.sql.SQLException;

public interface CuentaDAO extends DAO<Cuenta, Integer> {
    Cuenta findByNombreUsuario(String nombreUsuario) throws SQLException;
    // No filtra activo: detecta tambien registros dados de baja
    Cuenta findByCorreo(String correo) throws SQLException;
    // Incluye registros dados de baja: para cargar relaciones del historial
    Cuenta findByIdHistorico(Integer id) throws SQLException;
}
