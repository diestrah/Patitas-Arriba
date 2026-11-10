package patitasarriba.rest.dto;

public class DetalleCitaDTO {
    private int id;
    // Si el JSON no trae "activo", queda en true (igual que el modelo Registro)
    private boolean activo = true;
    private String observaciones;
    private ServicioDTO servicio;
    private VeterinarioDTO veterinario;

    public DetalleCitaDTO() {
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

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public ServicioDTO getServicio() {
        return servicio;
    }

    public void setServicio(ServicioDTO servicio) {
        this.servicio = servicio;
    }

    public VeterinarioDTO getVeterinario() {
        return veterinario;
    }

    public void setVeterinario(VeterinarioDTO veterinario) {
        this.veterinario = veterinario;
    }
}
