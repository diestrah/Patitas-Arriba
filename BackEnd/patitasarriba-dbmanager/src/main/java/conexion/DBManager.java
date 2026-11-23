package conexion;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

// Entrega las conexiones a MySQL desde un pool (HikariCP): abrir una conexion nueva a la base
// en AWS cuesta varios viajes de red, asi que se reutilizan. Cerrar la conexion (close) no la
// corta: la devuelve al pool. El pool se crea la primera vez que se pide una conexion.
public class DBManager {
    protected String servidor;
    protected int puerto;
    protected String esquema;
    protected String usuario;
    protected String password;

    private static DBManager instance;

    private HikariDataSource pool;

    protected DBManager() {
    }

    protected DBManager(String servidor, int puerto, String esquema, String usuario, String password) {
        this.servidor = servidor;
        this.puerto = puerto;
        this.esquema = esquema;
        this.usuario = usuario;
        this.password = password;
    }

    public synchronized static DBManager getInstance() {
        if (instance == null) {
            ResourceBundle rb = ResourceBundle.getBundle("db");

            instance = new DBManager(
                    rb.getString("servidor"),
                    Integer.parseInt(rb.getString("puerto")),
                    rb.getString("esquema"),
                    rb.getString("usuario"),
                    rb.getString("password")
            );
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return pool().getConnection();
    }

    private synchronized HikariDataSource pool() {
        if (pool == null || pool.isClosed()) {
            HikariConfig config = new HikariConfig();
            config.setPoolName("patitasarriba");
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            config.setJdbcUrl("jdbc:mysql://" + servidor + ":" + puerto + "/" + esquema
                    + "?useSSL=false&allowPublicKeyRetrieval=true");
            config.setUsername(usuario);
            config.setPassword(password);
            // Cada consulta usa una sola conexion (las anidadas la comparten), asi que pocas bastan.
            // Pocas tambien porque la base de AWS la comparten todos los del grupo
            config.setMaximumPoolSize(8);
            config.setMinimumIdle(1);
            config.setConnectionTimeout(30_000);   // espera maxima por una conexion libre (ms)
            config.setMaxLifetime(600_000);        // renueva cada conexion a los 10 min
            config.setKeepaliveTime(120_000);      // mantiene viva la conexion inactiva
            // Los DAO usan {call ...} con parametros por nombre: sin esta cache, MySQL Connector/J
            // consulta la definicion del procedure en cada llamada (un viaje de red extra)
            config.addDataSourceProperty("cacheCallableStmts", "true");
            config.addDataSourceProperty("callableStmtCacheSize", "250");
            pool = new HikariDataSource(config);
        }
        return pool;
    }

    // Cierra el pool y sus conexiones (al detener o volver a desplegar la aplicacion)
    public static void cerrarPool() {
        DBManager actual;
        synchronized (DBManager.class) {
            actual = instance;
        }
        if (actual != null) actual.cerrar();
    }

    private synchronized void cerrar() {
        if (pool != null) {
            pool.close();
            pool = null;
        }
    }
}
