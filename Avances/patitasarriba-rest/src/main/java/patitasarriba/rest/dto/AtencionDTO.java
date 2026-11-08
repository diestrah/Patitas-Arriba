package patitasarriba.rest.dto;

import java.util.ArrayList;
import java.util.List;

public class AtencionDTO {
    private int id;
    private boolean activo;
    private DetalleCitaDTO detalleCita;
    private String fechaHora;
    private String motivoConsulta;
    private String observaciones;
    private List<InsumoUtilizadoDTO> insumosUtilizados = new ArrayList<>();
    private List<AtencionDiagnosticoDTO> diagnosticos = new ArrayList<>();

    public AtencionDTO() {
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

    public DetalleCitaDTO getDetalleCita() {
        return detalleCita;
    }

    public void setDetalleCita(DetalleCitaDTO detalleCita) {
        this.detalleCita = detalleCita;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getMotivoConsulta() {
        return motivoConsulta;
    }

    public void setMotivoConsulta(String motivoConsulta) {
        this.motivoConsulta = motivoConsulta;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public List<InsumoUtilizadoDTO> getInsumosUtilizados() {
        return insumosUtilizados;
    }

    public void setInsumosUtilizados(List<InsumoUtilizadoDTO> insumosUtilizados) {
        this.insumosUtilizados = insumosUtilizados;
    }

    public List<AtencionDiagnosticoDTO> getDiagnosticos() {
        return diagnosticos;
    }

    public void setDiagnosticos(List<AtencionDiagnosticoDTO> diagnosticos) {
        this.diagnosticos = diagnosticos;
    }
}
