package model.cita;

import model.mascota.Mascota;

import java.time.LocalDateTime;
import java.util.List;

public class Cita {
    private int idCita;
    private LocalDateTime fechaHora;
    private EstadoCita estado;
    private Mascota mascota;
    private List<DetalleCita> detalles;

    public Cita(int idCita, LocalDateTime fechaHora, EstadoCita estado, Mascota mascota, List<DetalleCita> detalles) {
        this.idCita = idCita;
        this.fechaHora = fechaHora;
        this.estado = estado;
        this.mascota = mascota;
        this.detalles = detalles;
    }

    // Getters y Setters
    public int getIdCita() {
        return idCita;
    }

    public void setIdCita(int idCita) {
        this.idCita = idCita;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public EstadoCita getEstado() {
        return estado;
    }

    public void setEstado(EstadoCita estado) {
        this.estado = estado;
    }

    public Mascota getMascota() {
        return mascota;
    }

    public void setMascota(Mascota mascota) {
        this.mascota = mascota;
    }

    public List<DetalleCita> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleCita> detalles) {
        this.detalles = detalles;
    }

    @Override
    public String toString() {
        return "Cita{" +
                "idCita=" + idCita +
                ", fechaHora=" + fechaHora +
                ", estado=" + estado +
                ", mascota=" + (mascota != null ? mascota.getNombre() : null) +
                ", detalles=" + detalles +
                '}';
    }
}
