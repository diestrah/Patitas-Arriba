package patitasarriba.bl.impl.usuario;

import patitasarriba.bl.BLException;
import patitasarriba.bl.CredencialesInvalidasException;
import patitasarriba.bl.CuentaBL;
import patitasarriba.bl.NoEncontradoException;
import patitasarriba.bl.ValidacionException;
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
import patitasarriba.modelo.usuario.Persona;
import patitasarriba.modelo.usuario.Veterinario;

import java.sql.SQLException;
import java.time.LocalDate;
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
            Cuenta cuenta= cuentaDAO.findById(id);
            if(cuenta==null){
                throw new NoEncontradoException("No se pudo encontrar la cuenta con el id "+id);
            }
            return cuenta;
        } catch (SQLException e) {
            throw new BLException("No se pudo recuperar la cuenta", e);
        }
    }

    @Override
    public Cuenta findByNombreUsuario(String nombreUsuario) throws BLException {
        if(nombreUsuario==null || nombreUsuario.isBlank()){
            throw new ValidacionException("El nombre ingresado debe ser valido");
        }

        try {
            // El DAO no filtra activo (lo necesita la validacion de unicos); la consulta solo
            // devuelve registros activos, igual que findById
            Cuenta encontrado = cuentaDAO.findByNombreUsuario(nombreUsuario);
            return encontrado != null && encontrado.isActivo() ? encontrado : null;
        } catch (SQLException e) {
            throw new BLException("Error al buscar la cuenta por nombre de usuario", e);
        }
    }



    @Override
    public Cuenta findByCorreo(String correo) throws BLException {
        if (correo == null || correo.isBlank()) {
            throw new ValidacionException("El correo ingresado debe ser valido");
        }

        try {
            // Igual que findByNombreUsuario: solo se devuelve una cuenta activa
            Cuenta encontrado = cuentaDAO.findByCorreo(correo);
            return encontrado != null && encontrado.isActivo() ? encontrado : null;
        } catch (SQLException e) {
            throw new BLException("Error al buscar la cuenta por correo", e);
        }
    }

    // RF01: se ingresa con el correo. El mensaje de error es el mismo si falla el correo o la contrasena
    @Override
    public Persona login(String correo, String passwordPlana) throws BLException {
        if (correo == null || correo.trim().isEmpty() ||
                passwordPlana == null || passwordPlana.trim().isEmpty()) {
            throw new ValidacionException("Debe ingresar el correo y la contraseña");
        }

        Cuenta cuenta = findByCorreo(correo);
        if (cuenta == null || !cuenta.isActivo()) {
            throw new CredencialesInvalidasException("Correo o contraseña incorrectos");
        }

        String hashIngresado = HashUtil.generarHashSHA256(passwordPlana);
        if (!cuenta.getPassword().equals(hashIngresado)) {
            throw new CredencialesInvalidasException("Correo o contraseña incorrectos");
        }

        // El rol lo da el tipo de persona duena de la cuenta
        try {
            Persona persona = clienteDAO.findByCuenta(cuenta.getId());
            if (persona == null) persona = veterinarioDAO.findByCuenta(cuenta.getId());
            if (persona == null) persona = administradorDAO.findByCuenta(cuenta.getId());
            if (persona == null || !persona.isActivo()) {
                throw new CredencialesInvalidasException("Correo o contraseña incorrectos");
            }
            return persona;
        } catch (SQLException e) {
            throw new BLException("No se pudo iniciar sesión", e);
        }
    }

    @Override
    public void insert(Cuenta cuenta) throws BLException {
        validar(cuenta);
        if (cuenta.getPassword() == null || cuenta.getPassword().trim().isEmpty()) {
            throw new ValidacionException("La contraseña es obligatoria");
        }
        validarUnicos(cuenta);

        cuenta.setPassword(HashUtil.generarHashSHA256(cuenta.getPassword()));
        cuenta.setFechaCreacion(LocalDate.now());

        // activo es el borrado logico: registrar o modificar siempre deja la cuenta activa,
        // aunque el cliente mande "activo": false (para dar de baja esta el delete)
        cuenta.setActivo(true);

        TransactionsManager.iniciar();
        try {
            cuentaDAO.insert(cuenta);
            TransactionsManager.commit();
        } catch (SQLException | RuntimeException e) {
            if (TransactionsManager.activa()) TransactionsManager.rollback();
            throw new BLException("No se pudo insertar la cuenta", e);
        }
    }

    //hash la contrasena
    @Override
    public void update(Cuenta cuenta) throws BLException {
        validar(cuenta);
        validarUnicos(cuenta);
        try {
            Cuenta actual = cuentaDAO.findById(cuenta.getId());
            if (actual == null) throw new NoEncontradoException("No existe una cuenta activa con id " + cuenta.getId());
            if (cuenta.getPassword() == null || cuenta.getPassword().trim().isEmpty()) {
                cuenta.setPassword(actual.getPassword());
            // Si llega el mismo hash que esta guardado, la contrasena no cambio: no se vuelve a hashear
            } else if (!cuenta.getPassword().equals(actual.getPassword())) {
                cuenta.setPassword(HashUtil.generarHashSHA256(cuenta.getPassword()));
            }
            cuenta.setFechaCreacion(actual.getFechaCreacion());
            cuenta.setActivo(true);
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
        findById(id);
        validarSinPersonaActiva(id);
        TransactionsManager.iniciar();
        try {

            cuentaDAO.delete(id);
            TransactionsManager.commit();
        } catch (SQLException | RuntimeException e) {
            if (TransactionsManager.activa()) TransactionsManager.rollback();
            throw new BLException("No se pudo eliminar la cuenta", e);
        }
    }

    void validar(Cuenta cuenta) throws BLException {
        if (cuenta == null) {
            throw new ValidacionException("La cuenta no puede ser nula");
        }
        if (cuenta.getNombreUsuario() == null || cuenta.getNombreUsuario().trim().isEmpty()) {
            throw new ValidacionException("El nombre de usuario es obligatorio");
        }
        if (cuenta.getCorreo() == null || !cuenta.getCorreo().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new ValidacionException("Debe ingresar un correo electrónico válido");
        }
    }

    //delete: no deja eliminar la cuenta si la usa una persona activa:
    private void validarSinPersonaActiva(int idCuenta) throws BLException {
        try {
            for (Cliente c : clienteDAO.findAll()) {
                if (c.getCuenta().getId() == idCuenta) throw new ValidacionException("No se puede eliminar: la cuenta la usa un cliente activo");
            }
            for (Veterinario v : veterinarioDAO.findAll()) {
                if (v.getCuenta().getId() == idCuenta) throw new ValidacionException("No se puede eliminar: la cuenta la usa un veterinario activo");
            }
            for (Administrador a : administradorDAO.findAll()) {
                if (a.getCuenta().getId() == idCuenta) throw new ValidacionException("No se puede eliminar: la cuenta la usa un administrador activo");
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar quién usa la cuenta", e);
        }
    }

    // nombre_usuario y correo son UNIQUE en la base
    void validarUnicos(Cuenta cuenta) throws BLException {
        try {
            // buscar_cuenta_por_nombre_usuario no filtra activo: detecta tambien cuentas dadas de baja
            Cuenta porUsuario = cuentaDAO.findByNombreUsuario(cuenta.getNombreUsuario());
            if (porUsuario != null && porUsuario.getId() != cuenta.getId()) {
                throw new ValidacionException("El nombre de usuario '" + cuenta.getNombreUsuario() + "' ya está en uso");
            }
            for (Cuenta otra : cuentaDAO.findAll()) {
                if (otra.getId() != cuenta.getId() && otra.getCorreo().equalsIgnoreCase(cuenta.getCorreo())) {
                    throw new ValidacionException("El correo '" + cuenta.getCorreo() + "' ya está registrado");
                }
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar el usuario o el correo", e);
        }
    }
}
