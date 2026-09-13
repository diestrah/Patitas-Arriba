package model.producto;

import model.usuario.Veterinario;

public class ServicioMedico extends Servicio {
    private TipoServicioMedico tipo;
    private boolean requiereTriaje;
    private boolean requiereVacuna;
    private Veterinario veterinario;


    public ServicioMedico(int idProducto, String nombre, double precioBase, String descripcion, boolean estado,
                          int duracionEstimada, boolean requiereCita,
                          TipoServicioMedico tipo, boolean requiereTriaje, boolean requiereVacuna, Veterinario veterinario) {
        super(idProducto, nombre, precioBase, descripcion, estado, duracionEstimada, requiereCita);
        this.tipo = tipo;
        this.requiereTriaje = requiereTriaje;
        this.requiereVacuna = requiereVacuna;
        this.veterinario = veterinario;
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

    public Veterinario getVeterinario() {
        return veterinario;
    }

    public void setVeterinario(Veterinario veterinario) {
        this.veterinario = veterinario;
    }

    @Override
    public String toString() {
        return "ServicioMedico{" +
                "idProducto=" + getIdProducto() +
                ", nombre='" + getNombre() + '\'' +
                ", tipo=" + tipo +
                ", requiereTriaje=" + requiereTriaje +
                ", requiereVacuna=" + requiereVacuna +
                ", veterinario=" + (veterinario != null ? veterinario.getDni() : null) +
                '}';
    }
}
