package patitasarriba.bl.impl.usuario;

import patitasarriba.bl.BLException;
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
            return veterinarioDAO.findById(id);
        } catch (SQLException e) {
            throw new BLException("No se pudo recuperar el veterinario", e);
        }
    }

    @Override
    public Veterinario findByDni(String dni) throws BLException {
        try {
            return veterinarioDAO.findByDni(dni);
        } catch (SQLException e) {
            throw new BLException("Error al buscar el veterinario por DNI", e);
        }
    }

    @Override
    public void insert(Veterinario vet) throws BLException {
        validar(vet);
        validarCuentaActiva (vet);
        TransactionsManager.iniciar();
        try {
            veterinarioDAO.insert(vet);
            TransactionsManager.commit();
        } catch (SQLException | RuntimeException e) {
            if (TransactionsManager.activa()) TransactionsManager.rollback();
            throw new BLException("No se pudo registrar el veterinario", e);
        }
    }

    @Override
    public void update(Veterinario vet) throws BLException {
        validar(vet);
        validarCuentaActiva (vet);
        TransactionsManager.iniciar();
        try {
            veterinarioDAO.update(vet);
            TransactionsManager.commit();
        } catch (SQLException | RuntimeException e) {
            if (TransactionsManager.activa()) TransactionsManager.rollback();
            throw new BLException("No se pudo registrar el veterinario", e);
        }
    }

    @Override
    public void delete(Integer id) throws BLException {
        validarSinCitas (id);
        TransactionsManager.iniciar();
        try {
            veterinarioDAO.delete(id);
            TransactionsManager.commit();
        } catch (SQLException | RuntimeException e) {
            if (TransactionsManager.activa()) TransactionsManager.rollback();
            throw new BLException("No se pudo registrar el veterinario", e);
        }
    }

    private void validar(Veterinario vet) throws BLException {
        if (vet == null) {
            throw new BLException("El veterinario no puede ser nulo");
        }
        if (vet.getDni() == null || !vet.getDni().matches("\\d{8}")) {
            throw new BLException("El DNI debe contener exactamente 8 dígitos numéricos");
        }
        if (vet.getNombres() == null || vet.getNombres().trim().isEmpty()) {
            throw new BLException("El nombre del veterinario es obligatorio");
        }
        if (vet.getNumeroColegiatura() == null || vet.getNumeroColegiatura().trim().isEmpty()) {
            throw new BLException("El número de colegiatura es obligatorio");
        }
    }

    private void validarCuentaActiva(Veterinario vet) throws BLException {
        if (vet.getCuenta() == null) throw new BLException("El veterinario debe tener una cuenta");
        try {
            if (cuentaDAO.findById(vet.getCuenta().getId()) == null) {
                throw new BLException("La cuenta no existe o está dada de baja");
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
                        throw new BLException("No se puede eliminar el veterinario: está asignado a la cita "
                                + cita.getId() + " (" + cita.getEstado() + ")");
                    }
                }
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar las citas del veterinario", e);
        }
    }
}
