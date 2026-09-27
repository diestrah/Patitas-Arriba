package patitasarriba.modelo.atencion;

import patitasarriba.modelo.Registro;

public class AtencionDiagnostico extends Registro {
    private NivelGravedad nivelGravedad;
    private String detalleDiagnostico;
    private Diagnostico diagnostico;

    public AtencionDiagnostico() {
    }

    public AtencionDiagnostico(int id, boolean activo, NivelGravedad nivelGravedad, String detalleDiagnostico, Diagnostico diagnostico) {
        super(id, activo);
        setNivelGravedad(nivelGravedad);
        setDetalleDiagnostico(detalleDiagnostico);
        setDiagnostico(diagnostico);
    }

    // Constructor de copia
    public AtencionDiagnostico(final AtencionDiagnostico atencionDiagnostico) {
        if (atencionDiagnostico == null) {
            throw new IllegalArgumentException("atencionDiagnostico no puede ser nulo");
        }
        super(atencionDiagnostico);
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
                super.toString() +
                "nivelGravedad='" + nivelGravedad + '\'' +
                ", detalleDiagnostico='" + detalleDiagnostico + '\'' +
                ", diagnostico=" + diagnostico +
                '}';
    }
}
