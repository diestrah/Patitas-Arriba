package patitasarriba.dao.producto;

import patitasarriba.dao.DAO;
import patitasarriba.modelo.producto.Servicio;

import java.sql.SQLException;

public interface ServicioDAO extends DAO<Servicio, Integer> {
    Servicio findByName(String name) throws SQLException;
}
