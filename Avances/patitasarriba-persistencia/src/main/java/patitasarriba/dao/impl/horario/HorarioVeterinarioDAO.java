package patitasarriba.dao.impl.horario;

import patitasarriba.modelo.horario.Horario;
import java.sql.SQLException;
import java.util.List;

public interface HorarioVeterinarioDAO {
    void insertarPorVeterinario(int idVeterinario, List<Horario> horarios) throws SQLException;
    List<Horario> listarPorVeterinario(int idVeterinario) throws SQLException;
    void eliminarPorVeterinario(int idVeterinario) throws SQLException;
}
