package model.producto;

public class ServicioEstetico extends Servicio {
    private TipoServicioEstetico tipo;


    public ServicioEstetico(int idProducto, String nombre, double precioBase, String descripcion, boolean estado,
                            int duracionEstimada, boolean requiereCita,
                            TipoServicioEstetico tipo) {
        super(idProducto, nombre, precioBase, descripcion, estado, duracionEstimada, requiereCita);
        this.tipo = tipo;
    }

    // Getters y Setters
    public TipoServicioEstetico getTipo() {
        return tipo;
    }

    public void setTipo(TipoServicioEstetico tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "ServicioEstetico{" +
                "idProducto=" + getIdProducto() +
                ", nombre='" + getNombre() + "'" +
                ", tipo=" + tipo +
                '}';
    }
}
