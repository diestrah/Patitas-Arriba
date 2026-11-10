package patitasarriba.bl.impl.usuario;

import patitasarriba.bl.BLException;
import patitasarriba.bl.NoEncontradoException;
import patitasarriba.bl.ValidacionException;
import patitasarriba.bl.VeterinarioBL;
import patitasarriba.dao.CitaDAO;
import patitasarriba.dao.CuentaDAO;
import patitasarriba.dao.VeterinarioDAO;
import patitasarriba.dao.impl.cita.CitaDAOImpl;
import patitasarriba.dao.impl.usuario.CuentaDAOImpl;
import patitasarriba.dao.impl.usuario.VeterinarioDAOImpl;
import patitasarriba.dao.transacciones.TransactionsManager;
import patitasarriba.modelo.cita.Cita;
import patitasarriba.modelo.cita.DetalleCita;
import patitasarriba.modelo.usuario.Veterinario;

import java.sql.SQLException;
import java.util.List;

public class VeterinarioBLImpl implements VeterinarioBL {
    private final VeterinarioDAO veterinarioDAO = new VeterinarioDAOImpl();
    private final CuentaDAO cuentaDAO = new CuentaDAOImpl();
    private final CitaDAO citaDAO = new CitaDAOImpl();
    @Override
    public List<Veterinario> findAll() throws BLException {
        try {
            return veterinarioDAO.findAll();
        } catch (SQLException e) {
            throw new BLException("No se pudo listar los veterinarios", e);
        }
    }

    @Override
    public Veterinario findById(Integer id) throws BLException {
        try {
            Veterinario veterinario = veterinarioDAO.findById(id);
            if(veterinario==null){
                throw new NoEncontradoException("No se pudo recuperar el veterinario con el id :"
                        +id);
            }
            return veterinario;
        } catch (SQLException e) {
            throw new BLException("No se pudo recuperar el veterinario", e);
        }
    }

    @Override
    public Veterinario findByDni(String dni) throws BLException {
        if(dni==null || dni.isBlank()){
            throw new ValidacionException("El dni debe ser valido");
        }
        try {
            // El DAO no filtra activo (lo necesita la validacion de unicos); la consulta solo
            // devuelve registros activos, igual que findById
            Veterinario encontrado = veterinarioDAO.findByDni(dni);
            return encontrado != null && encontrado.isActivo() ? encontrado : null;
        } catch (IllegalArgumentException e) {
            // El DAO falla al leer una persona dada de baja cuya cuenta tambien se dio de baja
            return null;
        } catch (SQLException e) {
            throw new BLException("Error al buscar el veterinario por DNI", e);
        }
    }

    @Override
    public void insert(Veterinario vet) throws BLException {
        validar(vet);
        validarCuentaActiva (vet);
        validarUnicos(vet);
        // activo es el borrado logico: registrar o modificar siempre deja el registro activo,
        // aunque el cliente mande "activo": false (para dar de baja esta el delete)
        vet.setActivo(true);
        TransactionsManager.iniciar();
        try {
            veterinarioDAO.insert(vet);
            TransactionsManager.commit();
        } catch (SQLException | RuntimeException e) {
            if (TransactionsManager.activa()) TransactionsManager.rollback();
            throw new BLException("No se pudo insertar el veterinario", e);
        }
    }

    @Override
    public void update(Veterinario vet) throws BLException {
        validar(vet);
        findById(vet.getId());
        validarCuentaActiva (vet);
        validarUnicos(vet);
        // activo es el borrado logico: registrar o modificar siempre deja el registro activo,
        // aunque el cliente mande "activo": false (para dar de baja esta el delete)
        vet.setActivo(true);
        TransactionsManager.iniciar();
        try {
            veterinarioDAO.update(vet);
            TransactionsManager.commit();
        } catch (SQLException | RuntimeException e) {
            if (TransactionsManager.activa()) TransactionsManager.rollback();
            throw new BLException("No se pudo actualizar el veterinario", e);
        }
    }

    @Override
    public void delete(Integer id) throws BLException {
        if (id == null) throw new ValidacionException("El id del veterinario no puede ser nulo");
        Veterinario vet = findById(id);
        validarSinCitas (id);
        TransactionsManager.iniciar();
        try {
            veterinarioDAO.delete(id);
            // La cuenta se da de baja con la persona, asi ya no puede iniciar sesion
            cuentaDAO.delete(vet.getCuenta().getId());
            TransactionsManager.commit();
        } catch (SQLException | RuntimeException e) {
            if (TransactionsManager.activa()) TransactionsManager.rollback();
            throw new BLException("No se pudo eliminar el veterinario", e);
        }
    }

    void validar(Veterinario vet) throws BLException {
        if (vet == null) {
            throw new ValidacionException("El veterinario no puede ser nulo");
        }
        if (vet.getDni() == null || !vet.getDni().matches("\\d{8}")) {
            throw new ValidacionException("El DNI debe contener exactamente 8 dígitos numéricos");
        }
        if (vet.getNombres() == null || vet.getNombres().trim().isEmpty()) {
            throw new ValidacionException("El nombre del veterinario es obligatorio");
        }
        if (vet.getNumeroColegiatura() == null || vet.getNumeroColegiatura().trim().isEmpty()) {
            throw new ValidacionException("El número de colegiatura es obligatorio");
        }
    }

    private void validarCuentaActiva(Veterinario vet) throws BLException {
        if (vet.getCuenta() == null) throw new ValidacionException("El veterinario debe tener una cuenta");
        try {
            if (cuentaDAO.findById(vet.getCuenta().getId()) == null) {
                throw new ValidacionException("La cuenta no existe o está dada de baja");
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar la cuenta del veterinario", e);
        }
    }

    private void validarSinCitas(int idVeterinario) throws BLException {
        try {
            for (Cita cita : citaDAO.findAll()) {
                for (DetalleCita detalle : cita.getDetalles()) {
                    if (detalle.getVeterinario().getId() == idVeterinario) {
                        throw new ValidacionException("No se puede eliminar el veterinario: está asignado a la cita "
                                + cita.getId() + " (" + cita.getEstado() + ")");
                    }
                }
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar las citas del veterinario", e);
        }
    }

    // dni, telefono y numero_colegiatura son UNIQUE en la base
    void validarUnicos(Veterinario vet) throws BLException {
        try {
            Veterinario porDni;
            try {
                porDni = veterinarioDAO.findByDni(vet.getDni());
            } catch (IllegalArgumentException e) {
                // El DAO falla al leer un veterinario dado de baja cuya cuenta tambien se dio de baja
                throw new ValidacionException("Ya existe un veterinario dado de baja con el DNI " + vet.getDni());
            }
            if (porDni != null && porDni.getId() != vet.getId()) {
                throw new ValidacionException("Ya existe un veterinario con el DNI " + vet.getDni());
            }
            for (Veterinario otro : veterinarioDAO.findAll()) {
                if (otro.getId() == vet.getId()) continue;
                if (otro.getTelefono().equals(vet.getTelefono())) {
                    throw new ValidacionException("Ya existe un veterinario con el teléfono " + vet.getTelefono());
                }
                if (otro.getNumeroColegiatura().equalsIgnoreCase(vet.getNumeroColegiatura())) {
                    throw new ValidacionException("Ya existe un veterinario con la colegiatura " + vet.getNumeroColegiatura());
                }
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar el DNI, el teléfono o la colegiatura del veterinario", e);
        }
    }

}
