package patitasarriba.daoimpl;

import conexion.DBManager;
import patitasarriba.dao.ComandoDAO;
import patitasarriba.dao.transacciones.TransactionsManager;

import java.sql.Connection;
import java.sql.SQLException;

/** Gestiona las conexiones propias; nunca cierra la transacción del llamador. */
public final class ConexionDAO {
    // Conexion que este hilo ya esta usando fuera de una transaccion. Las consultas anidadas
    // (mapear una cita busca su mascota, la mascota a su cliente, el cliente a su cuenta...)
    // la reutilizan en vez de pedir otra al pool
    private static final ThreadLocal<Connection> EN_USO = new ThreadLocal<>();

    private ConexionDAO() { }

    public static <R> R ejecutarComando(ComandoDAO<R> comando) throws SQLException {
        if (TransactionsManager.activa()) {
            return comando.ejecutar(TransactionsManager.getConnection());
        }
        Connection actual = EN_USO.get();
        if (actual != null) {
            return comando.ejecutar(actual);
        }
        try (Connection conn = DBManager.getInstance().getConnection()) {
            EN_USO.set(conn);
            CacheConsulta.abrir();
            try {
                return comando.ejecutar(conn);
            } finally {
                CacheConsulta.cerrar();
                EN_USO.remove();
            }
        }
    }
}
