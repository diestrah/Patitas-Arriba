package patitasarriba.dao;

import patitasarriba.modelo.usuario.Cliente;

import java.sql.SQLException;

public interface ClienteDAO extends DAO<Cliente, Integer> {
    Cliente findByDni(String dni) throws SQLException;
    // No filtra activo: detecta tambien registros dados de baja
    Cliente findByTelefono(String telefono) throws SQLException;
    // No filtra activo: detecta tambien registros dados de baja
    Cliente findByCuenta(Integer idCuenta) throws SQLException;
}
