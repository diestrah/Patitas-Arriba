package patitasarriba.dao.impl.horario;

import patitasarriba.modelo.horario.HorarioPersonal;

import java.sql.SQLException;
import java.util.List;

interface HorarioVeterinarioDAO {
    void insertarPorVeterinario(int idVeterinario, List<HorarioPersonal> horarios) throws SQLException;

    List<HorarioPersonal> listarPorVeterinario(int idVeterinario) throws SQLException;

    void eliminarPorVeterinario(int idVeterinario) throws SQLException;
}
