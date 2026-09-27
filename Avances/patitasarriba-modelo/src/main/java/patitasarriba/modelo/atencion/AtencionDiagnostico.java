package patitasarriba.modelo.atencion;

public class AtencionDiagnostico {
    private NivelGravedad nivelGravedad;
    private String detalleDiagnostico;
    private Diagnostico diagnostico;


    public AtencionDiagnostico(NivelGravedad nivelGravedad, String detalleDiagnostico, Diagnostico diagnostico) {
        setNivelGravedad(nivelGravedad);
        setDetalleDiagnostico(detalleDiagnostico);
        setDiagnostico(diagnostico);
    }

    // Constructor de copia
    public AtencionDiagnostico(AtencionDiagnostico atencionDiagnostico) {
        if (atencionDiagnostico == null) {
            throw new IllegalArgumentException("atencionDiagnostico no puede ser nulo");
        }
        setNivelGravedad(atencionDiagnostico.getNivelGravedad());
        setDetalleDiagnostico(atencionDiagnostico.getDetalleDiagnostico());
        setDiagnostico(atencionDiagnostico.getDiagnostico());
    }

    // Getters y Setters
    public NivelGravedad getNivelGravedad() {
        return nivelGravedad;
    }

    public void setNivelGravedad(NivelGravedad nivelGravedad) {
        if (nivelGravedad == null) {
            throw new IllegalArgumentException("nivelGravedad no puede ser nulo");
        }
        this.nivelGravedad = nivelGravedad;
    }

    public String getDetalleDiagnostico() {
        return detalleDiagnostico;
    }

    public void setDetalleDiagnostico(String detalleDiagnostico) {
        if (detalleDiagnostico == null || detalleDiagnostico.isEmpty()) {
            throw new IllegalArgumentException("detalleDiagnostico no puede ser nulo o vacío");
        }
        this.detalleDiagnostico = detalleDiagnostico;
    }

    public Diagnostico getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(Diagnostico diagnostico) {
        if (diagnostico == null) {
            throw new IllegalArgumentException("diagnostico no puede ser nulo");
        }
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
