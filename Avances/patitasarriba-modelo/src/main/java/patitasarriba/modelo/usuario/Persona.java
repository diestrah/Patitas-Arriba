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
        setIdPersona(idPersona);
        setNombres(nombres);
        setApellidoPaterno(apellidoPaterno);
        setApellidoMaterno(apellidoMaterno);
        setTelefono(telefono);
        setDni(dni);
        setCuenta(cuenta);
    }

    // Constructor de copia
    protected Persona(Persona persona) {
        if (persona == null) {
            throw new IllegalArgumentException("persona no puede ser nula");
        }
        setIdPersona(persona.getIdPersona());
        setNombres(persona.getNombres());
        setApellidoPaterno(persona.getApellidoPaterno());
        setApellidoMaterno(persona.getApellidoMaterno());
        setTelefono(persona.getTelefono());
        setDni(persona.getDni());
        setCuenta(persona.getCuenta());
    }

    // Getters y Setters

    public int getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(int idPersona) {
        if (idPersona < 0) {
            throw new IllegalArgumentException("idPersona no puede ser negativo");
        }
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
        if (telefono == null || telefono.isEmpty()) {
            throw new IllegalArgumentException("telefono no puede ser nulo");
        }
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
        if (cuenta == null) {
            throw new IllegalArgumentException("cuenta no puede ser nula");
        }
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
