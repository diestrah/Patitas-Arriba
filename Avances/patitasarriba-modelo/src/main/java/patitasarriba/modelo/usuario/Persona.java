package patitasarriba.modelo.usuario;

import patitasarriba.modelo.Registro;

public abstract class Persona extends Registro {
    private int idPersona;
    private Cuenta cuenta;
    private String dni;
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String telefono;

    public Persona() {
    }

    public Persona(int idPersona, boolean activo, Cuenta cuenta, String dni, String nombres,
                   String apellidoPaterno, String apellidoMaterno, String telefono) {
        super(activo);
        setIdPersona(idPersona);
        setCuenta(cuenta);
        setDni(dni);
        setNombres(nombres);
        setApellidoPaterno(apellidoPaterno);
        setApellidoMaterno(apellidoMaterno);
        setTelefono(telefono);
    }

    // Constructor de copia
    public Persona(final Persona persona) {
        if (persona == null) {
            throw new IllegalArgumentException("persona no puede ser nula");
        }
        super(persona);
        setIdPersona(persona.getIdPersona());
        setCuenta(persona.getCuenta());
        setDni(persona.getDni());
        setNombres(persona.getNombres());
        setApellidoPaterno(persona.getApellidoPaterno());
        setApellidoMaterno(persona.getApellidoMaterno());
        setTelefono(persona.getTelefono());
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

    public Cuenta getCuenta() {
        if (cuenta == null) {
            return null;
        }
        return new Cuenta(cuenta);
    }

    public void setCuenta(Cuenta cuenta) {
        if (cuenta == null) {
            throw new IllegalArgumentException("cuenta no puede ser nula");
        }
        this.cuenta = new Cuenta(cuenta);
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        // El formato (8 digitos) lo valida la capa de negocio
        if (dni == null || dni.isBlank()) {
            throw new IllegalArgumentException("dni no puede ser nulo o vacío");
        }
        this.dni = dni;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        if (nombres == null || nombres.isBlank()) {
            throw new IllegalArgumentException("nombres no puede ser nulo o vacío");
        }
        this.nombres = nombres;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        if (apellidoPaterno == null || apellidoPaterno.isBlank()) {
            throw new IllegalArgumentException("apellidoPaterno no puede ser nulo o vacío");
        }
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        if (apellidoMaterno == null || apellidoMaterno.isBlank()) {
            throw new IllegalArgumentException("apellidoMaterno no puede ser nulo o vacío");
        }
        this.apellidoMaterno = apellidoMaterno;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        if (telefono == null || telefono.isBlank()) {
            throw new IllegalArgumentException("telefono no puede ser nulo o vacío");
        }
        this.telefono = telefono;
    }

    @Override
    public String toString() {
        String nombreUsuario = null;
        if (cuenta != null) {
            nombreUsuario = cuenta.getNombreUsuario();
        }

        return "idPersona=" + idPersona +
                ", " + super.toString() +
                ", cuenta=" + nombreUsuario +
                ", dni='" + dni + '\'' +
                ", nombres='" + nombres + '\'' +
                ", apellidoPaterno='" + apellidoPaterno + '\'' +
                ", apellidoMaterno='" + apellidoMaterno + '\'' +
                ", telefono='" + telefono + '\'';
    }
}
