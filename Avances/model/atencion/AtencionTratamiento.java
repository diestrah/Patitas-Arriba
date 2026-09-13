package model.atencion;

import model.producto.Articulo;

public class AtencionTratamiento {
    private Tratamiento tratamiento;
    private Articulo articulo;


    public AtencionTratamiento(Tratamiento tratamiento, Articulo articulo) {
        this.tratamiento = tratamiento;
        this.articulo = articulo;
    }

    // Getters y Setters
    public Tratamiento getTratamiento() {
        return tratamiento;
    }

    public void setTratamiento(Tratamiento tratamiento) {
        this.tratamiento = tratamiento;
    }

    public Articulo getArticulo() {
        return articulo;
    }

    public void setArticulo(Articulo articulo) {
        this.articulo = articulo;
    }

    @Override
    public String toString() {
        return "AtencionTratamiento{" +
                "tratamiento=" + tratamiento +
                ", articulo=" + (articulo != null ? articulo.getNombre() : null) +
                '}';
    }
}
