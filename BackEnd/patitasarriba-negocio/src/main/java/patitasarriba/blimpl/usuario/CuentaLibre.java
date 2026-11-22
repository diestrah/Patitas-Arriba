package patitasarriba.blimpl.usuario;

import patitasarriba.bl.excepciones.BLException;
import patitasarriba.bl.excepciones.ValidacionException;
import patitasarriba.dao.usuario.AdministradorDAO;
import patitasarriba.dao.usuario.ClienteDAO;
import patitasarriba.dao.usuario.VeterinarioDAO;
import patitasarriba.daoimpl.usuario.AdministradorDAOImpl;
import patitasarriba.daoimpl.usuario.ClienteDAOImpl;
import patitasarriba.daoimpl.usuario.VeterinarioDAOImpl;
import patitasarriba.modelo.usuario.Administrador;
import patitasarriba.modelo.usuario.Cliente;
import patitasarriba.modelo.usuario.Veterinario;

import java.sql.SQLException;

// Una cuenta pertenece a una sola persona, de cualquier rol (RN-CU-08). El UNIQUE de id_cuenta es por tabla,
// asi que la base no impide que un cliente y un veterinario compartan la misma cuenta.
// Las busquedas por cuenta no filtran activo: tambien detectan personas dadas de baja.
final class CuentaLibre {
    private static final ClienteDAO clienteDAO = new ClienteDAOImpl();
    private static final VeterinarioDAO veterinarioDAO = new VeterinarioDAOImpl();
    private static final AdministradorDAO administradorDAO = new AdministradorDAOImpl();

    private CuentaLibre() {
    }

    // Al modificar, la persona puede conservar su propia cuenta: se pasa su id (null al registrar)
    static void paraCliente(int idCuenta, Integer idCliente) throws BLException {
        try {
            Cliente cliente = clienteDAO.findByCuenta(idCuenta);
            if (cliente != null && (idCliente == null || cliente.getId() != idCliente)) ocupada();
            if (veterinarioDAO.findByCuenta(idCuenta) != null) ocupada();
            if (administradorDAO.findByCuenta(idCuenta) != null) ocupada();
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar si la cuenta está libre", e);
        } catch (IllegalArgumentException e) {
            // El DAO falla al leer una persona dada de baja cuya cuenta tambien se dio de baja: la fila existe
            ocupada();
        }
    }

    static void paraVeterinario(int idCuenta, Integer idVeterinario) throws BLException {
        try {
            if (clienteDAO.findByCuenta(idCuenta) != null) ocupada();
            Veterinario vet = veterinarioDAO.findByCuenta(idCuenta);
            if (vet != null && (idVeterinario == null || vet.getId() != idVeterinario)) ocupada();
            if (administradorDAO.findByCuenta(idCuenta) != null) ocupada();
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar si la cuenta está libre", e);
        } catch (IllegalArgumentException e) {
            ocupada();
        }
    }

    static void paraAdministrador(int idCuenta, Integer idAdministrador) throws BLException {
        try {
            if (clienteDAO.findByCuenta(idCuenta) != null) ocupada();
            if (veterinarioDAO.findByCuenta(idCuenta) != null) ocupada();
            Administrador admin = administradorDAO.findByCuenta(idCuenta);
            if (admin != null && (idAdministrador == null || admin.getId() != idAdministrador)) ocupada();
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar si la cuenta está libre", e);
        } catch (IllegalArgumentException e) {
            ocupada();
        }
    }

    private static void ocupada() throws ValidacionException {
        throw new ValidacionException("La cuenta ya pertenece a otra persona");
    }
}
