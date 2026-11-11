package patitasarriba.daoimpl;

import conexion.DBManager;
import patitasarriba.dao.ComandoDAO;
import patitasarriba.dao.transacciones.TransactionsManager;

import java.sql.Connection;
import java.sql.SQLException;

/** Gestiona las conexiones propias; nunca cierra la transacción del llamador. */
public final class ConexionDAO {
    private ConexionDAO() { }

    public static <R> R ejecutarComando(ComandoDAO<R> comando) throws SQLException {
        if (TransactionsManager.activa()) {
            return comando.ejecutar(TransactionsManager.getConnection());
        }
        try (Connection conn = DBManager.getInstance().getConnection()) {
            return comando.ejecutar(conn);
        }
    }
}
