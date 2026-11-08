package patitasarriba.rest.dto;

import java.util.ArrayList;
import java.util.List;

public class CitaDTO {
    private int id;
    private boolean activo;
    private String fechaHora;
    private String estado;
    private MascotaDTO mascota;
    private AdministradorDTO administrador;
    private boolean esEmergencia;
    private List<DetalleCitaDTO> detalles = new ArrayList<>();

    public CitaDTO() {
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

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public MascotaDTO getMascota() {
        return mascota;
    }

    public void setMascota(MascotaDTO mascota) {
        this.mascota = mascota;
    }

    public AdministradorDTO getAdministrador() {
        return administrador;
    }

    public void setAdministrador(AdministradorDTO administrador) {
        this.administrador = administrador;
    }

    public boolean isEsEmergencia() {
        return esEmergencia;
    }

    public void setEsEmergencia(boolean esEmergencia) {
        this.esEmergencia = esEmergencia;
    }

    public List<DetalleCitaDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleCitaDTO> detalles) {
        this.detalles = detalles;
    }
}
