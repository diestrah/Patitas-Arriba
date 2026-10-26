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
    private int idCita;
    private LocalDateTime fechaHora;
    private EstadoCita estado;
    private Mascota mascota;
    private Administrador administrador;
    private boolean esEmergencia;
    private List<DetalleCita> detalles;

    public Cita(){
        this.detalles = new ArrayList<>();
    }


    public Cita(int idCita, boolean activo, LocalDateTime fechaHora, EstadoCita estado, Mascota mascota, Administrador administrador, boolean esEmergencia, List<DetalleCita> detalles) {
        super(activo);
        setIdCita(idCita);
        setFechaHora(fechaHora);
        setEstado(estado);
        setMascota(mascota);
        setAdministrador(administrador);
        setEsEmergencia(esEmergencia);
        setDetalles(detalles);
    }

    // Constructor de copia
    public Cita(final Cita cita) {
        if (cita == null) {
            throw new IllegalArgumentException("Cita no puede ser nula");
        }
        super(cita);
        setIdCita(cita.getIdCita());
        setFechaHora(cita.getFechaHora());
        setEstado(cita.getEstado());
        setMascota(cita.getMascota());
        setAdministrador(cita.getAdministrador());

        setEsEmergencia(cita.isEsEmergencia());

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

    public boolean isEsEmergencia() {
        return esEmergencia;
    }

    public void setEsEmergencia(boolean esEmergencia) {
        this.esEmergencia = esEmergencia;
    }

    public List<DetalleCita> getDetalles() {
        return Collections.unmodifiableList(detalles);
    }

    public void setDetalles(List<DetalleCita> detalles) {
        if (detalles == null) {
            throw new IllegalArgumentException("detalles no puede ser nulo");
        }
        for (DetalleCita detalle : detalles) {
            if (detalle == null) {
                throw new IllegalArgumentException("detalles no puede contener elementos nulos");
            }
        }
        this.detalles = new ArrayList<>(detalles);
    }

    public void agregarDetalle(int idDetalleCita, boolean activo, String observaciones, Servicio servicio, Veterinario veterinario) {
        detalles.add(new DetalleCita(idDetalleCita, activo, observaciones, servicio, veterinario, this));
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
                "idCita=" + idCita +
                ", " + super.toString() +
                ", fechaHora=" + fechaHora +
                ", estado=" + estado +
                ", mascota=" + nombreMascota +
                ", administrador=" + dniAdministrador +
                ", esEmergencia=" + esEmergencia +
                ", detalles=" + detalles +
                '}';
    }
}
