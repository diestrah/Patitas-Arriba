package patitasarriba.modelo.producto;

import patitasarriba.modelo.Registro;

public abstract class Producto extends Registro {
    private int idProducto;
    private String nombre;
    private double precioBase;
    private String descripcion;

    public Producto() {
    }

    public Producto(int idProducto, boolean activo, String nombre, double precioBase, String descripcion) {
        super(activo);
        setIdProducto(idProducto);
        setNombre(nombre);
        setPrecioBase(precioBase);
        setDescripcion(descripcion);
    }

    // Constructor de copia
    public Producto(final Producto producto) {
        if (producto == null) {
            throw new IllegalArgumentException("producto no puede ser nulo");
        }
        super(producto);
        setIdProducto(producto.getIdProducto());
        setNombre(producto.getNombre());
        setPrecioBase(producto.getPrecioBase());
        setDescripcion(producto.getDescripcion());
    }

    // Getters y Setters
    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        if (idProducto < 0) {
            throw new IllegalArgumentException("idProducto no puede ser negativo");
        }
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("nombre no puede ser nulo o vacío");
        }
        this.nombre = nombre;
    }

    public double getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(double precioBase) {
        if (precioBase < 0) {
            throw new IllegalArgumentException("precioBase no puede ser negativo");
        }
        this.precioBase = precioBase;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        // descripcion es opcional
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return "idProducto=" + idProducto +
                ", " + super.toString() +
                ", nombre='" + nombre + '\'' +
                ", precioBase=" + precioBase +
                ", descripcion='" + descripcion + '\'';
    }
}
