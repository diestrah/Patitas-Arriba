package model.atencion;

public class AtencionDiagnostico {
    private String nivelGravedad;
    private String detalleDiagnostico;
    private Diagnostico diagnostico;


    public AtencionDiagnostico(String nivelGravedad, String detalleDiagnostico, Diagnostico diagnostico) {
        this.nivelGravedad = nivelGravedad;
        this.detalleDiagnostico = detalleDiagnostico;
        this.diagnostico = diagnostico;
    }

    // Getters y Setters
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

    public Diagnostico getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(Diagnostico diagnostico) {
        this.diagnostico = diagnostico;
    }

    @Override
    public String toString() {
        return "AtencionDiagnostico{" +
                "nivelGravedad='" + nivelGravedad + '\'' +
                ", detalleDiagnostico='" + detalleDiagnostico + '\'' +
                ", diagnostico=" + diagnostico +
                '}';
    }
}
