package patitasarriba.modelo.receta;

import patitasarriba.modelo.Registro;
import patitasarriba.modelo.atencion.Atencion;
import patitasarriba.modelo.producto.Articulo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Receta extends Registro {
    private int idReceta;
    private Atencion atencion;
    private List<DetalleReceta> detalles;
    private LocalDate fechaEmision;
    private String indicacionesGenerales;

    public Receta() {
        this.detalles = new ArrayList<>();
    }

    public Receta(int idReceta, boolean activo, Atencion atencion, List<DetalleReceta> detalles,
                  LocalDate fechaEmision, String indicacionesGenerales) {
        super(activo);
        setIdReceta(idReceta);
        setAtencion(atencion);
        setDetalles(detalles);
        setFechaEmision(fechaEmision);
        setIndicacionesGenerales(indicacionesGenerales);
    }

    // Constructor de copia
    public Receta(final Receta receta) {
        if (receta == null) {
            throw new IllegalArgumentException("receta no puede ser nula");
        }
        super(receta);
        setIdReceta(receta.getIdReceta());
        setAtencion(receta.getAtencion());
        setDetalles(receta.getDetalles());
        setFechaEmision(receta.getFechaEmision());
        setIndicacionesGenerales(receta.getIndicacionesGenerales());
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

    public Atencion getAtencion() {
        if (atencion == null) {
            return null;
        }
        return new Atencion(atencion);
    }

    public void setAtencion(Atencion atencion) {
        // Que la atencion sea de un servicio medico lo valida la capa de negocio
        if (atencion == null) {
            throw new IllegalArgumentException("atencion no puede ser nula");
        }
        this.atencion = new Atencion(atencion);
    }

    public List<DetalleReceta> getDetalles() {
        return Collections.unmodifiableList(detalles);
    }

    public void setDetalles(List<DetalleReceta> detalles) {
        if (detalles == null) {
            throw new IllegalArgumentException("detalles no puede ser nulo");
        }
        for (DetalleReceta detalle : detalles) {
            if (detalle == null) {
                throw new IllegalArgumentException("detalles no puede contener elementos nulos");
            }
        }
        this.detalles = new ArrayList<>(detalles);
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
        // indicacionesGenerales es opcional
        this.indicacionesGenerales = indicacionesGenerales;
    }

    public void agregarDetalle(int idDetalleReceta, boolean activo, Articulo articulo, String dosis, String frecuencia,
                               int duracionDias, int cantidadTotal) {
        detalles.add(new DetalleReceta(idDetalleReceta, activo, this, articulo, dosis, frecuencia, duracionDias,
                cantidadTotal));
    }

    @Override
    public String toString() {
        int idAtencion = -1;
        if (atencion != null) {
            idAtencion = atencion.getIdAtencion();
        }

        return "Receta{" +
                "idReceta=" + idReceta +
                ", " + super.toString() +
                ", atencion=" + idAtencion +
                ", fechaEmision=" + fechaEmision +
                ", indicacionesGenerales='" + indicacionesGenerales + '\'' +
                ", detalles=" + detalles +
                '}';
    }
}
