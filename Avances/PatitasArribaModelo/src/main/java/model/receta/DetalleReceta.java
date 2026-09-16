package model.receta;

import model.producto.Articulo;

public class DetalleReceta {
    private int idDetalleReceta;
    private String dosis;
    private String frecuencia;
    private int duracionDias;
    private int cantidadTotal;
    private Articulo producto;


    public DetalleReceta(int idDetalleReceta, String dosis, String frecuencia, int duracionDias, int cantidadTotal, Articulo producto) {
        this.idDetalleReceta = idDetalleReceta;
        this.dosis = dosis;
        this.frecuencia = frecuencia;
        this.duracionDias = duracionDias;
        this.cantidadTotal = cantidadTotal;
        this.producto = producto;
    }

    // Getters y Setters
    public int getIdDetalleReceta() {
        return idDetalleReceta;
    }

    public void setIdDetalleReceta(int idDetalleReceta) {
        this.idDetalleReceta = idDetalleReceta;
    }

    public String getDosis() {
        return dosis;
    }

    public void setDosis(String dosis) {
        this.dosis = dosis;
    }

    public String getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(String frecuencia) {
        this.frecuencia = frecuencia;
    }

    public int getDuracionDias() {
        return duracionDias;
    }

    public void setDuracionDias(int duracionDias) {
        this.duracionDias = duracionDias;
    }

    public int getCantidadTotal() {
        return cantidadTotal;
    }

    public void setCantidadTotal(int cantidadTotal) {
        this.cantidadTotal = cantidadTotal;
    }

    public Articulo getProducto() {
        return producto;
    }

    public void setProducto(Articulo producto) {
        this.producto = producto;
    }

    @Override
    public String toString() {
        return "DetalleReceta{" +
                "idDetalleReceta=" + idDetalleReceta +
                ", dosis='" + dosis + '\'' +
                ", frecuencia='" + frecuencia + '\'' +
                ", duracionDias=" + duracionDias +
                ", cantidadTotal=" + cantidadTotal +
                ", producto=" + producto +
                '}';
    }
}
