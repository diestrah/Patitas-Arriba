package model.atencion;

public class Diagnostico {
    private int idDiagnostico;
    private String nombreEnfermedad;
    private String descripcion;


    public Diagnostico(int idDiagnostico, String nombreEnfermedad, String descripcion) {
        this.idDiagnostico = idDiagnostico;
        this.nombreEnfermedad = nombreEnfermedad;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public int getIdDiagnostico() {
        return idDiagnostico;
    }

    public void setIdDiagnostico(int idDiagnostico) {
        this.idDiagnostico = idDiagnostico;
    }

    public String getNombreEnfermedad() {
        return nombreEnfermedad;
    }

    public void setNombreEnfermedad(String nombreEnfermedad) {
        this.nombreEnfermedad = nombreEnfermedad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
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
