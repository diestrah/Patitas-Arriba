package patitasarriba.dao;

import patitasarriba.modelo.usuario.Administrador;

import java.sql.SQLException;

public interface AdministradorDAO extends DAO<Administrador, Integer> {
    Administrador findByDni(String dni) throws SQLException;
    // RF01: persona duena de la cuenta (no filtra activo)
    Administrador findByCuenta(Integer idCuenta) throws SQLException;
}
