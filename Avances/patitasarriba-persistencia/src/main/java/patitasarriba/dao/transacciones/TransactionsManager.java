package patitasarriba.dao.transacciones;

import conexion.DBManager;

import java.sql.Connection;
import java.sql.SQLException;

// Coordina transacciones que escriben en mas de una tabla (maestro-detalle).
// Regla: se inicia/comitea/revierte desde la capa de negocio (BL), nunca desde un DAO.
// El DAO solo pide prestada la conexion activa con getConnection().
public class TransactionsManager {
    private static final ThreadLocal<Connection> conexionActual = new ThreadLocal<>();

    private TransactionsManager() {
    }

    public static void iniciar() throws SQLException {
        Connection conn = DBManager.getInstance().getConnection();
        conn.setAutoCommit(false);
        conexionActual.set(conn);
    }

    public static Connection getConnection() throws SQLException {
        if (!activa()) {
            throw new IllegalStateException("No hay una transaccion activa");
        }
        return conexionActual.get();
    }

    public static void commit() throws SQLException {
        Connection conn = getConnection();
        conn.commit();
        cerrar(conn);
    }

    public static void rollback() throws SQLException {
        Connection conn = getConnection();
        conn.rollback();
        cerrar(conn);
    }

    public static boolean activa() {
        return conexionActual.get() != null;
    }

    private static void cerrar(Connection conn) throws SQLException {
        conn.setAutoCommit(true);
        conn.close();
        conexionActual.remove();
    }
}
