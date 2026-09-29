package patitasarriba.bl.impl.usuario;

import patitasarriba.bl.BLException;
import patitasarriba.bl.ClienteBL;
import patitasarriba.dao.ClienteDAO;
import patitasarriba.dao.impl.usuario.ClienteDAOImpl;
import patitasarriba.dao.transacciones.TransactionsManager;
import patitasarriba.modelo.usuario.Cliente;

import java.sql.SQLException;
import java.util.List;

public class ClienteBLImpl implements ClienteBL {
    private final ClienteDAO clienteDAO = new ClienteDAOImpl();

    @Override
    public List<Cliente> findAll() throws BLException {
        try {
            return clienteDAO.findAll();
        } catch (SQLException e) {
            throw new BLException("No se pudo listar los clientes", e);
        }
    }

    @Override
    public Cliente findById(Integer id) throws BLException {
        try {
            return clienteDAO.findById(id);
        } catch (SQLException e) {
            throw new BLException("No se pudo recuperar el cliente", e);
        }
    }

    @Override
    public Cliente findByDni(String dni) throws BLException {
        try {
            return clienteDAO.findByDni(dni);
        } catch (SQLException e) {
            throw new BLException("Error al buscar el cliente por DNI", e);
        }
    }

    @Override
    public void insert(Cliente cliente) throws BLException {
        validar(cliente);

        TransactionsManager.iniciar();
        try {
            clienteDAO.insert(cliente);
            TransactionsManager.commit();
        } catch (SQLException e) {
            TransactionsManager.rollback();
            throw new BLException("No se pudo registrar el cliente", e);
        }
    }

    @Override
    public void update(Cliente cliente) throws BLException {
        validar(cliente);

        TransactionsManager.iniciar();
        try {
            clienteDAO.update(cliente);
            TransactionsManager.commit();
        } catch (SQLException e) {
            TransactionsManager.rollback();
            throw new BLException("No se pudo actualizar el cliente", e);
        }
    }

    @Override
    public void delete(Integer id) throws BLException {
        TransactionsManager.iniciar();
        try {
            clienteDAO.delete(id);
            TransactionsManager.commit();
        } catch (SQLException e) {
            TransactionsManager.rollback();
            throw new BLException("No se pudo eliminar el cliente", e);
        }
    }

    private void validar(Cliente cliente) throws BLException {
        if (cliente == null) {
            throw new BLException("El cliente no puede ser nulo");
        }
        if (cliente.getDni() == null || !cliente.getDni().matches("\\d{8}")) {
            throw new BLException("El DNI debe contener exactamente 8 dígitos numéricos");
        }
        if (cliente.getNombres() == null || cliente.getNombres().trim().isEmpty()) {
            throw new BLException("El nombre del cliente es obligatorio");
        }
        if (cliente.getApellidoPaterno() == null || cliente.getApellidoPaterno().trim().isEmpty()) {
            throw new BLException("El apellido paterno es obligatorio");
        }
    }
}
