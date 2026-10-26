package patitasarriba.modelo.producto;

import patitasarriba.modelo.Registro;

public class Categoria extends Registro {
    private int idCategoria;
    private String nombre;
    private String descripcion;

    public Categoria() {
    }

    public Categoria(int idCategoria, boolean activo, String nombre, String descripcion) {
        super(activo);
        setIdCategoria(idCategoria);
        setNombre(nombre);
        setDescripcion(descripcion);
    }

    // Constructor de copia
    public Categoria(final Categoria categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("categoria no puede ser nula");
        }
        super(categoria);
        setIdCategoria(categoria.getIdCategoria());
        setNombre(categoria.getNombre());
        setDescripcion(categoria.getDescripcion());
    }

    // Getters y Setters
    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        if (idCategoria < 0) {
            throw new IllegalArgumentException("idCategoria no puede ser negativo");
        }
        this.idCategoria = idCategoria;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("descripcion no puede ser nula o vacía");
        }
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return "Categoria{" +
                "idCategoria=" + idCategoria +
                ", " + super.toString() +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}
