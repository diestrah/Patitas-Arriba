package patitasarriba.modelo.usuario;

public abstract class Persona {
    private int idPersona;
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String telefono;
    private String dni;
    private Cuenta cuenta;


    public Persona(int idPersona, String nombres, String apellidoPaterno, String apellidoMaterno,
                   String telefono, String dni, Cuenta cuenta) {
        this.idPersona = idPersona;
        this.nombres = nombres;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.telefono = telefono;
        this.dni = dni;
        this.cuenta = cuenta;
    }

    // Getters y Setters

    public int getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(int idPersona) {
        this.idPersona = idPersona;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        if (nombres == null || nombres.isEmpty()) {
            throw new IllegalArgumentException("nombres no puede ser nulo");
        }
        this.nombres = nombres;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        if (apellidoPaterno == null || apellidoPaterno.isEmpty()) {
            throw new IllegalArgumentException("apellidoPaterno no puede ser nulo");
        }
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        if (apellidoMaterno == null || apellidoMaterno.isEmpty()) {
            throw new IllegalArgumentException("apellidoMaterno no puede ser nulo");
        }
        this.apellidoMaterno = apellidoMaterno;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        if (dni == null || dni.isEmpty()) {
            throw new IllegalArgumentException("dni no puede ser nulo");
        }
        this.dni = dni;
    }

    public Cuenta getCuenta() {
        return cuenta;
    }

    public void setCuenta(Cuenta cuenta) {
        this.cuenta = cuenta;
    }

    @Override
    public String toString() {
        return "Persona{" +
                "idPersona=" + idPersona +
                "nombres='" + nombres + '\'' +
                ", apellidoPaterno='" + apellidoPaterno + '\'' +
                ", apellidoMaterno='" + apellidoMaterno + '\'' +
                ", telefono='" + telefono + '\'' +
                ", dni='" + dni + '\'' +
                ", cuenta=" + cuenta +
                '}';
    }
}
