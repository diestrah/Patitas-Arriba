package patitasarriba.bl.impl.usuario;

import patitasarriba.bl.BLException;
import patitasarriba.bl.CuentaBL;
import patitasarriba.bl.util.HashUtil;
import patitasarriba.dao.CuentaDAO;
import patitasarriba.dao.impl.usuario.CuentaDAOImpl;
import patitasarriba.dao.transacciones.TransactionsManager;
import patitasarriba.modelo.usuario.Cuenta;

import java.sql.SQLException;
import java.util.List;

public class CuentaBLImpl implements CuentaBL {
    private final CuentaDAO cuentaDAO = new CuentaDAOImpl();

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

    @Override
    public Cuenta login(String nombreUsuario, String passwordPlana) throws BLException {
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty() ||
                passwordPlana == null || passwordPlana.trim().isEmpty()) {
            throw new BLException("Debe ingresar el usuario y la contraseña");
        }

        Cuenta cuenta = findByNombreUsuario(nombreUsuario);
        if (cuenta == null || !cuenta.isActivo()) {
            throw new BLException("Usuario o contraseña incorrectos");
        }

        String hashIngresado = HashUtil.generarHashSHA256(passwordPlana);
        if (!cuenta.getPassword().equals(hashIngresado)) {
            throw new BLException("Usuario o contraseña incorrectos");
        }

        return cuenta;
    }

    @Override
    public void insert(Cuenta cuenta) throws BLException {
        validar(cuenta);

        cuenta.setPassword(HashUtil.generarHashSHA256(cuenta.getPassword()));

        TransactionsManager.iniciar();
        try {
            cuentaDAO.insert(cuenta);
            TransactionsManager.commit();
        } catch (SQLException e) {
            TransactionsManager.rollback();
            throw new BLException("No se pudo registrar la cuenta", e);
        }
    }

    @Override
    public void update(Cuenta cuenta) throws BLException {
        validar(cuenta);

        if (cuenta.getPassword() != null && !cuenta.getPassword().isEmpty()) {
            cuenta.setPassword(HashUtil.generarHashSHA256(cuenta.getPassword()));
        }

        TransactionsManager.iniciar();
        try {
            cuentaDAO.update(cuenta);
            TransactionsManager.commit();
        } catch (SQLException e) {
            TransactionsManager.rollback();
            throw new BLException("No se pudo actualizar la cuenta", e);
        }
    }

    @Override
    public void delete(Integer id) throws BLException {
        TransactionsManager.iniciar();
        try {
            cuentaDAO.delete(id);
            TransactionsManager.commit();
        } catch (SQLException e) {
            TransactionsManager.rollback();
            throw new BLException("No se pudo eliminar la cuenta", e);
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
        if (cuenta.getCorreo() == null || !cuenta.getCorreo().contains("@")) {
            throw new BLException("Debe ingresar un correo electrónico válido");
        }
    }
}
