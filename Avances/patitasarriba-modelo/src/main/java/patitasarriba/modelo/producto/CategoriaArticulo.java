package patitasarriba.modelo.producto;

import patitasarriba.modelo.Registro;

public class CategoriaArticulo extends Registro {
    private String nombre;
    private String descripcion;

    public CategoriaArticulo() {
    }

    public CategoriaArticulo(int id, boolean activo, String nombre, String descripcion) {
        super(id, activo);
        setNombre(nombre);
        setDescripcion(descripcion);
    }

    // Constructor de copia
    public CategoriaArticulo(final CategoriaArticulo categoriaArticulo) {
        if (categoriaArticulo == null) {
            throw new IllegalArgumentException("categoriaArticulo no puede ser nulo");
        }
        super(categoriaArticulo);
        setNombre(categoriaArticulo.getNombre());
        setDescripcion(categoriaArticulo.getDescripcion());
    }

    // setter y getters

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede ser nulo o vacío");
        }
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        if(descripcion == null || descripcion.isEmpty()){
            throw new IllegalArgumentException("El descripcion no puede ser nula o vacía");
        }
        this.descripcion = descripcion;
    }
}
