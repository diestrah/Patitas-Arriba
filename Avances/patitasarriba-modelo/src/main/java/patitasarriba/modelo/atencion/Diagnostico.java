package patitasarriba.modelo.atencion;

public class Diagnostico {
    private int idDiagnostico;
    private String nombreEnfermedad;
    private String descripcion;


    public Diagnostico(int idDiagnostico, String nombreEnfermedad, String descripcion) {
        setIdDiagnostico(idDiagnostico);
        setNombreEnfermedad(nombreEnfermedad);
        setDescripcion(descripcion);
    }

    // Constructor de copia
    public Diagnostico(Diagnostico diagnostico) {
        if (diagnostico == null) {
            throw new IllegalArgumentException("diagnostico no puede ser nulo");
        }
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
        if (nombreEnfermedad == null || nombreEnfermedad.isEmpty()) {
            throw new IllegalArgumentException("nombreEnfermedad no puede ser nulo o vacío");
        }
        this.nombreEnfermedad = nombreEnfermedad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        if (descripcion == null || descripcion.isEmpty()) {
            throw new IllegalArgumentException("descripcion no puede ser nula o vacía");
        }
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return "Diagnostico{" +
                "idDiagnostico=" + idDiagnostico +
                ", nombreEnfermedad='" + nombreEnfermedad + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}
