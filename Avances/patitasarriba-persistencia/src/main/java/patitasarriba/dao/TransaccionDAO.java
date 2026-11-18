package patitasarriba.dao;

import java.sql.Connection;
import java.sql.SQLException;

// Variante de escritura con conexion explicita: ejecuta sobre la conexion recibida y no la abre, cierra ni confirma.
// Quien la pasa (por ejemplo el BL con TransactionsManager) es el dueno de la transaccion.
public interface TransaccionDAO<T, ID> extends DAO<T, ID> {
    void crear(T modelo, Connection conn) throws SQLException;
    void actualizar(T modelo, Connection conn) throws SQLException;
    void eliminar(ID id, Connection conn) throws SQLException;
}
