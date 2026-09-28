package patitasarriba.dao;

import patitasarriba.modelo.atencion.Tratamiento;

import java.sql.SQLException;

public interface TratamientoDAO extends DAO<Tratamiento, Integer> {
    Tratamiento findByName(String nombre) throws SQLException;
}
