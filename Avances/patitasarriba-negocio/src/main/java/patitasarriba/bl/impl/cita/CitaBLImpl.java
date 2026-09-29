package patitasarriba.bl.impl.cita;

import patitasarriba.bl.BLException;
import patitasarriba.bl.CitaBL;
import patitasarriba.dao.CitaDAO;
import patitasarriba.dao.impl.cita.CitaDAOImpl;
import patitasarriba.dao.transacciones.TransactionsManager;
import patitasarriba.modelo.cita.Cita;
import patitasarriba.modelo.cita.EstadoCita;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class CitaBLImpl implements CitaBL {
    private final CitaDAO citaDAO = new CitaDAOImpl();

    @Override
    public List<Cita> findAll() throws BLException {
        try {
            return citaDAO.findAll();
        } catch (SQLException e) {
            throw new BLException("No se pudo listar las citas", e);
        }
    }

    @Override
    public Cita findById(Integer id) throws BLException {
        try {
            return citaDAO.findById(id);
        } catch (SQLException e) {
            throw new BLException("No se pudo recuperar la cita", e);
        }
    }

    @Override
    public void insert(Cita cita) throws BLException {
        validarFecha(cita);
        validarEstadoInicial(cita);

        TransactionsManager.iniciar();
        try {
            citaDAO.insert(cita);
            TransactionsManager.commit();
        } catch (SQLException e) {
            TransactionsManager.rollback();
            throw new BLException("No se pudo registrar la cita", e);
        }
    }

    @Override
    public void update(Cita cita) throws BLException {
        validarExiste(cita.getId());
        validarFecha(cita);

        TransactionsManager.iniciar();
        try {
            citaDAO.update(cita);
            TransactionsManager.commit();
        } catch (SQLException e) {
            TransactionsManager.rollback();
            throw new BLException("No se pudo actualizar la cita", e);
        }
    }

    @Override
    public void delete(Integer id) throws BLException {
        TransactionsManager.iniciar();
        try {
            citaDAO.delete(id);
            TransactionsManager.commit();
        } catch (SQLException e) {
            TransactionsManager.rollback();
            throw new BLException("No se pudo eliminar la cita", e);
        }
    }

    private void validarFecha(Cita cita) throws BLException {
        if (cita.getFechaHora().isBefore(LocalDateTime.now())) {
            throw new BLException("La fecha y hora de la cita no puede ser en el pasado");
        }
    }

    private void validarEstadoInicial(Cita cita) throws BLException {
        if (cita.getEstado() != EstadoCita.AGENDADA) {
            throw new BLException("Toda cita nueva debe registrarse con estado AGENDADA");
        }
    }

    private void validarExiste(int id) throws BLException {
        try {
            if (citaDAO.findById(id) == null) {
                throw new BLException("No existe una cita con id " + id);
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar la existencia de la cita", e);
        }
    }
}
