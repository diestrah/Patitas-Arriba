package model.producto;

public abstract class Servicio extends Producto {
    private int duracionEstimada;
    private boolean requiereCita;


    public Servicio(int idProducto, String nombre, double precioBase, String descripcion, boolean estado,
                    int duracionEstimada, boolean requiereCita) {
        super(idProducto, nombre, precioBase, descripcion, estado);
        this.duracionEstimada = duracionEstimada;
        this.requiereCita = requiereCita;
    }

    // Getters y Setters
    public int getDuracionEstimada() {
        return duracionEstimada;
    }

    public void setDuracionEstimada(int duracionEstimada) {
        this.duracionEstimada = duracionEstimada;
    }

    public boolean isRequiereCita() {
        return requiereCita;
    }

    public void setRequiereCita(boolean requiereCita) {
        this.requiereCita = requiereCita;
    }

    @Override
    public String toString() {
        return "Servicio{" +
                "idProducto=" + getIdProducto() +
                ", nombre='" + getNombre() + '\'' +
                ", duracionEstimada=" + duracionEstimada +
                ", requiereCita=" + requiereCita +
                '}';
    }
}
