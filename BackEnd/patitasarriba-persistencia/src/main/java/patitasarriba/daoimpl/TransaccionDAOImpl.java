package patitasarriba.daoimpl;

import patitasarriba.dao.TransaccionDAO;
import patitasarriba.modelo.Registro;

import java.sql.Connection;
import java.sql.SQLException;

// Ejecuta los comandos de BaseDAOImpl sobre la conexion recibida. Solo cierra los comandos que prepara:
// la conexion (y su commit o rollback) es del llamador.
public abstract class TransaccionDAOImpl<T extends Registro> extends BaseDAOImpl<T> implements TransaccionDAO<T, Integer> {

    @Override
    public void crear(T modelo, Connection conn) throws SQLException {
        ejecutarComandoInsertar(conn, modelo);
    }

    @Override
    public void actualizar(T modelo, Connection conn) throws SQLException {
        ejecutarComandoActualizar(conn, modelo);
    }

    @Override
    public void eliminar(Integer id, Connection conn) throws SQLException {
        ejecutarComandoEliminar(conn, id);
    }
}
