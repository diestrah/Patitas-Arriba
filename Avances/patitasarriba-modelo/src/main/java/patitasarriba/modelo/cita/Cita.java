package patitasarriba.modelo.cita;

import patitasarriba.modelo.mascota.Mascota;
import patitasarriba.modelo.producto.Servicio;
import patitasarriba.modelo.usuario.Veterinario;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cita {
    private int idCita;
    private LocalDateTime fechaHora;
    private EstadoCita estado;
    private Mascota mascota;
    private Veterinario veterinario;
    private List<DetalleCita> detalles;

    public Cita(int idCita, LocalDateTime fechaHora, EstadoCita estado, Mascota mascota, Veterinario veterinario, List<DetalleCita> detalles) {
        setIdCita(idCita);
        setFechaHora(fechaHora);
        setEstado(estado);
        setMascota(mascota);
        setVeterinario(veterinario);
        setDetalles(detalles);
    }

    // Constructor de copia
    public Cita(Cita cita) {
        if (cita == null) {
            throw new IllegalArgumentException("cita no puede ser nula");
        }
        setIdCita(cita.getIdCita());
        setFechaHora(cita.getFechaHora());
        setEstado(cita.getEstado());
        setMascota(cita.getMascota());
        setVeterinario(cita.getVeterinario());
        setDetalles(cita.getDetalles());
    }

    // Getters y Setters
    public int getIdCita() {
        return idCita;
    }

    public void setIdCita(int idCita) {
        if (idCita < 0) {
            throw new IllegalArgumentException("idCita no puede ser negativo");
        }
        this.idCita = idCita;
    }

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
        return mascota;
    }

    public void setMascota(Mascota mascota) {
        if (mascota == null) {
            throw new IllegalArgumentException("mascota no puede ser nula");
        }
        this.mascota = mascota;
    }

    public Veterinario getVeterinario() {
        return veterinario;
    }

    public void setVeterinario(Veterinario veterinario) {
        if (veterinario == null) {
            throw new IllegalArgumentException("veterinario no puede ser nulo");
        }
        this.veterinario = veterinario;
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

    public void agregarDetalle(int idDetalleCita, String observaciones, Servicio servicio) {
        detalles.add(new DetalleCita(idDetalleCita, observaciones, servicio));
    }

    @Override
    public String toString() {
        String nombreMascota = null;
        if (mascota != null) {
            nombreMascota = mascota.getNombre();
        }

        String dniPersonalAtencion = null;
        if (veterinario != null) {
            dniPersonalAtencion = veterinario.getDni();
        }

        return "Cita{" +
                "idCita=" + idCita +
                ", fechaHora=" + fechaHora +
                ", estado=" + estado +
                ", mascota=" + nombreMascota +
                ", personalAtencion=" + dniPersonalAtencion +
                ", detalles=" + detalles +
                '}';
    }
}
