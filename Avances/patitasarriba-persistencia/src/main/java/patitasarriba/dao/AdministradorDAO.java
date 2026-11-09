package patitasarriba.dao;

import patitasarriba.modelo.usuario.Administrador;

import java.sql.SQLException;

public interface AdministradorDAO extends DAO<Administrador, Integer> {
    Administrador findByDni(String dni) throws SQLException;
    // No filtra activo: detecta tambien registros dados de baja
    Administrador findByTelefono(String telefono) throws SQLException;
    // No filtra activo: detecta tambien registros dados de baja
    Administrador findByCuenta(Integer idCuenta) throws SQLException;
    // Incluye registros dados de baja: para cargar relaciones del historial
    Administrador findByIdHistorico(Integer id) throws SQLException;
}
