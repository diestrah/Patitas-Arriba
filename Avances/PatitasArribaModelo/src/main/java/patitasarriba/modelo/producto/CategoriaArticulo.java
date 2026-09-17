package patitasarriba.modelo.producto;

public class CategoriaArticulo {
    private int idCategoriaArticulo;
    private String nombre;
    private String descripcion;

    public CategoriaArticulo(int idCategoriaArticulo, String nombre, String descripcion) {
        this.idCategoriaArticulo = idCategoriaArticulo;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    // setter y getters

    public int getIdCategoriaArticulo() {
        return idCategoriaArticulo;
    }

    public void setIdCategoriaArticulo(int idCategoriaArticulo) {
        this.idCategoriaArticulo = idCategoriaArticulo;
    }

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
