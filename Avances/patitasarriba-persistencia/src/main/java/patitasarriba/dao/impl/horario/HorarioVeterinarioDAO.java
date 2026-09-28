package patitasarriba.dao.impl.horario;

import patitasarriba.modelo.horario.HorarioPersonal;

import java.sql.SQLException;
import java.util.List;

interface HorarioVeterinarioDAO {
    void insert(int idVeterinario, HorarioPersonal horarioPersonal) throws SQLException;

    void update(int idVeterinario, HorarioPersonal horarioPersonal) throws SQLException;

    void delete(int idHorarioVeterinario) throws SQLException;

    HorarioPersonal findById(int idHorarioVeterinario) throws SQLException;

    List<HorarioPersonal> findAll() throws SQLException;

    List<HorarioPersonal> listarPorVeterinario(int idVeterinario) throws SQLException;

    void eliminarPorVeterinario(int idVeterinario) throws SQLException;
}
