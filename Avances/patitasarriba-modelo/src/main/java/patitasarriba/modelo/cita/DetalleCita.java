package patitasarriba.modelo.cita;

import patitasarriba.modelo.Registro;
import patitasarriba.modelo.producto.Servicio;

public class DetalleCita extends Registro {
    private String observaciones;
    private Servicio servicio;

    public DetalleCita() {
    }

    public DetalleCita(int id, boolean activo, String observaciones, Servicio servicio) {
        super(id, activo);
        setObservaciones(observaciones);
        setServicio(servicio);
    }

    // Constructor de copia
    public DetalleCita(final DetalleCita detalleCita) {
        if (detalleCita == null) {
            throw new IllegalArgumentException("DetalleCita no puede ser nulo");
        }
        super(detalleCita);
        setObservaciones(detalleCita.getObservaciones());
        setServicio(detalleCita.getServicio());
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
        return servicio;
    }

    public void setServicio(Servicio servicio) {
        if (servicio == null) {
            throw new IllegalArgumentException("servicio no puede ser nulo");
        }
        this.servicio = servicio;
    }

    @Override
    public String toString() {
        return "DetalleCita{" +
                super.toString() +
                ", observaciones='" + observaciones + '\'' +
                ", servicio=" + servicio +
                '}';
    }
}
