package patitasarriba.dao;

import java.sql.Connection;
import java.sql.SQLException;

public interface ComandoDAO<R>{
    R ejecutar(Connection conn) throws SQLException;
}
