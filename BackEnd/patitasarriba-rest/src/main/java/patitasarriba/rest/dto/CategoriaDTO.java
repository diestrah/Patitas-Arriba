package patitasarriba.rest.dto;

public class CategoriaDTO {
    private int id;
    // Si el JSON no trae "activo", queda en true (igual que el modelo Registro)
    private boolean activo = true;
    private String nombre;
    private String descripcion;

    public CategoriaDTO() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
