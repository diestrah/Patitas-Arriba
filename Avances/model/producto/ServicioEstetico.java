package model.producto;

import model.usuario.Estilista;

public class ServicioEstetico extends Servicio {
    private TipoServicioEstetico tipo;
    private Estilista estilista;


    public ServicioEstetico(int idProducto, String nombre, double precioBase, String descripcion, boolean estado,
                            int duracionEstimada, boolean requiereCita,
                            TipoServicioEstetico tipo, Estilista estilista) {
        super(idProducto, nombre, precioBase, descripcion, estado, duracionEstimada, requiereCita);
        this.tipo = tipo;
        this.estilista = estilista;
    }

    // Getters y Setters
    public TipoServicioEstetico getTipo() {
        return tipo;
    }

    public void setTipo(TipoServicioEstetico tipo) {
        this.tipo = tipo;
    }

    public Estilista getEstilista() {
        return estilista;
    }

    public void setEstilista(Estilista estilista) {
        this.estilista = estilista;
    }

    @Override
    public String toString() {
        return "ServicioEstetico{" +
                "idProducto=" + getIdProducto() +
                ", nombre='" + getNombre() + '\'' +
                ", tipo=" + tipo +
                ", estilista=" + (estilista != null ? estilista.getDni() : null) +
                '}';
    }
}
