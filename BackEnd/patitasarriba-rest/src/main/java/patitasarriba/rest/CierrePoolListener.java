package patitasarriba.rest;

import conexion.DBManager;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

// Al detener o volver a desplegar la aplicacion en GlassFish, cierra el pool de conexiones
// para no dejar conexiones abiertas en la base de datos
@WebListener
public class CierrePoolListener implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent evento) {
        DBManager.cerrarPool();
    }
}
