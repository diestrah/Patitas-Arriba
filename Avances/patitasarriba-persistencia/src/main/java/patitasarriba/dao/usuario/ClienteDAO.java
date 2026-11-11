package patitasarriba.dao.usuario;

import patitasarriba.dao.DAO;
import patitasarriba.modelo.usuario.Cliente;

import java.sql.SQLException;

public interface ClienteDAO extends DAO<Cliente, Integer> {
    Cliente findByDni(String dni) throws SQLException;
    // RF01: persona duena de la cuenta (no filtra activo)
    Cliente findByCuenta(Integer idCuenta) throws SQLException;
}
