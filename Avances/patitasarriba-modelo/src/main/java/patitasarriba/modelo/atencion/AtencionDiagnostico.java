package patitasarriba.modelo.atencion;

import patitasarriba.modelo.Registro;

// No tiene id propio: en la base su clave es compuesta (id_atencion, id_diagnostico)
public class AtencionDiagnostico extends Registro {
    private Atencion atencion;
    private Diagnostico diagnostico;
    private NivelGravedad nivelGravedad;
    private String detalleDiagnostico;

    public AtencionDiagnostico() {
    }

    public AtencionDiagnostico(boolean activo, Atencion atencion, Diagnostico diagnostico,
                               NivelGravedad nivelGravedad, String detalleDiagnostico) {
        super(activo);
        setAtencion(atencion);
        setDiagnostico(diagnostico);
        setNivelGravedad(nivelGravedad);
        setDetalleDiagnostico(detalleDiagnostico);
    }

    // Constructor de copia
    public AtencionDiagnostico(final AtencionDiagnostico atencionDiagnostico) {
        if (atencionDiagnostico == null) {
            throw new IllegalArgumentException("atencionDiagnostico no puede ser nulo");
        }
        super(atencionDiagnostico);
        setAtencion(atencionDiagnostico.getAtencion());
        setDiagnostico(atencionDiagnostico.getDiagnostico());
        setNivelGravedad(atencionDiagnostico.getNivelGravedad());
        setDetalleDiagnostico(atencionDiagnostico.getDetalleDiagnostico());
    }

    // Getters y Setters

    // atencion es el contenedor (composicion): se guarda la referencia, no una copia,
    // para que el diagnostico siga apuntando a la misma atencion que lo contiene
    public Atencion getAtencion() {
        return atencion;
    }

    public void setAtencion(Atencion atencion) {
        if (atencion == null) {
            throw new IllegalArgumentException("atencion no puede ser nula");
        }
        this.atencion = atencion;
    }

    public Diagnostico getDiagnostico() {
        if (diagnostico == null) {
            return null;
        }
        return new Diagnostico(diagnostico);
    }

    public void setDiagnostico(Diagnostico diagnostico) {
        if (diagnostico == null) {
            throw new IllegalArgumentException("diagnostico no puede ser nulo");
        }
        this.diagnostico = new Diagnostico(diagnostico);
    }

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
        // detalleDiagnostico es opcional
        this.detalleDiagnostico = detalleDiagnostico;
    }

    @Override
    public String toString() {
        int idAtencion = -1;
        if (atencion != null) {
            idAtencion = atencion.getIdAtencion();
        }

        String nombreEnfermedad = null;
        if (diagnostico != null) {
            nombreEnfermedad = diagnostico.getNombreEnfermedad();
        }

        return "AtencionDiagnostico{" +
                super.toString() +
                ", atencion=" + idAtencion +
                ", diagnostico=" + nombreEnfermedad +
                ", nivelGravedad=" + nivelGravedad +
                ", detalleDiagnostico='" + detalleDiagnostico + '\'' +
                '}';
    }
}
