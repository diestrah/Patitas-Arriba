package patitasarriba.dao;

import patitasarriba.modelo.usuario.Cuenta;

import java.sql.SQLException;

public interface CuentaDAO extends DAO<Cuenta, Integer> {
    Cuenta findByNombreUsuario(String nombreUsuario) throws SQLException;
}
