package patitasarriba.dao.impl;

import patitasarriba.modelo.Registro;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class RegistroDAOImpl<T extends Registro> {
    // El id no se mapea aqui: cada tabla tiene su propio nombre de columna
    // (ID_CATEGORIA_ARTICULO, ID_ARTICULO, ID_CITA, ...), asi que lo pone
    // cada DAO concreto. Solo ACTIVO se llama igual en todas las tablas.
    protected T mapear(ResultSet rs, T registro) throws SQLException {
        registro.setActivo(rs.getBoolean("ACTIVO"));
        return registro;
    }
}
