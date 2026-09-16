package model.cita;

import model.mascota.Mascota;
import model.producto.Servicio;
import model.usuario.PersonalAtencion;

import java.time.LocalDateTime;
import java.util.List;

public class Cita {
    private int idCita;
    private LocalDateTime fechaHora;
    private EstadoCita estado;
    private Mascota mascota;
    private PersonalAtencion personalAtencion;
    private List<DetalleCita> detalles;

    public Cita(int idCita, LocalDateTime fechaHora, EstadoCita estado, Mascota mascota, PersonalAtencion personalAtencion, List<DetalleCita> detalles) {
        this.idCita = idCita;
        this.fechaHora = fechaHora;
        this.estado = estado;
        this.mascota = mascota;
        this.personalAtencion = personalAtencion;
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

    public PersonalAtencion getPersonalAtencion() {
        return personalAtencion;
    }

    public void setPersonalAtencion(PersonalAtencion personalAtencion) {
        this.personalAtencion = personalAtencion;
    }

    public List<DetalleCita> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleCita> detalles) {
        this.detalles = detalles;
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
        if (personalAtencion != null) {
            dniPersonalAtencion = personalAtencion.getDni();
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
