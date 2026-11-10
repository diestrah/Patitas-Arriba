package patitasarriba.rest.dto;

public class AtencionDiagnosticoDTO {
    // Si el JSON no trae "activo", queda en true (igual que el modelo Registro)
    private boolean activo = true;
    private DiagnosticoDTO diagnostico;
    private String nivelGravedad;
    private String detalleDiagnostico;

    public AtencionDiagnosticoDTO() {
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public DiagnosticoDTO getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(DiagnosticoDTO diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getNivelGravedad() {
        return nivelGravedad;
    }

    public void setNivelGravedad(String nivelGravedad) {
        this.nivelGravedad = nivelGravedad;
    }

    public String getDetalleDiagnostico() {
        return detalleDiagnostico;
    }

    public void setDetalleDiagnostico(String detalleDiagnostico) {
        this.detalleDiagnostico = detalleDiagnostico;
    }
}
