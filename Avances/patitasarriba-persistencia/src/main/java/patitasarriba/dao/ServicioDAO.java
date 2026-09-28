package patitasarriba.dao;

import patitasarriba.modelo.producto.Servicio;

import java.sql.SQLException;

public interface ServicioDAO extends DAO<Servicio, Integer> {
    Servicio findByName(String name) throws SQLException;
}
