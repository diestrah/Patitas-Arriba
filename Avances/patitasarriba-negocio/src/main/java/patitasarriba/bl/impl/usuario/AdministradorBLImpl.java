package patitasarriba.bl.impl.usuario;

import patitasarriba.bl.util.Activos;
import patitasarriba.bl.AdministradorBL;
import patitasarriba.bl.BLException;
import patitasarriba.bl.NoEncontradoException;
import patitasarriba.bl.util.ValidadorPersona;
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
            Administrador administrador = administradorDAO.findById(id);
            if(administrador==null){
                throw new NoEncontradoException("No se pudo recuperar el administrador con id: "+id);
            }
            return administrador;
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
        Activos.marcar(admin);
        validar(admin);
        validarCuentaActiva (admin);
        validarUnicos(admin);
        TransactionsManager.iniciar();
        try {
            administradorDAO.insert(admin);
            TransactionsManager.commit();
        } catch (SQLException | RuntimeException e) {
            if (TransactionsManager.activa()) TransactionsManager.rollback();
            throw new BLException("No se pudo insertar el administrador", e);
        }
    }

    @Override
    public void update(Administrador admin) throws BLException {
        // RN-GE-06: solo se modifica un registro que existe y esta activo
        if (admin != null) findById(admin.getId());
        Activos.marcar(admin);
        validar(admin);
        validarCuentaActiva(admin);
        validarUnicos(admin);
        TransactionsManager.iniciar();
        try {
            administradorDAO.update(admin);
            TransactionsManager.commit();
        } catch (SQLException | RuntimeException e) {
            if (TransactionsManager.activa()) TransactionsManager.rollback();
            throw new BLException("No se pudo actualizar el administrador", e);
        }
    }

    @Override
    public void delete(Integer id) throws BLException {
        // RN-AD-02: se valida antes de abrir la transaccion. Dentro del try, la BLException
        // no la captura el catch y la transaccion quedaba abierta sin rollback
        validarNoEsUltimoActivo(id);
        Administrador admin = findById(id);
        TransactionsManager.iniciar();
        try {
            administradorDAO.delete(id);
            // RN-PE-06: la cuenta se da de baja con la persona, asi ya no puede iniciar sesion
            cuentaDAO.delete(admin.getCuenta().getId());
            TransactionsManager.commit();
        } catch (SQLException | RuntimeException e) {
            if (TransactionsManager.activa()) TransactionsManager.rollback();
            throw new BLException("No se pudo eliminar el administrador", e);
        }
    }

    // Para que no nos quedemos en 0 administradores activos
    private void validarNoEsUltimoActivo(Integer id) throws BLException {
        if (id == null) throw new BLException("El id del administrador no puede ser nulo");
        try {
            List<Administrador> activos = administradorDAO.findAll();
            boolean esActivo = activos.stream().anyMatch(a -> a.getId() == id);
            if (!esActivo) {
                throw new NoEncontradoException("No existe un administrador activo con id " + id);
            }
            if (activos.size() <= 1) {
                throw new BLException("No se puede eliminar al único administrador activo");
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar los administradores activos", e);
        }
    }

    private void validar(Administrador admin) throws BLException {
        ValidadorPersona.validarDatos(admin, "administrador");
        ValidadorPersona.validarHorarios(admin);
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
        ValidadorPersona.validarCuentaLibre(admin);
    }

    // dni y telefono son UNIQUE en la base
    private void validarUnicos(Administrador admin) throws BLException {
        try {
            Administrador porDni = administradorDAO.findByDni(admin.getDni());
            if (porDni != null && porDni.getId() != admin.getId()) {
                throw new BLException("Ya existe un administrador con el DNI " + admin.getDni());
            }
            // buscar_administrador_por_telefono no filtra activo: el telefono sigue ocupado tras dar de baja
            Administrador porTelefono = administradorDAO.findByTelefono(admin.getTelefono());
            if (porTelefono != null && porTelefono.getId() != admin.getId()) {
                throw new BLException("Ya existe un administrador con el teléfono " + admin.getTelefono());
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar el DNI o el teléfono del administrador", e);
        }
    }
}
