package patitasarriba.dao.impl.horario;

import patitasarriba.modelo.horario.Horario;
import java.sql.SQLException;
import java.util.List;

public interface HorarioAdministradorDAO {
    void insertarPorAdministrador(int idAdministrador, List<Horario> horarios) throws SQLException;
    List<Horario> listarPorAdministrador(int idAdministrador) throws SQLException;
    void eliminarPorAdministrador(int idAdministrador) throws SQLException;
}
