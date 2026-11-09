package patitasarriba.bl.impl.usuario;

import patitasarriba.bl.util.Activos;
import patitasarriba.bl.BLException;
import patitasarriba.bl.NoEncontradoException;
import patitasarriba.bl.ValidacionException;
import patitasarriba.bl.VeterinarioBL;
import patitasarriba.bl.util.ValidadorPersona;
import patitasarriba.dao.CitaDAO;
import patitasarriba.dao.CuentaDAO;
import patitasarriba.dao.VeterinarioDAO;
import patitasarriba.dao.impl.cita.CitaDAOImpl;
import patitasarriba.dao.impl.usuario.CuentaDAOImpl;
import patitasarriba.dao.impl.usuario.VeterinarioDAOImpl;
import patitasarriba.dao.transacciones.TransactionsManager;
import patitasarriba.modelo.cita.Cita;
import patitasarriba.modelo.cita.DetalleCita;
import patitasarriba.modelo.cita.EstadoCita;
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
            return veterinarioDAO.findByDni(dni);
        } catch (SQLException e) {
            throw new BLException("Error al buscar el veterinario por DNI", e);
        }
    }

    @Override
    public void insert(Veterinario vet) throws BLException {
        Activos.marcar(vet);
        validar(vet);
        validarCuentaActiva (vet);
        validarUnicos(vet);
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
        // RN-GE-06: solo se modifica un registro que existe y esta activo
        if (vet != null) findById(vet.getId());
        Activos.marcar(vet);
        validar(vet);
        validarCuentaActiva (vet);
        validarUnicos(vet);
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
        if (id == null) throw new BLException("El id del veterinario no puede ser nulo");
        Veterinario vet = findById(id);
        validarSinCitasPendientes(id);
        TransactionsManager.iniciar();
        try {
            veterinarioDAO.delete(id);
            // RN-PE-06: la cuenta se da de baja con la persona, asi ya no puede iniciar sesion
            cuentaDAO.delete(vet.getCuenta().getId());
            TransactionsManager.commit();
        } catch (SQLException | RuntimeException e) {
            if (TransactionsManager.activa()) TransactionsManager.rollback();
            throw new BLException("No se pudo eliminar el veterinario", e);
        }
    }

    // RN-VE-03: las citas terminadas se conservan, pero no se deja a un veterinario dado de baja
    // con citas por atender: primero hay que reasignarlas, cancelarlas o rechazarlas
    private void validarSinCitasPendientes(int idVeterinario) throws BLException {
        try {
            for (Cita cita : citaDAO.findAll()) {
                if (cita.getEstado() != EstadoCita.PENDIENTE && cita.getEstado() != EstadoCita.APROBADO) continue;
                for (DetalleCita detalle : cita.getDetalles()) {
                    if (detalle.getVeterinario().getId() == idVeterinario) {
                        throw new BLException("No se puede dar de baja al veterinario: tiene la cita "
                                + cita.getId() + " en estado " + cita.getEstado());
                    }
                }
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar las citas del veterinario", e);
        }
    }

    private void validar(Veterinario vet) throws BLException {
        ValidadorPersona.validarDatos(vet, "veterinario");
        if (vet.getNumeroColegiatura() == null || vet.getNumeroColegiatura().trim().isEmpty()) {
            throw new BLException("El número de colegiatura es obligatorio");
        }
        ValidadorPersona.validarHorarios(vet);
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
        ValidadorPersona.validarCuentaLibre(vet);
    }

    // dni, telefono y numero_colegiatura son UNIQUE en la base
    private void validarUnicos(Veterinario vet) throws BLException {
        try {
            Veterinario porDni = veterinarioDAO.findByDni(vet.getDni());
            if (porDni != null && porDni.getId() != vet.getId()) {
                throw new BLException("Ya existe un veterinario con el DNI " + vet.getDni());
            }
            // Los finders por telefono y colegiatura no filtran activo: siguen ocupados tras dar de baja
            Veterinario porTelefono = veterinarioDAO.findByTelefono(vet.getTelefono());
            Veterinario porColegiatura = veterinarioDAO.findByColegiatura(vet.getNumeroColegiatura());
            if (porTelefono != null && porTelefono.getId() != vet.getId()) {
                throw new BLException("Ya existe un veterinario con el teléfono " + vet.getTelefono());
            }
            if (porColegiatura != null && porColegiatura.getId() != vet.getId()) {
                throw new BLException("Ya existe un veterinario con la colegiatura " + vet.getNumeroColegiatura());
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar el DNI, el teléfono o la colegiatura del veterinario", e);
        }
    }

}
