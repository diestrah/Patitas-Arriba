package patitasarriba.bl.impl.usuario;

import patitasarriba.bl.util.Activos;
import patitasarriba.bl.BLException;
import patitasarriba.bl.ClienteBL;
import patitasarriba.bl.NoEncontradoException;
import patitasarriba.bl.ValidacionException;
import patitasarriba.bl.util.ValidadorPersona;
import patitasarriba.dao.*;
import patitasarriba.dao.impl.boleta.BoletaDAOImpl;
import patitasarriba.dao.impl.cita.CitaDAOImpl;
import patitasarriba.dao.impl.mascota.MascotaDAOImpl;
import patitasarriba.dao.impl.usuario.ClienteDAOImpl;
import patitasarriba.dao.impl.usuario.CuentaDAOImpl;
import patitasarriba.dao.transacciones.TransactionsManager;
import patitasarriba.modelo.boleta.Boleta;
import patitasarriba.modelo.cita.Cita;
import patitasarriba.modelo.usuario.Cliente;

import java.sql.SQLException;
import java.util.List;

public class ClienteBLImpl implements ClienteBL {
    private final ClienteDAO clienteDAO = new ClienteDAOImpl();
    private final CuentaDAO cuentaDAO = new CuentaDAOImpl();
    private final MascotaDAO mascotaDAO = new MascotaDAOImpl();
    private final CitaDAO citaDAO = new CitaDAOImpl();
    private final BoletaDAO boletaDAO = new BoletaDAOImpl();

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
            Cliente cliente = clienteDAO.findById(id);
            if(cliente==null){
                throw new NoEncontradoException("No se pudo recuperar el cliente con id :"+id);
            }
            return cliente;
        } catch (SQLException e) {
            throw new BLException("No se pudo recuperar el cliente", e);
        }
    }

    @Override
    public Cliente findByDni(String dni) throws BLException {
        if(dni==null || dni.isBlank()){
            throw new ValidacionException("El dni debe ser valido");
        }

        try {
            return clienteDAO.findByDni(dni);
        } catch (SQLException e) {
            throw new BLException("Error al buscar el cliente por DNI", e);
        }
    }

    @Override
    public void insert(Cliente cliente) throws BLException {
        Activos.marcar(cliente);
        validar(cliente);
        validarCuentaActiva (cliente);
        validarUnicos(cliente);
        TransactionsManager.iniciar();
        try {
            clienteDAO.insert(cliente);
            TransactionsManager.commit();
        } catch (SQLException | RuntimeException e) {
            if (TransactionsManager.activa()) TransactionsManager.rollback();
            throw new BLException("No se pudo insertar el cliente", e);
        }
    }

    @Override
    public void update(Cliente cliente) throws BLException {
        // RN-GE-06: solo se modifica un registro que existe y esta activo
        if (cliente != null) findById(cliente.getId());
        Activos.marcar(cliente);
        validar(cliente);
        validarCuentaActiva (cliente);
        validarUnicos(cliente);
        TransactionsManager.iniciar();
        try {
            clienteDAO.update(cliente);
            TransactionsManager.commit();
        } catch (SQLException | RuntimeException e) {
            if (TransactionsManager.activa()) TransactionsManager.rollback();
            throw new BLException("No se pudo actualizar el cliente", e);
        }
    }

    @Override
    public void delete(Integer id) throws BLException {
        if (id == null) throw new BLException("El id del cliente no puede ser nulo");
        Cliente cliente = findById(id);
        validarSinHistorial(id);
        TransactionsManager.iniciar();
        try {
            mascotaDAO.eliminarPorCliente(id);
            clienteDAO.delete(id);
            // RN-PE-06: la cuenta se da de baja con la persona, asi ya no puede iniciar sesion
            cuentaDAO.delete(cliente.getCuenta().getId());
            TransactionsManager.commit();
        } catch (SQLException | RuntimeException e) {
            if (TransactionsManager.activa()) TransactionsManager.rollback();
            throw new BLException("No se pudo eliminar el cliente", e);
        }
    }

    // No se elimina un cliente con citas o boletas: dejarian de poder leerse
    private void validarSinHistorial(int idCliente) throws BLException {
        try {
            for (Cita cita : citaDAO.findAll()) {
                if (cita.getMascota().getCliente().getId() == idCliente) {
                    throw new BLException("No se puede eliminar el cliente: su mascota "
                            + cita.getMascota().getNombre() + " tiene citas registradas");
                }
            }
            for (Boleta boleta : boletaDAO.findAll()) {
                if (boleta.getCliente().getId() == idCliente) {
                    throw new BLException("No se puede eliminar el cliente: tiene la boleta "
                            + boleta.getSerieCorrelativo());
                }
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar el historial del cliente", e);
        }
    }

    private void validar(Cliente cliente) throws BLException {
        ValidadorPersona.validarDatos(cliente, "cliente");
    }

    private void validarCuentaActiva(Cliente cliente) throws BLException {
        if (cliente.getCuenta() == null) throw new BLException("El cliente debe tener una cuenta");
        try {
            if (cuentaDAO.findById(cliente.getCuenta().getId()) == null) {
                throw new BLException("La cuenta no existe o está dada de baja");
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar la cuenta del cliente", e);
        }
        ValidadorPersona.validarCuentaLibre(cliente);
    }

    private void validarUnicos(Cliente cliente) throws BLException {
        try {
            Cliente porDni = clienteDAO.findByDni(cliente.getDni());
            if (porDni != null && porDni.getId() != cliente.getId()) {
                throw new BLException("Ya existe un cliente con el DNI " + cliente.getDni());
            }
            // buscar_cliente_por_telefono no filtra activo: el telefono de un cliente dado de baja sigue ocupado
            Cliente porTelefono = clienteDAO.findByTelefono(cliente.getTelefono());
            if (porTelefono != null && porTelefono.getId() != cliente.getId()) {
                throw new BLException("Ya existe un cliente con el teléfono " + cliente.getTelefono());
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar el DNI o el teléfono del cliente", e);
        }
    }

}
