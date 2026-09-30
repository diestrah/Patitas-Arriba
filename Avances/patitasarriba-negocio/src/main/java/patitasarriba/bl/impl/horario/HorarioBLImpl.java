package patitasarriba.bl.impl.horario;

import patitasarriba.bl.BLException;
import patitasarriba.bl.HorarioBL;
import patitasarriba.modelo.horario.Horario;
import patitasarriba.dao.HorarioDAO;
import patitasarriba.dao.impl.horario.HorarioDAOImpl;

import java.sql.SQLException;
import java.util.List;

public class HorarioBLImpl implements HorarioBL {
    private final HorarioDAO horarioDAO = new HorarioDAOImpl();

    @Override
    public List<Horario> findAll() throws BLException {
        try{
            return horarioDAO.findAll();
        } catch (SQLException e) {
            throw new BLException("No se pudo listar los horarios", e);
        }
    }

    @Override
    public Horario findById(Integer id) throws BLException {
        try{
            return horarioDAO.findById(id);
        } catch (SQLException e) {
            throw new BLException("No se pudo encontrar el horario con id: " + id, e);
        }
    }

    @Override
    public void insert(Horario horario) throws BLException {
        validar(horario);
        try{
            horarioDAO.insert(horario);
        } catch (SQLException e) {
            throw new BLException("No se pudo insertar el horario", e);
        }
    }

    @Override
    public void update(Horario horario) throws BLException {
        validar(horario);
        try{
            horarioDAO.update(horario);
        } catch (SQLException e) {
            throw new BLException("No se pudo actualizar el horario", e);
        }
    }

    @Override
    public void delete(Integer id) throws BLException {
        try{
            horarioDAO.delete(id);
        } catch (SQLException e) {
            throw new BLException("No se pudo eliminar el horario con id: " + id, e);
        }
    }

    private void validar(Horario horario) throws BLException {
        if (horario == null) {
            throw new BLException("El horario no puede ser nulo");
        }
        if (horario.getDiaSemana() == null
                || horario.getHoraInicio() == null
                || horario.getHoraFin() == null) {
            throw new BLException("El horario debe tener día y horas definidas");
        }
        if (!horario.getHoraInicio().isBefore(horario.getHoraFin())) {
            throw new BLException("La hora de inicio debe ser anterior a la hora de fin");
        }
    }
}
