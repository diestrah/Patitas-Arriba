package patitasarriba.dao;

import patitasarriba.modelo.usuario.Cliente;

import java.sql.SQLException;

public interface ClienteDAO extends DAO<Cliente, Integer> {
    Cliente findByDni(String dni) throws SQLException;
}
