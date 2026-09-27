package patitasarriba.modelo.atencion;

public class Tratamiento {
    private int idTratamiento;
    private String nombreProcedimiento;
    private String descripcion;


    public Tratamiento(int idTratamiento, String nombreProcedimiento, String descripcion) {
        setIdTratamiento(idTratamiento);
        setNombreProcedimiento(nombreProcedimiento);
        setDescripcion(descripcion);
    }

    // Constructor de copia
    public Tratamiento(Tratamiento tratamiento) {
        if (tratamiento == null) {
            throw new IllegalArgumentException("tratamiento no puede ser nulo");
        }
        setIdTratamiento(tratamiento.getIdTratamiento());
        setNombreProcedimiento(tratamiento.getNombreProcedimiento());
        setDescripcion(tratamiento.getDescripcion());
    }

    // Getters y Setters
    public int getIdTratamiento() {
        return idTratamiento;
    }

    public void setIdTratamiento(int idTratamiento) {
        if (idTratamiento < 0) {
            throw new IllegalArgumentException("idTratamiento no puede ser negativo");
        }
        this.idTratamiento = idTratamiento;
    }

    public String getNombreProcedimiento() {
        return nombreProcedimiento;
    }

    public void setNombreProcedimiento(String nombreProcedimiento) {
        if (nombreProcedimiento == null || nombreProcedimiento.isEmpty()) {
            throw new IllegalArgumentException("nombreProcedimiento no puede ser nulo o vacío");
        }
        this.nombreProcedimiento = nombreProcedimiento;
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
        return "Tratamiento{" +
                "idTratamiento=" + idTratamiento +
                ", nombreProcedimiento='" + nombreProcedimiento + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}
