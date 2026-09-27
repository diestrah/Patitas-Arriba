package patitasarriba.modelo.receta;

import patitasarriba.modelo.Registro;
import patitasarriba.modelo.producto.Articulo;

public class DetalleReceta extends Registro {
    private String dosis;
    private String frecuencia;
    private int duracionDias;
    private int cantidadTotal;
    private Articulo producto;

    public DetalleReceta() {
        // Constructor vacío
    }

    public DetalleReceta(int id, boolean activo, String dosis, String frecuencia, int duracionDias, int cantidadTotal, Articulo producto) {
        super(id, activo);
        setDosis(dosis);
        setFrecuencia(frecuencia);
        setDuracionDias(duracionDias);
        setCantidadTotal(cantidadTotal);
        setProducto(producto);
    }

    // Constructor de copia
    public DetalleReceta(final DetalleReceta detalleReceta) {
        if (detalleReceta == null) {
            throw new IllegalArgumentException("DetalleReceta no puede ser nulo");
        }
        super(detalleReceta);
        setDosis(detalleReceta.getDosis());
        setFrecuencia(detalleReceta.getFrecuencia());
        setDuracionDias(detalleReceta.getDuracionDias());
        setCantidadTotal(detalleReceta.getCantidadTotal());
        setProducto(detalleReceta.getProducto());
    }

    // Getters y Setters
    public String getDosis() {
        return dosis;
    }

    public void setDosis(String dosis) {
        if (dosis == null || dosis.isEmpty()) {
            throw new IllegalArgumentException("dosis no puede ser nulo o vacío");
        }
        this.dosis = dosis;
    }

    public String getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(String frecuencia) {
        if (frecuencia == null || frecuencia.isEmpty()) {
            throw new IllegalArgumentException("frecuencia no puede ser nulo o vacío");
        }
        this.frecuencia = frecuencia;
    }

    public int getDuracionDias() {
        return duracionDias;
    }

    public void setDuracionDias(int duracionDias) {
        if (duracionDias <= 0) {
            throw new IllegalArgumentException("duracionDias debe ser mayor que 0");
        }
        this.duracionDias = duracionDias;
    }

    public int getCantidadTotal() {
        return cantidadTotal;
    }

    public void setCantidadTotal(int cantidadTotal) {
        if (cantidadTotal <= 0) {
            throw new IllegalArgumentException("cantidadTotal debe ser mayor que 0");
        }
        this.cantidadTotal = cantidadTotal;
    }

    public Articulo getProducto() {
        return producto;
    }

    public void setProducto(Articulo producto) {
        if (producto == null) {
            throw new IllegalArgumentException("producto no puede ser nulo");
        }
        this.producto = producto;
    }

    @Override
    public String toString() {
        return "DetalleReceta{" +
                super.toString() +
                ", dosis='" + dosis + '\'' +
                ", frecuencia='" + frecuencia + '\'' +
                ", duracionDias=" + duracionDias +
                ", cantidadTotal=" + cantidadTotal +
                ", producto=" + producto +
                '}';
    }
}
