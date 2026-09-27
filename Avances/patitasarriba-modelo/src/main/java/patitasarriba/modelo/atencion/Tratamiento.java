package patitasarriba.modelo.atencion;

import patitasarriba.modelo.Registro;

public class Tratamiento extends Registro {
    private String nombreProcedimiento;
    private String descripcion;

    public Tratamiento() {
    }

    public Tratamiento(int id, boolean activo, String nombreProcedimiento, String descripcion) {
        super(id, activo);
        setNombreProcedimiento(nombreProcedimiento);
        setDescripcion(descripcion);
    }

    // Constructor de copia
    public Tratamiento(final Tratamiento tratamiento) {
        if (tratamiento == null) {
            throw new IllegalArgumentException("tratamiento no puede ser nulo");
        }
        super(tratamiento);
        setNombreProcedimiento(tratamiento.getNombreProcedimiento());
        setDescripcion(tratamiento.getDescripcion());
    }

    // Getters y Setters

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
                super.toString() +
                ", nombreProcedimiento='" + nombreProcedimiento + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}
