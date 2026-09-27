package patitasarriba.modelo.atencion;

import patitasarriba.modelo.Registro;

public class Diagnostico extends Registro {
    private String nombreEnfermedad;
    private String descripcion;

    public Diagnostico() {
    }

    public Diagnostico(int id, boolean activo, String nombreEnfermedad, String descripcion) {
        super(id, activo);
        setNombreEnfermedad(nombreEnfermedad);
        setDescripcion(descripcion);
    }

    // Constructor de copia
    public Diagnostico(final Diagnostico diagnostico) {
        if (diagnostico == null) {
            throw new IllegalArgumentException("diagnostico no puede ser nulo");
        }
        super(diagnostico);
        setNombreEnfermedad(diagnostico.getNombreEnfermedad());
        setDescripcion(diagnostico.getDescripcion());
    }

    // Getters y Setters

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
                super.toString() +
                ", nombreEnfermedad='" + nombreEnfermedad + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}
