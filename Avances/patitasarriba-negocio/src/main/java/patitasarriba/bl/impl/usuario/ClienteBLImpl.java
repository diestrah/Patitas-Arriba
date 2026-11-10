package patitasarriba.bl.impl.usuario;

import patitasarriba.bl.BLException;
import patitasarriba.bl.ClienteBL;
import patitasarriba.bl.NoEncontradoException;
import patitasarriba.bl.ValidacionException;
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
            // El DAO no filtra activo (lo necesita la validacion de unicos); la consulta solo
            // devuelve registros activos, igual que findById
            Cliente encontrado = clienteDAO.findByDni(dni);
            return encontrado != null && encontrado.isActivo() ? encontrado : null;
        } catch (IllegalArgumentException e) {
            // El DAO falla al leer una persona dada de baja cuya cuenta tambien se dio de baja
            return null;
        } catch (SQLException e) {
            throw new BLException("Error al buscar el cliente por DNI", e);
        }
    }

    @Override
    public void insert(Cliente cliente) throws BLException {
        validar(cliente);
        validarCuentaActiva (cliente);
        validarUnicos(cliente);
        // activo es el borrado logico: registrar o modificar siempre deja el registro activo,
        // aunque el cliente mande "activo": false (para dar de baja esta el delete)
        cliente.setActivo(true);
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
        validar(cliente);
        findById(cliente.getId());
        validarCuentaActiva (cliente);
        validarUnicos(cliente);
        // activo es el borrado logico: registrar o modificar siempre deja el registro activo,
        // aunque el cliente mande "activo": false (para dar de baja esta el delete)
        cliente.setActivo(true);
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
        if (id == null) throw new ValidacionException("El id del cliente no puede ser nulo");
        Cliente cliente = findById(id);
        validarSinHistorial(id);
        TransactionsManager.iniciar();
        try {
            mascotaDAO.eliminarPorCliente(id);
            clienteDAO.delete(id);
            // La cuenta se da de baja con la persona, asi ya no puede iniciar sesion
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
                    throw new ValidacionException("No se puede eliminar el cliente: su mascota "
                            + cita.getMascota().getNombre() + " tiene citas registradas");
                }
            }
            for (Boleta boleta : boletaDAO.findAll()) {
                if (boleta.getCliente().getId() == idCliente) {
                    throw new ValidacionException("No se puede eliminar el cliente: tiene la boleta "
                            + boleta.getSerieCorrelativo());
                }
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar el historial del cliente", e);
        }
    }

    private void validar(Cliente cliente) throws BLException {
        if (cliente == null) {
            throw new ValidacionException("El cliente no puede ser nulo");
        }
        if (cliente.getDni() == null || !cliente.getDni().matches("\\d{8}")) {
            throw new ValidacionException("El DNI debe contener exactamente 8 dígitos numéricos");
        }
        if (cliente.getNombres() == null || cliente.getNombres().trim().isEmpty()) {
            throw new ValidacionException("El nombre del cliente es obligatorio");
        }
        if (cliente.getApellidoPaterno() == null || cliente.getApellidoPaterno().trim().isEmpty()) {
            throw new ValidacionException("El apellido paterno es obligatorio");
        }
    }

    private void validarCuentaActiva(Cliente cliente) throws BLException {
        if (cliente.getCuenta() == null) throw new ValidacionException("El cliente debe tener una cuenta");
        try {
            if (cuentaDAO.findById(cliente.getCuenta().getId()) == null) {
                throw new ValidacionException("La cuenta no existe o está dada de baja");
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar la cuenta del cliente", e);
        }
    }

    private void validarUnicos(Cliente cliente) throws BLException {
        try {
            Cliente porDni;
            try {
                porDni = clienteDAO.findByDni(cliente.getDni());
            } catch (IllegalArgumentException e) {
                // El DAO falla al leer un cliente dado de baja cuya cuenta tambien se dio de baja:
                // igual significa que el DNI ya existe
                throw new ValidacionException("Ya existe un cliente dado de baja con el DNI " + cliente.getDni());
            }
            if (porDni != null && porDni.getId() != cliente.getId()) {
                throw new ValidacionException("Ya existe un cliente con el DNI " + cliente.getDni());
            }
            for (Cliente otro : clienteDAO.findAll()) {
                if (otro.getId() != cliente.getId() && otro.getTelefono().equals(cliente.getTelefono())) {
                    throw new ValidacionException("Ya existe un cliente con el teléfono " + cliente.getTelefono());
                }
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar el DNI o el teléfono del cliente", e);
        }
    }

}
