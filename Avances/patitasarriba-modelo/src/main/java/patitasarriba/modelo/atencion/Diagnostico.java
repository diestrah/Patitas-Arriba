package patitasarriba.modelo.atencion;

import patitasarriba.modelo.Registro;

public class Diagnostico extends Registro {
    private int idDiagnostico;
    private String nombreEnfermedad;
    private String descripcion;

    public Diagnostico() {
    }

    public Diagnostico(int idDiagnostico, boolean activo, String nombreEnfermedad, String descripcion) {
        super(activo);
        setIdDiagnostico(idDiagnostico);
        setNombreEnfermedad(nombreEnfermedad);
        setDescripcion(descripcion);
    }

    // Constructor de copia
    public Diagnostico(final Diagnostico diagnostico) {
        if (diagnostico == null) {
            throw new IllegalArgumentException("diagnostico no puede ser nulo");
        }
        super(diagnostico);
        setIdDiagnostico(diagnostico.getIdDiagnostico());
        setNombreEnfermedad(diagnostico.getNombreEnfermedad());
        setDescripcion(diagnostico.getDescripcion());
    }

    // Getters y Setters
    public int getIdDiagnostico() {
        return idDiagnostico;
    }

    public void setIdDiagnostico(int idDiagnostico) {
        if (idDiagnostico < 0) {
            throw new IllegalArgumentException("idDiagnostico no puede ser negativo");
        }
        this.idDiagnostico = idDiagnostico;
    }

    public String getNombreEnfermedad() {
        return nombreEnfermedad;
    }

    public void setNombreEnfermedad(String nombreEnfermedad) {
        if (nombreEnfermedad == null || nombreEnfermedad.isBlank()) {
            throw new IllegalArgumentException("nombreEnfermedad no puede ser nulo o vacío");
        }
        this.nombreEnfermedad = nombreEnfermedad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("descripcion no puede ser nula o vacía");
        }
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return "Diagnostico{" +
                "idDiagnostico=" + idDiagnostico +
                ", " + super.toString() +
                ", nombreEnfermedad='" + nombreEnfermedad + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}
