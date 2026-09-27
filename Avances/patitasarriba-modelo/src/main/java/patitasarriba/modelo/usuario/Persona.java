package patitasarriba.modelo.usuario;

import patitasarriba.modelo.Registro;

public abstract class Persona extends Registro {
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String telefono;
    private String dni;
    private Cuenta cuenta;


    public Persona(){}

    public Persona(int id, boolean activo, String nombres, String apellidoPaterno, String apellidoMaterno,
                   String telefono, String dni, Cuenta cuenta) {
        super(id, activo);
        setNombres(nombres);
        setApellidoPaterno(apellidoPaterno);
        setApellidoMaterno(apellidoMaterno);
        setTelefono(telefono);
        setDni(dni);
        setCuenta(cuenta);
    }

    // Constructor de copia
    public Persona(final Persona persona) {
        if (persona == null) {
            throw new IllegalArgumentException("persona no puede ser nula");
        }
        super(persona);
        setNombres(persona.getNombres());
        setApellidoPaterno(persona.getApellidoPaterno());
        setApellidoMaterno(persona.getApellidoMaterno());
        setTelefono(persona.getTelefono());
        setDni(persona.getDni());
        setCuenta(persona.getCuenta());
    }

    // Getters y Setters

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
                super.toString() +
                "nombres='" + nombres + '\'' +
                ", apellidoPaterno='" + apellidoPaterno + '\'' +
                ", apellidoMaterno='" + apellidoMaterno + '\'' +
                ", telefono='" + telefono + '\'' +
                ", dni='" + dni + '\'' +
                ", cuenta=" + cuenta +
                '}';
    }
}
