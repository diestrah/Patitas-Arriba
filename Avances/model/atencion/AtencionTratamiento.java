package model.atencion;

import model.producto.Articulo;

public class AtencionTratamiento {
    private String nombreResponsable;
    private Tratamiento tratamiento;
    private Articulo articulo;


    public AtencionTratamiento(String nombreResponsable, Tratamiento tratamiento, Articulo articulo) {
        this.nombreResponsable = nombreResponsable;
        this.tratamiento = tratamiento;
        this.articulo = articulo;
    }

    // Getters y Setters
    public String getNombreResponsable() {
        return nombreResponsable;
    }

    public void setNombreResponsable(String nombreResponsable) {
        this.nombreResponsable = nombreResponsable;
    }

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
                "nombreResponsable='" + nombreResponsable + '\'' +
                ", tratamiento=" + tratamiento +
                ", articulo=" + (articulo != null ? articulo.getNombre() : null) +
                '}';
    }
}
