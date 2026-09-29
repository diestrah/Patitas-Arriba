package patitasarriba.bl.impl.horario;

import patitasarriba.bl.BLException;
import patitasarriba.bl.HorarioBL;
import patitasarriba.modelo.horario.Horario;
import patitasarriba.dao.HorarioDAO;
import patitasarriba.dao.impl.horario.HorarioDAOImpl;

import java.util.List;

public class HorarioBLImpl implements HorarioBL {
    private final HorarioDAO horarioDAO = new HorarioDAOImpl();

    @Override
    public List<Horario> findAll() throws BLException {
        try{
            return horarioDAO.findAll();
        } catch (Exception e) {
            throw new BLException("No se pudo listar los horarios", e);
        }
    }

    @Override
    public Horario findById(Integer id) throws BLException {
        try{
            return horarioDAO.findById(id);
        } catch (Exception e) {
            throw new BLException("No se pudo encontrar el horario con id: " + id, e);
        }
    }

    @Override
    public void insert(Horario horario) throws BLException {
        // validaciones
        try{
            horarioDAO.insert(horario);
        } catch (Exception e) {
            throw new BLException("No se pudo insertar el horario", e);
        }
    }

    @Override
    public void update(Horario horario) throws BLException {
        // Validar que el horario exista antes de actualizarlo
        try{
            horarioDAO.update(horario);
        } catch (Exception e) {
            throw new BLException("No se pudo actualizar el horario", e);
        }
    }

    @Override
    public void delete(Integer id) throws BLException {
         // validaciones
        try{
            horarioDAO.delete(id);
        } catch (Exception e) {
            throw new BLException("No se pudo eliminar el horario con id: " + id, e);
        }
    }
}
