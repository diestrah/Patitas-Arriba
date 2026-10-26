package patitasarriba.modelo.cita;

import patitasarriba.modelo.Registro;
import patitasarriba.modelo.atencion.Atencion;
import patitasarriba.modelo.producto.Servicio;
import patitasarriba.modelo.usuario.Veterinario;

public class DetalleCita extends Registro {
    private int idDetalleCita;
    private Cita cita;
    private Atencion atencion;
    private String observaciones;
    private Servicio servicio;
    private Veterinario veterinario;

    public DetalleCita() {
    }

    public DetalleCita(int idDetalleCita, boolean activo, String observaciones, Servicio servicio, Veterinario veterinario, Cita cita) {
        super(activo);
        setIdDetalleCita(idDetalleCita);
        setObservaciones(observaciones);
        setServicio(servicio);
        setVeterinario(veterinario);
        setCita(cita);
    }

    // Constructor de copia
    public DetalleCita(final DetalleCita detalleCita) {
        if (detalleCita == null) {
            throw new IllegalArgumentException("DetalleCita no puede ser nulo");
        }
        super(detalleCita);
        setIdDetalleCita(detalleCita.getIdDetalleCita());
        setObservaciones(detalleCita.getObservaciones());
        setServicio(detalleCita.getServicio());
        setVeterinario(detalleCita.getVeterinario());
        setCita(detalleCita.getCita());
        setAtencion(detalleCita.getAtencion());
    }

    // Getters y Setters

    public int getIdDetalleCita() {
        return idDetalleCita;
    }

    public void setIdDetalleCita(int idDetalleCita) {
        if (idDetalleCita < 0) {
            throw new IllegalArgumentException("idDetalleCita no puede ser negativo");
        }
        this.idDetalleCita = idDetalleCita;
    }

    // cita es el contenedor del detalle (composicion): se guarda la referencia, no una copia,
    // para que el detalle siga apuntando a la misma cita que lo contiene
    public Cita getCita() {
        return cita;
    }

    public void setCita(Cita cita) {
        if (cita == null) {
            throw new IllegalArgumentException("cita no puede ser nula");
        }
        this.cita = cita;
    }

    // atencion es el otro lado de la relacion 1 a 1 con Atencion (la FK esta en atencion):
    // es opcional (el servicio aun no se ejecuta) y se guarda la referencia, no una copia,
    // porque Atencion ya copia su detalleCita y copiar en ambos sentidos seria una recursion infinita
    public Atencion getAtencion() {
        return atencion;
    }

    public void setAtencion(Atencion atencion) {
        this.atencion = atencion;
    }

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
        int idCita = -1;
        if (cita != null) {
            idCita = cita.getIdCita();
        }

        int idAtencion = -1;
        if (atencion != null) {
            idAtencion = atencion.getIdAtencion();
        }

        String nombreServicio = null;
        if (servicio != null) {
            nombreServicio = servicio.getNombre();
        }

        String dniVeterinario = null;
        if (veterinario != null) {
            dniVeterinario = veterinario.getDni();
        }

        return "DetalleCita{" +
                "idDetalleCita=" + idDetalleCita +
                ", " + super.toString() +
                ", cita=" + idCita +
                ", atencion=" + idAtencion +
                ", observaciones='" + observaciones + '\'' +
                ", servicio=" + nombreServicio +
                ", veterinario=" + dniVeterinario +
                '}';
    }
}
