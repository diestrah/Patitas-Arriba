package patitasarriba.bl.impl.atencion;

import patitasarriba.bl.AtencionMedicaBL;
import patitasarriba.bl.BLException;
import patitasarriba.dao.AtencionMedicaDAO;
import patitasarriba.dao.impl.atencion.AtencionMedicaDAOImpl;
import patitasarriba.dao.transacciones.TransactionsManager;
import patitasarriba.modelo.atencion.AtencionMedica;

import java.sql.SQLException;
import java.util.List;

public class AtencionMedicaBLImpl implements AtencionMedicaBL {

    private final AtencionMedicaDAO atencionMedicaDAO = new AtencionMedicaDAOImpl();

    @Override
    public List<AtencionMedica> findAll() throws BLException {
        try {
            return atencionMedicaDAO.findAll();
        } catch (SQLException e) {
            throw new BLException("No se pudo listar las atenciones medicas", e);
        }
    }

    @Override
    public AtencionMedica findById(Integer id) throws BLException {
        try {
            return atencionMedicaDAO.findById(id);
        } catch (SQLException e) {
            throw new BLException("No se pudo recuperar la atencion medica", e);
        }
    }

    @Override
    public void insert(AtencionMedica atencionMedica) throws BLException {
        validar(atencionMedica);

        TransactionsManager.iniciar();
        try {
            atencionMedicaDAO.insert(atencionMedica);
            TransactionsManager.commit();
        } catch (SQLException e) {
            TransactionsManager.rollback();
            throw new BLException("No se pudo registrar la atencion medica", e);
        }
    }

    @Override
    public void update(AtencionMedica atencionMedica) throws BLException {
        validar(atencionMedica);
        validarExiste(atencionMedica.getId());

        TransactionsManager.iniciar();
        try {
            atencionMedicaDAO.update(atencionMedica);
            TransactionsManager.commit();
        } catch (SQLException e) {
            TransactionsManager.rollback();
            throw new BLException("No se pudo actualizar la atencion medica", e);
        }
    }

    @Override
    public void delete(Integer id) throws BLException {
        TransactionsManager.iniciar();
        try {
            atencionMedicaDAO.delete(id);
            TransactionsManager.commit();
        } catch (SQLException e) {
            TransactionsManager.rollback();
            throw new BLException("No se pudo eliminar la atencion medica", e);
        }
    }

    private void validar(AtencionMedica atencionMedica) throws BLException {
        if (atencionMedica == null) {
            throw new BLException("La atencion medica no puede ser nula");
        }
        if (atencionMedica.getMascota() == null) {
            throw new BLException("La atencion medica debe tener una mascota asociada");
        }
        if (atencionMedica.getCita() == null) {
            throw new BLException("La atencion medica debe tener una cita asociada");
        }
    }

    private void validarExiste(int id) throws BLException {
        try {
            if (atencionMedicaDAO.findById(id) == null) {
                throw new BLException("No existe una atencion medica con id " + id);
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar la existencia de la atencion medica", e);
        }
    }
}
