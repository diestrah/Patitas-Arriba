package patitasarriba.dao;

import java.sql.SQLException;

public interface TratamientoDAO extends DAO<Tratamiento, Integer> {
    Tratamiento findByName(String nombre) throws SQLException;
}
