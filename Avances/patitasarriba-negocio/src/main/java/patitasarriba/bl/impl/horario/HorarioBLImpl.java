package patitasarriba.bl.impl.horario;

import patitasarriba.bl.BLException;
import patitasarriba.bl.HorarioBL;
import patitasarriba.dao.AdministradorDAO;
import patitasarriba.dao.VeterinarioDAO;
import patitasarriba.dao.impl.usuario.AdministradorDAOImpl;
import patitasarriba.dao.impl.usuario.VeterinarioDAOImpl;
import patitasarriba.modelo.horario.Horario;
import patitasarriba.dao.HorarioDAO;
import patitasarriba.dao.impl.horario.HorarioDAOImpl;
import patitasarriba.modelo.usuario.Administrador;
import patitasarriba.modelo.usuario.Veterinario;

import java.sql.SQLException;
import java.util.List;

public class HorarioBLImpl implements HorarioBL {
    private final HorarioDAO horarioDAO = new HorarioDAOImpl();
    private final VeterinarioDAO veterinarioDAO = new VeterinarioDAOImpl();
    private final AdministradorDAO administradorDAO = new AdministradorDAOImpl();
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
        validarSinAsignaciones(id);
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

    private void validarSinAsignaciones(int idHorario) throws BLException {
        try {
            for (Veterinario v : veterinarioDAO.findAll()) {
                for (Horario h : v.getHorarios()) {
                    if (h.getId() == idHorario) {
                        throw new BLException("No se puede eliminar el horario: está asignado al veterinario "
                                + v.getNombres() + " " + v.getApellidoPaterno());
                    }
                }
            }
            for (Administrador a : administradorDAO.findAll()) {
                for (Horario h : a.getHorarios()) {
                    if (h.getId() == idHorario) {
                        throw new BLException("No se puede eliminar el horario: está asignado al administrador "
                                + a.getNombres() + " " + a.getApellidoPaterno());
                    }
                }
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar las asignaciones del horario", e);
        }
    }
}
