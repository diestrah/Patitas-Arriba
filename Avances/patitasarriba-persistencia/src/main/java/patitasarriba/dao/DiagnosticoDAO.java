package patitasarriba.dao;

import patitasarriba.modelo.atencion.Diagnostico;

import java.sql.SQLException;

public interface DiagnosticoDAO extends DAO<Diagnostico, Integer> {
    Diagnostico findByNombreEnfermedad(String nombreEnfermedad) throws SQLException;
}
