package model.atencion;

public class Tratamiento {
    private int idTratamiento;
    private String nombreProcedimiento;
    private String descripcion;


    public Tratamiento(int idTratamiento, String nombreProcedimiento, String descripcion) {
        this.idTratamiento = idTratamiento;
        this.nombreProcedimiento = nombreProcedimiento;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public int getIdTratamiento() {
        return idTratamiento;
    }

    public void setIdTratamiento(int idTratamiento) {
        this.idTratamiento = idTratamiento;
    }

    public String getNombreProcedimiento() {
        return nombreProcedimiento;
    }

    public void setNombreProcedimiento(String nombreProcedimiento) {
        this.nombreProcedimiento = nombreProcedimiento;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return "Tratamiento{" +
                "idTratamiento=" + idTratamiento +
                ", nombreProcedimiento='" + nombreProcedimiento + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}
