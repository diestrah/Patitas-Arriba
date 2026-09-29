package patitasarriba.modelo.producto;

import patitasarriba.modelo.Registro;

public abstract class Producto extends Registro {
    private String nombre;
    private double precioBase;
    private String descripcion;

    public Producto() {
    }

    public Producto(int id, boolean activo, String nombre, double precioBase, String descripcion) {
        super(id, activo);
        setNombre(nombre);
        setPrecioBase(precioBase);
        setDescripcion(descripcion);
    }

    // Constructor de copia
    protected Producto(final Producto producto) {
        if (producto == null) {
            throw new IllegalArgumentException("producto no puede ser nulo");
        }
        super(producto);
        setNombre(producto.getNombre());
        setPrecioBase(producto.getPrecioBase());
        setDescripcion(producto.getDescripcion());
    }

    // getters y setters

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isEmpty()) {
            throw new IllegalArgumentException("nombre no puede ser nulo o vacío");
        }
        this.nombre = nombre;
    }

    public double getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(double precioBase) {
        if (precioBase < 0) {
            throw new IllegalArgumentException("precio no puede ser negativo");
        }
        this.precioBase = precioBase;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        if (descripcion == null || descripcion.isEmpty()) {
            throw new IllegalArgumentException("descripcion no puede ser nula o vacía");
        }
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return "Producto{" +
                super.toString() +
                ", nombre='" + nombre + '\'' +
                ", precioBase=" + precioBase +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}
