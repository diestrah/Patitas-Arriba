package patitasarriba.modelo.producto;

public abstract class Producto {
    private int idProducto;
    private String nombre;
    private double precioBase;
    private String descripcion;
    private boolean estado;

    public Producto(int idProducto, String nombre, double precioBase, String descripcion, boolean estado) {
        setIdProducto(idProducto);
        setNombre(nombre);
        setPrecioBase(precioBase);
        setDescripcion(descripcion);
        setEstado(estado);
    }

    // Constructor de copia
    protected Producto(Producto producto) {
        if (producto == null) {
            throw new IllegalArgumentException("producto no puede ser nulo");
        }
        setIdProducto(producto.getIdProducto());
        setNombre(producto.getNombre());
        setPrecioBase(producto.getPrecioBase());
        setDescripcion(producto.getDescripcion());
        setEstado(producto.isEstado());
    }

    // getters y setters

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
