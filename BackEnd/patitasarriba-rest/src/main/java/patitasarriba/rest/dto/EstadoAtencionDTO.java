package patitasarriba.rest.dto;

// GET /atenciones/estados: de que servicio de cita es cada atencion y si sigue activa (false: anulada)
public class EstadoAtencionDTO {
    private int idAtencion;
    private int idDetalleCita;
    private boolean activo;

    public EstadoAtencionDTO() {
    }

    public int getIdAtencion() {
        return idAtencion;
    }

    public void setIdAtencion(int idAtencion) {
        this.idAtencion = idAtencion;
    }

    public int getIdDetalleCita() {
        return idDetalleCita;
    }

    public void setIdDetalleCita(int idDetalleCita) {
        this.idDetalleCita = idDetalleCita;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
