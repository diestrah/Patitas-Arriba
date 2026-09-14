package model.producto;

public class ServicioMedico extends Servicio {
    private TipoServicioMedico tipo;
    private boolean requiereTriaje;
    private boolean requiereVacuna;


    public ServicioMedico(int idProducto, String nombre, double precioBase, String descripcion, boolean estado,
                          int duracionEstimada, boolean requiereCita,
                          TipoServicioMedico tipo, boolean requiereTriaje, boolean requiereVacuna) {
        super(idProducto, nombre, precioBase, descripcion, estado, duracionEstimada, requiereCita);
        this.tipo = tipo;
        this.requiereTriaje = requiereTriaje;
        this.requiereVacuna = requiereVacuna;
    }

    // Getters y Setters
    public TipoServicioMedico getTipo() {
        return tipo;
    }

    public void setTipo(TipoServicioMedico tipo) {
        this.tipo = tipo;
    }

    public boolean isRequiereTriaje() {
        return requiereTriaje;
    }

    public void setRequiereTriaje(boolean requiereTriaje) {
        this.requiereTriaje = requiereTriaje;
    }

    public boolean isRequiereVacuna() {
        return requiereVacuna;
    }

    public void setRequiereVacuna(boolean requiereVacuna) {
        this.requiereVacuna = requiereVacuna;
    }

    @Override
    public String toString() {
        return "ServicioMedico{" +
                "idProducto=" + getIdProducto() +
                ", nombre='" + getNombre() + "'" +
                ", tipo=" + tipo +
                ", requiereTriaje=" + requiereTriaje +
                ", requiereVacuna=" + requiereVacuna +
                '}';
    }
}
