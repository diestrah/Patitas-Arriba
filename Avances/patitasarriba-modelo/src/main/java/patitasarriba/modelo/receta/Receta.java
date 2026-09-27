package patitasarriba.modelo.receta;

import patitasarriba.modelo.producto.Articulo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Receta {
    private int idReceta;
    private LocalDate fechaEmision;
    private String indicacionesGenerales;
    private boolean estado;
    private List<DetalleReceta> detalles;

    public Receta(int idReceta, LocalDate fechaEmision, String indicacionesGenerales, boolean estado, List<DetalleReceta> detalles) {
        setIdReceta(idReceta);
        setFechaEmision(fechaEmision);
        setIndicacionesGenerales(indicacionesGenerales);
        setEstado(estado);
        setDetalles(detalles);
    }

    // Constructor de copia
    public Receta(Receta receta) {
        if (receta == null) {
            throw new IllegalArgumentException("receta no puede ser nula");
        }
        setIdReceta(receta.getIdReceta());
        setFechaEmision(receta.getFechaEmision());
        setIndicacionesGenerales(receta.getIndicacionesGenerales());
        setEstado(receta.isEstado());
        setDetalles(receta.getDetalles());
    }

    // Getters y Setters
    public int getIdReceta() {
        return idReceta;
    }

    public void setIdReceta(int idReceta) {
        if (idReceta < 0) {
            throw new IllegalArgumentException("idReceta no puede ser negativo");
        }
        this.idReceta = idReceta;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDate fechaEmision) {
        if (fechaEmision == null) {
            throw new IllegalArgumentException("fechaEmision no puede ser nula");
        }
        this.fechaEmision = fechaEmision;
    }

    public String getIndicacionesGenerales() {
        return indicacionesGenerales;
    }

    public void setIndicacionesGenerales(String indicacionesGenerales) {
        if (indicacionesGenerales == null || indicacionesGenerales.isEmpty()) {
            throw new IllegalArgumentException("indicacionesGenerales no puede ser nulo o vacío");
        }
        this.indicacionesGenerales = indicacionesGenerales;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public List<DetalleReceta> getDetalles() {
        return Collections.unmodifiableList(detalles);
    }

    public void setDetalles(List<DetalleReceta> detalles) {
        if (detalles == null) {
            throw new IllegalArgumentException("detalles no puede ser nulo");
        }
        this.detalles = new ArrayList<>(detalles);
    }

    public void agregarDetalle(int idDetalleReceta, String dosis, String frecuencia, int duracionDias, int cantidadTotal, Articulo producto) {
        detalles.add(new DetalleReceta(idDetalleReceta, dosis, frecuencia, duracionDias, cantidadTotal, producto));
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
