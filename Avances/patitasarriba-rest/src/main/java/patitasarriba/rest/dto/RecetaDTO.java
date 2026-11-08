package patitasarriba.rest.dto;

import java.util.ArrayList;
import java.util.List;

public class RecetaDTO {
    private int id;
    private boolean activo;
    private AtencionDTO atencion;
    private String fechaEmision;
    private String indicacionesGenerales;
    private List<DetalleRecetaDTO> detalles = new ArrayList<>();

    public RecetaDTO() {
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

    public AtencionDTO getAtencion() {
        return atencion;
    }

    public void setAtencion(AtencionDTO atencion) {
        this.atencion = atencion;
    }

    public String getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(String fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public String getIndicacionesGenerales() {
        return indicacionesGenerales;
    }

    public void setIndicacionesGenerales(String indicacionesGenerales) {
        this.indicacionesGenerales = indicacionesGenerales;
    }

    public List<DetalleRecetaDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleRecetaDTO> detalles) {
        this.detalles = detalles;
    }
}
