package patitasarriba.modelo.cita;

import patitasarriba.modelo.Registro;
import patitasarriba.modelo.producto.Servicio;
import patitasarriba.modelo.usuario.Veterinario;

public class DetalleCita extends Registro {
    private String observaciones;
    private Servicio servicio;
    private Veterinario veterinario;

    public DetalleCita() {
    }

    public DetalleCita(int id, boolean activo, String observaciones, Servicio servicio, Veterinario veterinario) {
        super(id, activo);
        setObservaciones(observaciones);
        setServicio(servicio);
        setVeterinario(veterinario);
    }

    // Constructor de copia
    public DetalleCita(final DetalleCita detalleCita) {
        if (detalleCita == null) {
            throw new IllegalArgumentException("DetalleCita no puede ser nulo");
        }
        super(detalleCita);
        setObservaciones(detalleCita.getObservaciones());
        setServicio(detalleCita.getServicio());
        setVeterinario(detalleCita.getVeterinario());
    }

    // Getters y Setters

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        // observaciones es opcional: no toda cita necesita una nota adicional
        this.observaciones = observaciones;
    }

    public Servicio getServicio() {
        if (servicio == null) {
            return null;
        }
        return new Servicio(servicio);
    }

    public void setServicio(Servicio servicio) {
        if (servicio == null) {
            throw new IllegalArgumentException("servicio no puede ser nulo");
        }
        this.servicio = new Servicio(servicio);
    }

    public Veterinario getVeterinario() {
        if (veterinario == null) {
            return null;
        }
        return new Veterinario(veterinario);
    }

    public void setVeterinario(Veterinario veterinario) {
        if (veterinario == null) {
            throw new IllegalArgumentException("veterinario no puede ser nulo");
        }
        this.veterinario = new Veterinario(veterinario);
    }

    @Override
    public String toString() {
        String nombreServicio = null;
        if (servicio != null) {
            nombreServicio = servicio.getNombre();
        }

        String dniVeterinario = null;
        if (veterinario != null) {
            dniVeterinario = veterinario.getDni();
        }

        return "DetalleCita{" +
                super.toString() +
                ", observaciones='" + observaciones + '\'' +
                ", servicio=" + nombreServicio +
                ", veterinario=" + dniVeterinario +
                '}';
    }
}
