package patitasarriba.bl.impl.cita;

import patitasarriba.bl.util.Activos;
import patitasarriba.bl.BLException;
import patitasarriba.bl.CitaBL;
import patitasarriba.bl.NoEncontradoException;
import patitasarriba.dao.AdministradorDAO;
import patitasarriba.dao.AtencionDAO;
import patitasarriba.dao.BoletaDAO;
import patitasarriba.dao.CitaDAO;
import patitasarriba.dao.MascotaDAO;
import patitasarriba.dao.ServicioDAO;
import patitasarriba.dao.VeterinarioDAO;
import patitasarriba.dao.impl.atencion.AtencionDAOImpl;
import patitasarriba.dao.impl.boleta.BoletaDAOImpl;
import patitasarriba.dao.impl.cita.CitaDAOImpl;
import patitasarriba.dao.impl.mascota.MascotaDAOImpl;
import patitasarriba.dao.impl.producto.ServicioDAOImpl;
import patitasarriba.dao.impl.usuario.AdministradorDAOImpl;
import patitasarriba.dao.impl.usuario.VeterinarioDAOImpl;
import patitasarriba.dao.transacciones.TransactionsManager;
import patitasarriba.modelo.atencion.Atencion;
import patitasarriba.modelo.cita.Cita;
import patitasarriba.modelo.cita.DetalleCita;
import patitasarriba.modelo.cita.EstadoCita;
import patitasarriba.modelo.horario.DiaSemana;
import patitasarriba.modelo.horario.Horario;
import patitasarriba.modelo.producto.Servicio;
import patitasarriba.modelo.usuario.Veterinario;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CitaBLImpl implements CitaBL {
    private final CitaDAO citaDAO = new CitaDAOImpl();
    private final MascotaDAO mascotaDAO = new MascotaDAOImpl();
    private final VeterinarioDAO veterinarioDAO = new VeterinarioDAOImpl();
    private final AdministradorDAO administradorDAO = new AdministradorDAOImpl();
    private final AtencionDAO atencionDAO = new AtencionDAOImpl();
    private final ServicioDAO servicioDAO = new ServicioDAOImpl();
    private final BoletaDAO boletaDAO = new BoletaDAOImpl();

    private static final int DURACION_POR_DEFECTO_MIN = 30;

    private static final int MARGEN_EMERGENCIA_HORAS = 1;

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
            Cita cita = citaDAO.findById(id);
            if(cita==null){
                throw new NoEncontradoException("No se encontro la cita con el id " + id);
            }
            return cita;
        } catch (SQLException e) {
            throw new BLException("No se pudo recuperar la cita", e);
        }
    }

    @Override
    public List<Cita> findByCliente(Integer idCliente) throws BLException {
        List<Cita> citas = new ArrayList<>();
        for (Cita cita : findAll()) {
            if (cita.getMascota().getCliente().getId() == idCliente) citas.add(cita);
        }
        citas.sort(Comparator.comparing(Cita::getFechaHora));
        return citas;
    }

    @Override
    public List<Cita> findByFecha(LocalDate fecha, Integer idVeterinario) throws BLException {
        List<Cita> citas = new ArrayList<>();
        for (Cita cita : findAll()) {
            if (!cita.getFechaHora().toLocalDate().equals(fecha)) continue;
            if (idVeterinario != null && cita.getDetalles().stream()
                    .noneMatch(d -> d.getVeterinario().getId() == idVeterinario)) continue;
            citas.add(cita);
        }
        citas.sort(Comparator.comparing(Cita::getFechaHora));
        return citas;
    }

    @Override
    public void insert(Cita cita) throws BLException {
        Activos.marcar(cita);
        if (cita != null) Activos.marcarTodos(cita.getDetalles());
        validar(cita);
        validarEstadoInicial(cita);
        validarMascotaExiste(cita);
        validarServiciosActivos(cita);
        validarAgenda(cita, true);

        TransactionsManager.iniciar();
        try {
            citaDAO.insert(cita);
            TransactionsManager.commit();
        } catch (SQLException | RuntimeException e)  {
            TransactionsManager.rollback();
            throw new BLException("No se pudo registrar la cita", e);
        }
    }

    @Override
    public void update(Cita cita) throws BLException {
        Activos.marcar(cita);
        if (cita != null) Activos.marcarTodos(cita.getDetalles());
        validar(cita);
        Cita actual = obtenerExistente(cita.getId());
        if (actual.isEsEmergencia() != cita.isEsEmergencia()) {
            throw new BLException("No se puede cambiar si una cita es de emergencia");
        }
        validarCambioDeEstado(actual, cita);
        validarCambiosPermitidos(actual, cita);
        validarServiciosActivos(cita);
        // RN-CI-06: la fecha solo se exige al reprogramar (completar una cita cuya hora ya paso es valido);
        // el horario y el solapamiento, al reprogramar o al cambiar servicios o veterinarios
        boolean reprogramada = !actual.getFechaHora().equals(cita.getFechaHora());
        if (reprogramada || cambiaronAsignaciones(actual, cita)) {
            validarAgenda(cita, reprogramada);
        }

        TransactionsManager.iniciar();
        try {
            citaDAO.update(cita);
            TransactionsManager.commit();
        } catch (SQLException | RuntimeException e)  {
            TransactionsManager.rollback();
            throw new BLException("No se pudo actualizar la cita", e);
        }
    }

    @Override
    public void delete(Integer id) throws BLException {
        Cita actual = obtenerExistente(id);
        validarSinAtenciones(actual);
        validarSinFacturar(actual);
        TransactionsManager.iniciar();
        try {
            citaDAO.delete(id);
            TransactionsManager.commit();
        } catch (SQLException | RuntimeException e) {
            TransactionsManager.rollback();
            throw new BLException("No se pudo eliminar la cita", e);
        }
    }

    // Fecha, veterinarios, horario y solapamiento (RN-CI-03, RN-CI-05, RN-CI-11, RN-CI-12)
    private void validarAgenda(Cita cita, boolean exigirFecha) throws BLException {
        if (exigirFecha) validarFecha(cita);
        validarVeterinariosActivos(cita);
        // Las emergencias se atienden con quien este disponible: no exigen horario ni revisan cruces
        if (!cita.isEsEmergencia()) {
            validarDisponibilidad(cita);
            validarSinSolapamiento(cita);
        }
    }

    // Solo al crear o reprogramar: no se aplica al actualizar el estado de una cita existente
    private void validarFecha(Cita cita) throws BLException {
        LocalDateTime ahora = LocalDateTime.now();
        if (cita.isEsEmergencia()) {
            // RN-CI-11: la emergencia se registra al llegar el paciente, con hasta 1 hora de margen
            if (cita.getFechaHora().isBefore(ahora.minusHours(MARGEN_EMERGENCIA_HORAS))) {
                throw new BLException("Una cita de emergencia no puede registrarse con más de "
                        + MARGEN_EMERGENCIA_HORAS + " hora de antigüedad");
            }
        } else if (cita.getFechaHora().isBefore(ahora)) {
            throw new BLException("La fecha y hora de la cita no puede ser en el pasado");
        }
    }

    // Detecta si se agregaron, quitaron o cambiaron servicios o veterinarios respecto a lo guardado.
    // Se comparan listas ordenadas (no conjuntos) porque una cita puede incluir varias veces el mismo
    // servicio del catalogo, incluso con el mismo veterinario
    private boolean cambiaronAsignaciones(Cita actual, Cita nueva) {
        return !asignaciones(actual).equals(asignaciones(nueva));
    }

    private List<String> asignaciones(Cita cita) {
        List<String> lista = new ArrayList<>();
        for (DetalleCita d : cita.getDetalles()) lista.add(d.getServicio().getId() + "-" + d.getVeterinario().getId());
        Collections.sort(lista);
        return lista;
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

    // Los servicios que se agregan a la cita deben estar activos en el catalogo. Los que la cita ya
    // tenia (id != 0) se aceptan aunque se hayan dado de baja despues: son historial
    private void validarServiciosActivos(Cita cita) throws BLException {
        try {
            for (DetalleCita detalle : cita.getDetalles()) {
                if (detalle.getId() == 0 && servicioDAO.findById(detalle.getServicio().getId()) == null) {
                    throw new BLException("El servicio " + detalle.getServicio().getId() + " no existe o está dado de baja");
                }
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar los servicios de la cita", e);
        }
    }

    // RN-CI-05: el veterinario de cada servicio debe existir y estar activo
    private void validarVeterinariosActivos(Cita cita) throws BLException {
        try {
            for (DetalleCita detalle : cita.getDetalles()) {
                if (veterinarioDAO.findById(detalle.getVeterinario().getId()) == null) {
                    throw new BLException("El veterinario " + detalle.getVeterinario().getId()
                            + " no existe o está dado de baja");
                }
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar los veterinarios de la cita", e);
        }
    }

    // RN-CI-05: cada veterinario debe atender ese dia durante todo el tiempo de sus servicios
    // (desde la hora de la cita hasta que termina el ultimo, segun su duracion estimada)
    private void validarDisponibilidad(Cita cita) throws BLException {
        LocalDateTime inicio = cita.getFechaHora();
        try {
            for (Map.Entry<Integer, Integer> bloque : minutosPorVeterinario(cita).entrySet()) {
                Veterinario veterinario = veterinarioDAO.findById(bloque.getKey());
                if (!cubre(veterinario.getHorarios(), inicio, bloque.getValue())) {
                    throw new BLException("El veterinario " + veterinario.getNombres() + " " + veterinario.getApellidoPaterno()
                            + " no atiende de " + inicio.toLocalTime() + " a "
                            + inicio.plusMinutes(bloque.getValue()).toLocalTime() + " ese día");
                }
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar la disponibilidad del veterinario", e);
        }
    }

    // RN-CI-12: un veterinario no puede tener servicios que se crucen en citas PENDIENTE o APROBADO
    private void validarSinSolapamiento(Cita cita) throws BLException {
        try {
            Map<Integer, Integer> propios = minutosPorVeterinario(cita);
            LocalDateTime inicio = cita.getFechaHora();
            for (Cita otra : citaDAO.findAll()) {
                if (otra.getId() == cita.getId()) continue;
                if (otra.getEstado() != EstadoCita.PENDIENTE && otra.getEstado() != EstadoCita.APROBADO) continue;
                Map<Integer, Integer> ajenos = minutosPorVeterinario(otra);
                for (Map.Entry<Integer, Integer> bloque : propios.entrySet()) {
                    Integer minutosOtra = ajenos.get(bloque.getKey());
                    if (minutosOtra == null) continue;
                    LocalDateTime fin = inicio.plusMinutes(bloque.getValue());
                    LocalDateTime otraInicio = otra.getFechaHora();
                    LocalDateTime otraFin = otraInicio.plusMinutes(minutosOtra);
                    if (inicio.isBefore(otraFin) && otraInicio.isBefore(fin)) {
                        throw new BLException("El veterinario ya tiene la cita " + otra.getId() + " de "
                                + otraInicio.toLocalTime() + " a " + otraFin.toLocalTime() + " ese día");
                    }
                }
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar la agenda del veterinario", e);
        }
    }

    // RN-CI-15: la mascota no cambia, y un DetalleCita con atencion (activa o anulada) o ya cobrado
    // se conserva con el mismo servicio y veterinario. Los demas pueden quitarse o cambiarse (RN-CI-16)
    private void validarCambiosPermitidos(Cita actual, Cita nueva) throws BLException {
        if (actual.getMascota().getId() != nueva.getMascota().getId()) {
            throw new BLException("No se puede cambiar la mascota de una cita");
        }
        try {
            for (DetalleCita anterior : actual.getDetalles()) {
                boolean atendido = atencionDAO.findByDetalleCita(anterior.getId()) != null;
                boolean cobrado = boletaDAO.boletaConServicio(anterior.getId()) != 0;
                if (!atendido && !cobrado) continue;
                DetalleCita nuevo = nueva.getDetalles().stream()
                        .filter(d -> d.getId() == anterior.getId()).findFirst().orElse(null);
                if (nuevo == null
                        || nuevo.getServicio().getId() != anterior.getServicio().getId()
                        || nuevo.getVeterinario().getId() != anterior.getVeterinario().getId()) {
                    throw new BLException("El servicio " + anterior.getServicio().getNombre() + " ya fue "
                            + (atendido ? "atendido" : "cobrado") + ": no se puede quitar ni cambiar su servicio o veterinario");
                }
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar los servicios atendidos o cobrados de la cita", e);
        }
    }

    // Minutos que cada veterinario ocupa en la cita: sus servicios van uno tras otro desde la hora
    // de la cita, cada uno con su duracion estimada (30 min si el servicio no la tiene)
    private Map<Integer, Integer> minutosPorVeterinario(Cita cita) throws SQLException {
        Map<Integer, Integer> minutos = new LinkedHashMap<>();
        for (DetalleCita detalle : cita.getDetalles()) {
            Servicio servicio = servicioDAO.findByIdHistorico(detalle.getServicio().getId());
            Integer duracion = servicio == null ? null : servicio.getDuracionEstimada();
            minutos.merge(detalle.getVeterinario().getId(),
                    duracion == null ? DURACION_POR_DEFECTO_MIN : duracion, Integer::sum);
        }
        return minutos;
    }

    // true si algun horario cubre todo el tramo [inicio, inicio + minutos] en el mismo dia
    private boolean cubre(List<Horario> horarios, LocalDateTime inicio, int minutos) {
        DiaSemana dia = DiaSemana.values()[inicio.getDayOfWeek().getValue() - 1];
        LocalDateTime fin = inicio.plusMinutes(minutos);
        boolean terminaAMedianoche = fin.toLocalTime().equals(LocalTime.MIDNIGHT);
        if (!terminaAMedianoche && !fin.toLocalDate().equals(inicio.toLocalDate())) return false;
        LocalTime horaInicio = inicio.toLocalTime();
        LocalTime horaFin = terminaAMedianoche ? LocalTime.MAX : fin.toLocalTime();
        return horarios.stream().anyMatch(h ->
                h.getDiaSemana() == dia
                        && !horaInicio.isBefore(h.getHoraInicio())
                        && !horaFin.isAfter(h.getHoraFin()));
    }

    // RN-CI-10: no se elimina una cita con algun servicio ya facturado (la boleta quedaria colgando)
    private void validarSinFacturar(Cita cita) throws BLException {
        try {
            for (DetalleCita detalle : cita.getDetalles()) {
                if (boletaDAO.boletaConServicio(detalle.getId()) != 0) {
                    throw new BLException("No se puede eliminar la cita: el servicio "
                            + detalle.getServicio().getNombre() + " ya fue facturado");
                }
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar la facturación de la cita", e);
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

    private void validarSinAtenciones(Cita cita) throws BLException {
        try {
            for (Atencion atencion : atencionDAO.findAll()) {
                for (DetalleCita detalle : cita.getDetalles()) {
                    if (atencion.getDetalleCita().getId() == detalle.getId()) {
                        throw new BLException("La cita tiene el servicio " + detalle.getServicio().getNombre()
                                + " ya atendido: no se puede eliminar, cancelar ni rechazar");
                    }
                }
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar las atenciones de la cita", e);
        }
    }

    private void validarCambioDeEstado(Cita actual, Cita nueva) throws BLException {
        EstadoCita de = actual.getEstado();
        EstadoCita a = nueva.getEstado();
        if (de == EstadoCita.CANCELADO || de == EstadoCita.RECHAZADO || de == EstadoCita.COMPLETADO) {
            throw new BLException("No se puede modificar una cita en estado " + de);
        }
        if (a != de) {
            boolean valido = (de == EstadoCita.PENDIENTE
                    && (a == EstadoCita.APROBADO || a == EstadoCita.RECHAZADO || a == EstadoCita.CANCELADO))
                    || (de == EstadoCita.APROBADO && (a == EstadoCita.CANCELADO || a == EstadoCita.COMPLETADO));
            if (!valido) {
                throw new BLException("No se puede pasar una cita de " + de + " a " + a);
            }
        }
        if ((a == EstadoCita.APROBADO || a == EstadoCita.RECHAZADO) && nueva.getAdministrador() == null) {
            throw new BLException("Debe indicarse el administrador que aprueba o rechaza la cita");
        }
        // RN-AD-03: quien aprueba o rechaza debe existir y estar activo (solo al tomar la decision:
        // si despues se da de baja, la cita conserva su administrador como historial)
        if (a != de && (a == EstadoCita.APROBADO || a == EstadoCita.RECHAZADO)) {
            try {
                if (administradorDAO.findById(nueva.getAdministrador().getId()) == null) {
                    throw new BLException("El administrador que aprueba o rechaza no existe o está dado de baja");
                }
            } catch (SQLException e) {
                throw new BLException("No se pudo verificar el administrador", e);
            }
        }
        // RECHAZADO tambien: una emergencia PENDIENTE puede tener atenciones antes de aprobarse
        if (a == EstadoCita.CANCELADO || a == EstadoCita.RECHAZADO) {
            validarSinAtenciones(actual);
        }
        if (a == EstadoCita.COMPLETADO) {
            validarTodosAtendidos(actual);
        }
    }

    // Una cita solo se completa cuando cada uno de sus servicios ya tiene su atencion
    private void validarTodosAtendidos(Cita cita) throws BLException {
        try {
            List<Atencion> atenciones = atencionDAO.findAll();
            for (DetalleCita detalle : cita.getDetalles()) {
                boolean atendido = false;
                for (Atencion atencion : atenciones) {
                    if (atencion.getDetalleCita().getId() == detalle.getId()) {
                        atendido = true;
                        break;
                    }
                }
                if (!atendido) {
                    throw new BLException("No se puede completar la cita: el servicio "
                            + detalle.getServicio().getNombre() + " aún no fue atendido");
                }
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar las atenciones de la cita", e);
        }
    }
}
