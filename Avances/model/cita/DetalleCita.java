package model.cita;

import model.producto.Servicio;

public class DetalleCita {
    private int idDetalleCita;
    private String observaciones;
    private Servicio servicio;

    public DetalleCita(int idDetalleCita, String observaciones, Servicio servicio) {
        this.idDetalleCita = idDetalleCita;
        this.observaciones = observaciones;
        this.servicio = servicio;
    }

    // Getters y Setters
    public int getIdDetalleCita() {
        return idDetalleCita;
    }

    public void setIdDetalleCita(int idDetalleCita) {
        this.idDetalleCita = idDetalleCita;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Servicio getServicio() {
        return servicio;
    }

    public void setServicio(Servicio servicio) {
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
