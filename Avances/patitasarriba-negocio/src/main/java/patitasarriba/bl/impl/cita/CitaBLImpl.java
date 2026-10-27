package patitasarriba.bl.impl.cita;

import patitasarriba.bl.BLException;
import patitasarriba.bl.CitaBL;
import patitasarriba.dao.CitaDAO;
import patitasarriba.dao.MascotaDAO;
import patitasarriba.dao.VeterinarioDAO;
import patitasarriba.dao.impl.cita.CitaDAOImpl;
import patitasarriba.dao.impl.mascota.MascotaDAOImpl;
import patitasarriba.dao.impl.usuario.VeterinarioDAOImpl;
import patitasarriba.dao.transacciones.TransactionsManager;
import patitasarriba.modelo.cita.Cita;
import patitasarriba.modelo.cita.DetalleCita;
import patitasarriba.modelo.cita.EstadoCita;
import patitasarriba.modelo.horario.DiaSemana;
import patitasarriba.modelo.usuario.Veterinario;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class CitaBLImpl implements CitaBL {
    private final CitaDAO citaDAO = new CitaDAOImpl();
    private final MascotaDAO mascotaDAO = new MascotaDAOImpl();
    private final VeterinarioDAO veterinarioDAO = new VeterinarioDAOImpl();

    @Override
    public List<Cita> findAll() throws BLException {
        try {
            return citaDAO.findAll();
        } catch (SQLException e) {
            throw new BLException("No se pudo listar las citas", e);
        }
    }

    @Override
    public Cita findById(Integer id) throws BLException {
        try {
            return citaDAO.findById(id);
        } catch (SQLException e) {
            throw new BLException("No se pudo recuperar la cita", e);
        }
    }

    @Override
    public void insert(Cita cita) throws BLException {
        validar(cita);
        validarFechaFutura(cita);
        validarEstadoInicial(cita);
        validarMascotaExiste(cita);
        validarDisponibilidad(cita);

        TransactionsManager.iniciar();
        try {
            citaDAO.insert(cita);
            TransactionsManager.commit();
        } catch (SQLException e) {
            TransactionsManager.rollback();
            throw new BLException("No se pudo registrar la cita", e);
        }
    }

    @Override
    public void update(Cita cita) throws BLException {
        validar(cita);
        Cita actual = obtenerExistente(cita.getId());
        validarCambioDeEstado(actual, cita);
        // La fecha futura solo se exige al reprogramar: completar una cita cuya hora ya paso es valido
        if (!actual.getFechaHora().equals(cita.getFechaHora())) {
            validarFechaFutura(cita);
            validarDisponibilidad(cita);
        }

        TransactionsManager.iniciar();
        try {
            citaDAO.update(cita);
            TransactionsManager.commit();
        } catch (SQLException e) {
            TransactionsManager.rollback();
            throw new BLException("No se pudo actualizar la cita", e);
        }
    }

    @Override
    public void delete(Integer id) throws BLException {
        validarExiste(id);
        TransactionsManager.iniciar();
        try {
            citaDAO.delete(id);
            TransactionsManager.commit();
        } catch (SQLException e) {
            TransactionsManager.rollback();
            throw new BLException("No se pudo eliminar la cita", e);
        }
    }

    // Solo al crear o reprogramar: no se aplica al actualizar el estado de una cita existente
    private void validarFechaFutura(Cita cita) throws BLException {
        if (cita.getFechaHora().isBefore(LocalDateTime.now())) {
            throw new BLException("La fecha y hora de la cita no puede ser en el pasado");
        }
    }

    private void validar(Cita cita) throws BLException {
        if (cita == null) {
            throw new BLException("La cita no puede ser nula");
        }
        if (cita.getFechaHora() == null) {
            throw new BLException("La cita debe tener fecha y hora");
        }
        if (cita.getMascota() == null) {
            throw new BLException("La cita debe tener una mascota");
        }
        if (cita.getDetalles() == null || cita.getDetalles().isEmpty()) {
            throw new BLException("La cita debe tener al menos un servicio");
        }
        for (DetalleCita detalle : cita.getDetalles()) {
            if (detalle == null || detalle.getServicio() == null || detalle.getVeterinario() == null) {
                throw new BLException("Cada servicio de la cita debe tener servicio y veterinario");
            }
        }
    }

    private void validarEstadoInicial(Cita cita) throws BLException {
        if (cita == null) {
            throw new BLException("La cita no puede ser nula");
        }
        if (cita.getEstado() != EstadoCita.PENDIENTE) {
            throw new BLException("Toda cita nueva debe registrarse con estado PENDIENTE");
        }
    }

    private void validarMascotaExiste(Cita cita) throws BLException {
        try {
            if (mascotaDAO.findById(cita.getMascota().getId()) == null) {
                throw new BLException("La mascota de la cita no existe");
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar la mascota de la cita", e);
        }
    }

    // El veterinario de cada servicio debe existir y atender ese dia y a esa hora
    private void validarDisponibilidad(Cita cita) throws BLException {
        DiaSemana dia = DiaSemana.values()[cita.getFechaHora().getDayOfWeek().getValue() - 1];
        LocalTime hora = cita.getFechaHora().toLocalTime();
        try {
            for (DetalleCita detalle : cita.getDetalles()) {
                Veterinario veterinario = veterinarioDAO.findById(detalle.getVeterinario().getId());
                if (veterinario == null) {
                    throw new BLException("El veterinario asignado no existe");
                }
                boolean atiende = veterinario.getHorarios().stream().anyMatch(h ->
                        h.getDiaSemana() == dia
                                && !hora.isBefore(h.getHoraInicio())
                                && hora.isBefore(h.getHoraFin()));
                if (!atiende) {
                    throw new BLException("El veterinario no atiende en ese horario");
                }
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar la disponibilidad del veterinario", e);
        }
    }

    // Reglas del ciclo de vida: una cita cerrada no se modifica y aprobar/rechazar deja registrado al administrador
    private void validarCambioDeEstado(Cita actual, Cita nueva) throws BLException {
        EstadoCita estadoActual = actual.getEstado();
        if (nueva.getEstado() == EstadoCita.CANCELADO && estadoActual == EstadoCita.COMPLETADO) {
            throw new BLException("No se puede cancelar una cita ya completada");
        }
        if (estadoActual == EstadoCita.CANCELADO || estadoActual == EstadoCita.COMPLETADO
                || estadoActual == EstadoCita.RECHAZADO) {
            throw new BLException("No se puede modificar una cita en estado " + estadoActual);
        }
        if ((nueva.getEstado() == EstadoCita.APROBADO || nueva.getEstado() == EstadoCita.RECHAZADO)
                && nueva.getAdministrador() == null) {
            throw new BLException("Debe indicarse el administrador que aprueba o rechaza la cita");
        }
    }

    private Cita obtenerExistente(int id) throws BLException {
        try {
            Cita cita = citaDAO.findById(id);
            if (cita == null) {
                throw new BLException("No existe una cita con id " + id);
            }
            return cita;
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar la existencia de la cita", e);
        }
    }

    private void validarExiste(int id) throws BLException {
        obtenerExistente(id);
    }
}
