package model.producto;

public abstract class Producto {
    private int idProducto;
    private String nombre;
    private double precioBase;
    private String descripcion;
    private boolean estado;


    public Producto(int idProducto, String nombre, double precioBase, String descripcion, boolean estado) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.precioBase = precioBase;
        this.descripcion = descripcion;
        this.estado = estado;
    }

    // Getters y Setters
    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(double precioBase) {
        this.precioBase = precioBase;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Producto{" +
                "idProducto=" + idProducto +
                ", nombre='" + nombre + '\'' +
                ", precioBase=" + precioBase +
                ", descripcion='" + descripcion + '\'' +
                ", estado=" + estado +
                '}';
    }
}
