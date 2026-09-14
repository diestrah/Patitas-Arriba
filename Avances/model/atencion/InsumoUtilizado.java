package model.atencion;

import model.producto.Articulo;

public class InsumoUtilizado {
    private Articulo articulo;
    private int cantidadUtilizada;
    private String unidadMedida;

    public InsumoUtilizado(Articulo articulo, int cantidadUtilizada, String unidadMedida) {
        this.articulo = articulo;
        this.cantidadUtilizada = cantidadUtilizada;
        this.unidadMedida = unidadMedida;
    }

    // Getters y Setters
    public Articulo getArticulo() {
        return articulo;
    }

    public void setArticulo(Articulo articulo) {
        this.articulo = articulo;
    }

    public int getCantidadUtilizada() {
        return cantidadUtilizada;
    }

    public void setCantidadUtilizada(int cantidadUtilizada) {
        this.cantidadUtilizada = cantidadUtilizada;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    @Override
    public String toString() {
        String nombreArticulo = null;
        if (articulo != null) {
            nombreArticulo = articulo.getNombre();
        }

        return "InsumoUtilizado{" +
                "articulo=" + nombreArticulo +
                ", cantidadUtilizada=" + cantidadUtilizada +
                ", unidadMedida='" + unidadMedida + "'" +
                '}';
    }
}
