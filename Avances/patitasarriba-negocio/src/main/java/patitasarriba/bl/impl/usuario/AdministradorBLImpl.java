package patitasarriba.bl.impl.usuario;

import patitasarriba.bl.AdministradorBL;
import patitasarriba.bl.BLException;
import patitasarriba.dao.AdministradorDAO;
import patitasarriba.dao.CuentaDAO;
import patitasarriba.dao.impl.usuario.AdministradorDAOImpl;
import patitasarriba.dao.impl.usuario.CuentaDAOImpl;
import patitasarriba.dao.transacciones.TransactionsManager;
import patitasarriba.modelo.usuario.Administrador;

import java.sql.SQLException;
import java.util.List;

public class AdministradorBLImpl implements AdministradorBL {
    private final AdministradorDAO administradorDAO = new AdministradorDAOImpl();
    private final CuentaDAO cuentaDAO = new CuentaDAOImpl();

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
        validarCuentaActiva (admin);
        TransactionsManager.iniciar();
        try {
            administradorDAO.insert(admin);
            TransactionsManager.commit();
        } catch (SQLException | RuntimeException e) {
            if (TransactionsManager.activa()) TransactionsManager.rollback();
            throw new BLException("No se pudo registrar el administrador", e);
        }
    }

    @Override
    public void update(Administrador admin) throws BLException {
        validar(admin);
        validarCuentaActiva(admin);
        TransactionsManager.iniciar();
        try {
            administradorDAO.update(admin);
            TransactionsManager.commit();
        } catch (SQLException | RuntimeException e) {
            if (TransactionsManager.activa()) TransactionsManager.rollback();
            throw new BLException("No se pudo registrar el administrador", e);
        }
    }

    @Override
    public void delete(Integer id) throws BLException {
        TransactionsManager.iniciar();
        try {
            //para que no nos quedemos en 0
            if (administradorDAO.findAll().size() <= 1) {
                throw new BLException("No se puede eliminar al único administrador activo");
            }
            administradorDAO.delete(id);
            TransactionsManager.commit();
        } catch (SQLException | RuntimeException e) {
            if (TransactionsManager.activa()) TransactionsManager.rollback();
            throw new BLException("No se pudo registrar el administrador", e);
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

    private void validarCuentaActiva(Administrador admin) throws BLException {
        if (admin.getCuenta() == null) throw new BLException("El administrador debe tener una cuenta");
        try {
            if (cuentaDAO.findById(admin.getCuenta().getId()) == null) {
                throw new BLException("La cuenta no existe o está dada de baja");
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar la cuenta del administrador", e);
        }
        }
}
