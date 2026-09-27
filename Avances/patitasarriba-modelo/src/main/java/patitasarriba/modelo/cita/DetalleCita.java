package patitasarriba.modelo.cita;

import patitasarriba.modelo.producto.Servicio;

public class DetalleCita {
    private int idDetalleCita;
    private String observaciones;
    private Servicio servicio;

    public DetalleCita(int idDetalleCita, String observaciones, Servicio servicio) {
        setIdDetalleCita(idDetalleCita);
        setObservaciones(observaciones);
        setServicio(servicio);
    }

    // Constructor de copia
    public DetalleCita(DetalleCita detalleCita) {
        if (detalleCita == null) {
            throw new IllegalArgumentException("detalleCita no puede ser nulo");
        }
        setIdDetalleCita(detalleCita.getIdDetalleCita());
        setObservaciones(detalleCita.getObservaciones());
        setServicio(detalleCita.getServicio());
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
                "idDetalleCita=" + idDetalleCita +
                ", observaciones='" + observaciones + '\'' +
                ", servicio=" + servicio +
                '}';
    }
}
