package patitasarriba.bl.impl.atencion;

import patitasarriba.bl.AtencionBL;
import patitasarriba.bl.BLException;
import patitasarriba.dao.AtencionDAO;
import patitasarriba.dao.impl.atencion.AtencionDAOImpl;
import patitasarriba.dao.transacciones.TransactionsManager;
import patitasarriba.modelo.atencion.Atencion;

import java.sql.SQLException;
import java.util.List;

public class AtencionBLImpl implements AtencionBL {

    private final AtencionDAO atencionDAO = new AtencionDAOImpl();

    @Override
    public List<Atencion> findAll() throws BLException {
        try {
            return atencionDAO.findAll();
        } catch (SQLException e) {
            throw new BLException("No se pudo listar las atenciones medicas", e);
        }
    }

    @Override
    public Atencion findById(Integer id) throws BLException {
        try {
            return atencionDAO.findById(id);
        } catch (SQLException e) {
            throw new BLException("No se pudo recuperar la atencion medica", e);
        }
    }

    @Override
    public void insert(Atencion atencion) throws BLException {
        validar(atencion);

        TransactionsManager.iniciar();
        try {
            atencionDAO.insert(atencion);
            TransactionsManager.commit();
        } catch (SQLException e) {
            TransactionsManager.rollback();
            throw new BLException("No se pudo registrar la atencion medica", e);
        }
    }

    @Override
    public void update(Atencion atencion) throws BLException {
        validar(atencion);
        validarExiste(atencion.getId());

        TransactionsManager.iniciar();
        try {
            atencionDAO.update(atencion);
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
            atencionDAO.delete(id);
            TransactionsManager.commit();
        } catch (SQLException e) {
            TransactionsManager.rollback();
            throw new BLException("No se pudo eliminar la atencion medica", e);
        }
    }

    private void validar(Atencion atencion) throws BLException {
        if (atencion == null) {
            throw new BLException("La atencion medica no puede ser nula");
        }
        if (atencion.getDetalleCita() == null) {
            throw new BLException("La atencion medica debe estar asociada a un detalle de cita");
        }
        if (atencion.getFechaHora() == null) {
            throw new BLException("La atencion medica debe tener fecha y hora");
        }
    }

    private void validarExiste(int id) throws BLException {
        try {
            if (atencionDAO.findById(id) == null) {
                throw new BLException("No existe una atencion medica con id " + id);
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar la existencia de la atencion medica", e);
        }
    }
}
