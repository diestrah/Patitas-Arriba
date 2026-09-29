package patitasarriba.bl.impl.usuario;

import patitasarriba.bl.AdministradorBL;
import patitasarriba.bl.BLException;
import patitasarriba.dao.AdministradorDAO;
import patitasarriba.dao.impl.usuario.AdministradorDAOImpl;
import patitasarriba.dao.transacciones.TransactionsManager;
import patitasarriba.modelo.usuario.Administrador;

import java.sql.SQLException;
import java.util.List;

public class AdministradorBLImpl implements AdministradorBL {
    private final AdministradorDAO administradorDAO = new AdministradorDAOImpl();

    @Override
    public List<Administrador> findAll() throws BLException {
        try {
            return administradorDAO.findAll();
        } catch (SQLException e) {
            throw new BLException("No se pudo listar los administradores", e);
        }
    }

    @Override
    public Administrador findById(Integer id) throws BLException {
        try {
            return administradorDAO.findById(id);
        } catch (SQLException e) {
            throw new BLException("No se pudo recuperar el administrador", e);
        }
    }

    @Override
    public Administrador findByDni(String dni) throws BLException {
        try {
            return administradorDAO.findByDni(dni);
        } catch (SQLException e) {
            throw new BLException("Error al buscar el administrador por DNI", e);
        }
    }

    @Override
    public void insert(Administrador admin) throws BLException {
        validar(admin);

        TransactionsManager.iniciar();
        try {
            administradorDAO.insert(admin);
            TransactionsManager.commit();
        } catch (SQLException e) {
            TransactionsManager.rollback();
            throw new BLException("No se pudo registrar el administrador", e);
        }
    }

    @Override
    public void update(Administrador admin) throws BLException {
        validar(admin);

        TransactionsManager.iniciar();
        try {
            administradorDAO.update(admin);
            TransactionsManager.commit();
        } catch (SQLException e) {
            TransactionsManager.rollback();
            throw new BLException("No se pudo actualizar el administrador", e);
        }
    }

    @Override
    public void delete(Integer id) throws BLException {
        TransactionsManager.iniciar();
        try {
            administradorDAO.delete(id);
            TransactionsManager.commit();
        } catch (SQLException e) {
            TransactionsManager.rollback();
            throw new BLException("No se pudo eliminar el administrador", e);
        }
    }

    private void validar(Administrador admin) throws BLException {
        if (admin == null) {
            throw new BLException("El administrador no puede ser nulo");
        }
        if (admin.getDni() == null || !admin.getDni().matches("\\d{8}")) {
            throw new BLException("El DNI debe contener exactamente 8 dígitos numéricos");
        }
        if (admin.getNombres() == null || admin.getNombres().trim().isEmpty()) {
            throw new BLException("El nombre del administrador es obligatorio");
        }
    }
}
