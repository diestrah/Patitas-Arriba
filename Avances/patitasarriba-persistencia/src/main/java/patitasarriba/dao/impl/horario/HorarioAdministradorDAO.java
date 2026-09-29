package patitasarriba.dao.impl.horario;

import patitasarriba.modelo.horario.HorarioPersonal;

import java.sql.SQLException;
import java.util.List;

interface HorarioAdministradorDAO {
    void insertarPorAdministrador(int idAdministrador, List<HorarioPersonal> horarios) throws SQLException;

    List<HorarioPersonal> listarPorAdministrador(int idAdministrador) throws SQLException;

    void eliminarPorAdministrador(int idAdministrador) throws SQLException;
}
