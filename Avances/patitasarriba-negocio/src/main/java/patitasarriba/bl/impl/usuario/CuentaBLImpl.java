package patitasarriba.bl.impl.usuario;

import patitasarriba.bl.BLException;
import patitasarriba.bl.CuentaBL;
import patitasarriba.bl.util.HashUtil;
import patitasarriba.dao.AdministradorDAO;
import patitasarriba.dao.ClienteDAO;
import patitasarriba.dao.CuentaDAO;
import patitasarriba.dao.VeterinarioDAO;
import patitasarriba.dao.impl.usuario.AdministradorDAOImpl;
import patitasarriba.dao.impl.usuario.ClienteDAOImpl;
import patitasarriba.dao.impl.usuario.CuentaDAOImpl;
import patitasarriba.dao.impl.usuario.VeterinarioDAOImpl;
import patitasarriba.dao.transacciones.TransactionsManager;
import patitasarriba.modelo.usuario.Administrador;
import patitasarriba.modelo.usuario.Cliente;
import patitasarriba.modelo.usuario.Cuenta;
import patitasarriba.modelo.usuario.Veterinario;

import java.sql.SQLException;
import java.util.List;

public class CuentaBLImpl implements CuentaBL {
    private final CuentaDAO cuentaDAO = new CuentaDAOImpl();
    private final ClienteDAO clienteDAO = new ClienteDAOImpl();
    private final VeterinarioDAO veterinarioDAO = new VeterinarioDAOImpl();
    private final AdministradorDAO administradorDAO = new AdministradorDAOImpl();
    @Override
    public List<Cuenta> findAll() throws BLException {
        try {
            return cuentaDAO.findAll();
        } catch (SQLException e) {
            throw new BLException("No se pudo listar las cuentas", e);
        }
    }

    @Override
    public Cuenta findById(Integer id) throws BLException {
        try {
            return cuentaDAO.findById(id);
        } catch (SQLException e) {
            throw new BLException("No se pudo recuperar la cuenta", e);
        }
    }

    @Override
    public Cuenta findByNombreUsuario(String nombreUsuario) throws BLException {
        try {
            return cuentaDAO.findByNombreUsuario(nombreUsuario);
        } catch (SQLException e) {
            throw new BLException("Error al buscar la cuenta por nombre de usuario", e);
        }
    }

    // @Override
    // public Cuenta login(String nombreUsuario, String passwordPlana) throws BLException {
    //     if (nombreUsuario == null || nombreUsuario.trim().isEmpty() ||
    //             passwordPlana == null || passwordPlana.trim().isEmpty()) {
    //         throw new BLException("Debe ingresar el usuario y la contraseña");
    //     }
    //
    //     Cuenta cuenta = findByNombreUsuario(nombreUsuario);
    //     if (cuenta == null || !cuenta.isActivo()) {
    //         throw new BLException("Usuario o contraseña incorrectos");
    //     }
    //
    //     String hashIngresado = HashUtil.generarHashSHA256(passwordPlana);
    //     if (!cuenta.getPassword().equals(hashIngresado)) {
    //         throw new BLException("Usuario o contraseña incorrectos");
    //     }
    //
    //     return cuenta;
    // }

    @Override
    public void insert(Cuenta cuenta) throws BLException {
        validar(cuenta);

        cuenta.setPassword(HashUtil.generarHashSHA256(cuenta.getPassword()));

        TransactionsManager.iniciar();
        try {
            cuentaDAO.insert(cuenta);
            TransactionsManager.commit();
        } catch (SQLException | RuntimeException e) {
            if (TransactionsManager.activa()) TransactionsManager.rollback();
            throw new BLException("No se pudo registrar la cuenta", e);
        }
    }

    //hash la contrasena
    @Override
    public void update(Cuenta cuenta) throws BLException {
        validar(cuenta);
        try {
            Cuenta actual = cuentaDAO.findById(cuenta.getId());
            if (actual == null) throw new BLException("No existe una cuenta activa con id " + cuenta.getId());
            // Si llega el mismo hash que esta guardado, la contrasena no cambio: no se vuelve a hashear
            if (!cuenta.getPassword().equals(actual.getPassword())) {
                cuenta.setPassword(HashUtil.generarHashSHA256(cuenta.getPassword()));
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar la cuenta", e);
        }
        TransactionsManager.iniciar();
        try {
            cuentaDAO.update(cuenta);
            TransactionsManager.commit();
        } catch (SQLException | RuntimeException e) {
            if (TransactionsManager.activa()) TransactionsManager.rollback();
            throw new BLException("No se pudo actualizar la cuenta", e);
        }
    }

    @Override
    public void delete(Integer id) throws BLException {
        validarSinPersonaActiva(id);
        TransactionsManager.iniciar();
        try {

            cuentaDAO.delete(id);
            TransactionsManager.commit();
        } catch (SQLException | RuntimeException e) {
            if (TransactionsManager.activa()) TransactionsManager.rollback();
            throw new BLException("No se pudo registrar la cuenta", e);
        }
    }

    private void validar(Cuenta cuenta) throws BLException {
        if (cuenta == null) {
            throw new BLException("La cuenta no puede ser nula");
        }
        if (cuenta.getNombreUsuario() == null || cuenta.getNombreUsuario().trim().isEmpty()) {
            throw new BLException("El nombre de usuario es obligatorio");
        }
        if (cuenta.getPassword() == null || cuenta.getPassword().trim().isEmpty()) {
            throw new BLException("La contraseña es obligatoria");
        }
        if (cuenta.getCorreo() == null || !cuenta.getCorreo().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new BLException("Debe ingresar un correo electrónico válido");
        }
    }

    //delete: no deja eliminar la cuenta si la usa una persona activa:
    private void validarSinPersonaActiva(int idCuenta) throws BLException {
        try {
            for (Cliente c : clienteDAO.findAll()) {
                if (c.getCuenta().getId() == idCuenta) throw new BLException("No se puede eliminar: la cuenta la usa un cliente activo");
            }
            for (Veterinario v : veterinarioDAO.findAll()) {
                if (v.getCuenta().getId() == idCuenta) throw new BLException("No se puede eliminar: la cuenta la usa un veterinario activo");
            }
            for (Administrador a : administradorDAO.findAll()) {
                if (a.getCuenta().getId() == idCuenta) throw new BLException("No se puede eliminar: la cuenta la usa un administrador activo");
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar quién usa la cuenta", e);
        }
    }
}
