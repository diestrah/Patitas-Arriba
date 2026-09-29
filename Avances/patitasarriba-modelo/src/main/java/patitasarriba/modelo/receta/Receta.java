package patitasarriba.modelo.receta;

import patitasarriba.modelo.Registro;
import patitasarriba.modelo.atencion.AtencionMedica;
import patitasarriba.modelo.producto.Articulo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Receta extends Registro {
    private LocalDate fechaEmision;
    private String indicacionesGenerales;
    private List<DetalleReceta> detalles;
    private AtencionMedica atencionMedica;

    public Receta(){
        this.detalles = new ArrayList<>();
    }

    public Receta(int id, boolean activo, LocalDate fechaEmision, String indicacionesGenerales,
                  List<DetalleReceta> detalles, AtencionMedica atencionMedica) {
        super(id, activo);
        setFechaEmision(fechaEmision);
        setIndicacionesGenerales(indicacionesGenerales);
        setDetalles(detalles);
        setAtencionMedica(atencionMedica);
    }

    // Constructor de copia
    public Receta(final Receta receta) {
        if (receta == null) {
            throw new IllegalArgumentException("receta no puede ser nula");
        }
        super(receta);
        setFechaEmision(receta.getFechaEmision());
        setIndicacionesGenerales(receta.getIndicacionesGenerales());
        setDetalles(receta.getDetalles());
    }

    // Getters y Setters

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

    public List<DetalleReceta> getDetalles() {
        return Collections.unmodifiableList(detalles);
    }

    public void setDetalles(List<DetalleReceta> detalles) {
        if (detalles == null) {
            throw new IllegalArgumentException("detalles no puede ser nulo");
        }
        this.detalles = new ArrayList<>(detalles);
    }

    public void agregarDetalle(int idDetalleReceta, boolean activo, String dosis, String frecuencia, int duracionDias, int cantidadTotal, Articulo producto) {
        detalles.add(new DetalleReceta(idDetalleReceta, activo, dosis, frecuencia, duracionDias, cantidadTotal, producto));
    }

    public AtencionMedica getAtencionMedica() {
        return atencionMedica;
    }

    public void setAtencionMedica(AtencionMedica atencionMedica) {
        this.atencionMedica = atencionMedica;
    }

    @Override
    public String toString() {
        return "Receta{" +
                super.toString() +
                ", fechaEmision=" + fechaEmision +
                ", indicacionesGenerales='" + indicacionesGenerales + '\'' +
                ", detalles=" + detalles +
                '}';
    }
}
