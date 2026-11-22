package patitasarriba.reportes;

import conexion.DBManager;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

// Este .war tiene su propio pool de conexiones (DBManager). Al detener o volver a desplegar la
// aplicacion en GlassFish lo cierra, para no dejar conexiones abiertas en la base de datos
@WebListener
public class CierrePoolListener implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent evento) {
        DBManager.cerrarPool();
    }
}
