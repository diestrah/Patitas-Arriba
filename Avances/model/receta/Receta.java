package model.receta;

import java.time.LocalDate;
import java.util.List;

public class Receta {
    private int idReceta;
    private LocalDate fechaEmision;
    private String indicacionesGenerales;
    private boolean estado;
    private List<DetalleReceta> detalles;

    public Receta(int idReceta, LocalDate fechaEmision, String indicacionesGenerales, boolean estado, List<DetalleReceta> detalles) {
        this.idReceta = idReceta;
        this.fechaEmision = fechaEmision;
        this.indicacionesGenerales = indicacionesGenerales;
        this.estado = estado;
        this.detalles = detalles;
    }

    // Getters y Setters
    public int getIdReceta() {
        return idReceta;
    }

    public void setIdReceta(int idReceta) {
        this.idReceta = idReceta;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public String getIndicacionesGenerales() {
        return indicacionesGenerales;
    }

    public void setIndicacionesGenerales(String indicacionesGenerales) {
        this.indicacionesGenerales = indicacionesGenerales;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public List<DetalleReceta> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleReceta> detalles) {
        this.detalles = detalles;
    }

    @Override
    public String toString() {
        return "Receta{" +
                "idReceta=" + idReceta +
                ", fechaEmision=" + fechaEmision +
                ", indicacionesGenerales='" + indicacionesGenerales + '\'' +
                ", estado=" + estado +
                ", detalles=" + detalles +
                '}';
    }
}
