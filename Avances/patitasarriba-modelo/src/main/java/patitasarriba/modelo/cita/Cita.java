package patitasarriba.modelo.cita;

import patitasarriba.modelo.Registro;
import patitasarriba.modelo.mascota.Mascota;
import patitasarriba.modelo.producto.Servicio;
import patitasarriba.modelo.usuario.Administrador;
import patitasarriba.modelo.usuario.Veterinario;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cita extends Registro {
    private LocalDateTime fechaHora;
    private EstadoCita estado;
    private Mascota mascota;
    private Administrador administrador;
    private boolean emergencia;
    private List<DetalleCita> detalles;

    public Cita(){
        this.detalles = new ArrayList<>();
    }

    public Cita(int id, boolean activo, LocalDateTime fechaHora, EstadoCita estado, Mascota mascota, Administrador administrador, boolean emergencia, List<DetalleCita> detalles) {
        super(id, activo);
        setFechaHora(fechaHora);
        setEstado(estado);
        setMascota(mascota);
        setAdministrador(administrador);
        setEmergencia(emergencia);
        setDetalles(detalles);
    }

    // Constructor de copia
    public Cita(final Cita cita) {
        if (cita == null) {
            throw new IllegalArgumentException("Cita no puede ser nula");
        }
        super(cita);
        setFechaHora(cita.getFechaHora());
        setEstado(cita.getEstado());
        setMascota(cita.getMascota());
        setAdministrador(cita.getAdministrador());
        setEmergencia(cita.isEmergencia());
        setDetalles(cita.getDetalles());
    }

    // Getters y Setters

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        if (fechaHora == null) {
            throw new IllegalArgumentException("fechaHora no puede ser nula");
        }
        this.fechaHora = fechaHora;
    }

    public EstadoCita getEstado() {
        return estado;
    }

    public void setEstado(EstadoCita estado) {
        if (estado == null) {
            throw new IllegalArgumentException("estado no puede ser nulo");
        }
        this.estado = estado;
    }

    public Mascota getMascota() {
        if (mascota == null) {
            return null;
        }
        return new Mascota(mascota);
    }

    public void setMascota(Mascota mascota) {
        if (mascota == null) {
            throw new IllegalArgumentException("mascota no puede ser nula");
        }
        this.mascota = new Mascota(mascota);
    }

    public Administrador getAdministrador() {
        if (administrador == null) {
            return null;
        }
        return new Administrador(administrador);
    }

    public void setAdministrador(Administrador administrador) {
        // administrador es opcional: lo asigna quien aprueba o rechaza la cita
        if (administrador == null) {
            this.administrador = null;
        } else {
            this.administrador = new Administrador(administrador);
        }
    }

    public boolean isEmergencia() {
        return emergencia;
    }

    public void setEmergencia(boolean emergencia) {
        this.emergencia = emergencia;
    }

    public List<DetalleCita> getDetalles() {
        return Collections.unmodifiableList(detalles);
    }

    public void setDetalles(List<DetalleCita> detalles) {
        if (detalles == null) {
            throw new IllegalArgumentException("detalles no puede ser nulo");
        }
        this.detalles = new ArrayList<>(detalles);
    }

    public void agregarDetalle(int id, boolean activo, String observaciones, Servicio servicio, Veterinario veterinario) {
        detalles.add(new DetalleCita(id, activo, observaciones, servicio, veterinario));
    }

    @Override
    public String toString() {
        String nombreMascota = null;
        if (mascota != null) {
            nombreMascota = mascota.getNombre();
        }

        String dniAdministrador = null;
        if (administrador != null) {
            dniAdministrador = administrador.getDni();
        }

        return "Cita{" +
                super.toString() +
                ", fechaHora=" + fechaHora +
                ", estado=" + estado +
                ", mascota=" + nombreMascota +
                ", administrador=" + dniAdministrador +
                ", emergencia=" + emergencia +
                ", detalles=" + detalles +
                '}';
    }
}
