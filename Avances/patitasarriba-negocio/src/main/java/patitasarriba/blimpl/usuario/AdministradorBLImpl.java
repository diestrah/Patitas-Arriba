package patitasarriba.blimpl.usuario;

import patitasarriba.bl.usuario.AdministradorBL;
import patitasarriba.bl.excepciones.BLException;
import patitasarriba.bl.excepciones.ValidacionException;
import patitasarriba.bl.excepciones.NoEncontradoException;
import patitasarriba.dao.usuario.AdministradorDAO;
import patitasarriba.dao.usuario.CuentaDAO;
import patitasarriba.daoimpl.usuario.AdministradorDAOImpl;
import patitasarriba.daoimpl.usuario.CuentaDAOImpl;
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
            // El DAO no filtra activo (lo necesita la validacion de unicos); la consulta solo
            // devuelve registros activos, igual que findById
            Administrador encontrado = administradorDAO.findByDni(dni);
            return encontrado != null && encontrado.isActivo() ? encontrado : null;
        } catch (IllegalArgumentException e) {
            // El DAO falla al leer una persona dada de baja cuya cuenta tambien se dio de baja
            return null;
        } catch (SQLException e) {
            throw new BLException("Error al buscar el administrador por DNI", e);
        }
    }

    @Override
    public void insert(Administrador admin) throws BLException {
        validar(admin);
        validarCuentaActiva (admin);
        validarUnicos(admin);
        // activo es el borrado logico: registrar o modificar siempre deja el registro activo,
        // aunque el cliente mande "activo": false (para dar de baja esta el delete)
        admin.setActivo(true);
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
        validar(admin);
        findById(admin.getId());
        validarCuentaActiva(admin);
        validarUnicos(admin);
        // activo es el borrado logico: registrar o modificar siempre deja el registro activo,
        // aunque el cliente mande "activo": false (para dar de baja esta el delete)
        admin.setActivo(true);
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
        Administrador admin = findById(id);
        // Para que no nos quedemos en 0 administradores. Se valida antes de abrir la transaccion:
        // dentro del try, la BLException no la captura el catch y la transaccion quedaba abierta
        try {
            if (administradorDAO.findAll().size() <= 1) {
                throw new ValidacionException("No se puede eliminar al único administrador activo");
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar los administradores activos", e);
        }
        TransactionsManager.iniciar();
        try {
            administradorDAO.delete(id);
            // La cuenta se da de baja con la persona, asi ya no puede iniciar sesion
            cuentaDAO.delete(admin.getCuenta().getId());
            TransactionsManager.commit();
        } catch (SQLException | RuntimeException e) {
            if (TransactionsManager.activa()) TransactionsManager.rollback();
            throw new BLException("No se pudo eliminar el administrador", e);
        }
    }

    void validar(Administrador admin) throws BLException {
        if (admin == null) {
            throw new ValidacionException("El administrador no puede ser nulo");
        }
        if (admin.getDni() == null || !admin.getDni().matches("\\d{8}")) {
            throw new ValidacionException("El DNI debe contener exactamente 8 dígitos numéricos");
        }
        if (admin.getNombres() == null || admin.getNombres().trim().isEmpty()) {
            throw new ValidacionException("El nombre del administrador es obligatorio");
        }
    }

    private void validarCuentaActiva(Administrador admin) throws BLException {
        if (admin.getCuenta() == null) throw new ValidacionException("El administrador debe tener una cuenta");
        try {
            if (cuentaDAO.findById(admin.getCuenta().getId()) == null) {
                throw new ValidacionException("La cuenta no existe o está dada de baja");
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar la cuenta del administrador", e);
        }
    }

    // dni y telefono son UNIQUE en la base
    void validarUnicos(Administrador admin) throws BLException {
        try {
            Administrador porDni;
            try {
                porDni = administradorDAO.findByDni(admin.getDni());
            } catch (IllegalArgumentException e) {
                // El DAO falla al leer un administrador dado de baja cuya cuenta tambien se dio de baja
                throw new ValidacionException("Ya existe un administrador dado de baja con el DNI " + admin.getDni());
            }
            if (porDni != null && porDni.getId() != admin.getId()) {
                throw new ValidacionException("Ya existe un administrador con el DNI " + admin.getDni());
            }
            for (Administrador otro : administradorDAO.findAll()) {
                if (otro.getId() != admin.getId() && otro.getTelefono().equals(admin.getTelefono())) {
                    throw new ValidacionException("Ya existe un administrador con el teléfono " + admin.getTelefono());
                }
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar el DNI o el teléfono del administrador", e);
        }
    }
}
