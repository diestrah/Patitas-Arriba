package patitasarriba.dao.impl.horario;

import patitasarriba.modelo.horario.HorarioPersonal;

import java.sql.SQLException;
import java.util.List;

interface HorarioAdministradorDAO {
    void insert(int idAdministrador, HorarioPersonal horarioPersonal) throws SQLException;

    void update(int idAdministrador, HorarioPersonal horarioPersonal) throws SQLException;

    void delete(int idHorarioAdministrador) throws SQLException;

    HorarioPersonal findById(int idHorarioAdministrador) throws SQLException;

    List<HorarioPersonal> findAll() throws SQLException;

    List<HorarioPersonal> listarPorAdministrador(int idAdministrador) throws SQLException;

    void eliminarPorAdministrador(int idAdministrador) throws SQLException;
}
