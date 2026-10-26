package patitasarriba.modelo.usuario;

import patitasarriba.modelo.Registro;

import java.time.LocalDate;

public class Cuenta extends Registro {
    private int idCuenta;
    private String password;
    private String correo;
    private LocalDate fechaCreacion;
    private String nombreUsuario;

    public Cuenta() {
    }

    public Cuenta(int idCuenta, boolean activo, String password, String correo, LocalDate fechaCreacion,
                  String nombreUsuario) {
        super(activo);
        setIdCuenta(idCuenta);
        setPassword(password);
        setCorreo(correo);
        setFechaCreacion(fechaCreacion);
        setNombreUsuario(nombreUsuario);
    }

    // Constructor de copia
    public Cuenta(final Cuenta cuenta) {
        if (cuenta == null) {
            throw new IllegalArgumentException("cuenta no puede ser nula");
        }
        super(cuenta);
        setIdCuenta(cuenta.getIdCuenta());
        setPassword(cuenta.getPassword());
        setCorreo(cuenta.getCorreo());
        setFechaCreacion(cuenta.getFechaCreacion());
        setNombreUsuario(cuenta.getNombreUsuario());
    }

    // Getters y Setters
    public int getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(int idCuenta) {
        if (idCuenta < 0) {
            throw new IllegalArgumentException("idCuenta no puede ser negativo");
        }
        this.idCuenta = idCuenta;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("password no puede ser nulo o vacío");
        }
        this.password = password;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        if (correo == null || correo.isBlank()) {
            throw new IllegalArgumentException("correo no puede ser nulo o vacío");
        }
        this.correo = correo;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        if (fechaCreacion == null) {
            throw new IllegalArgumentException("fechaCreacion no puede ser nula");
        }
        this.fechaCreacion = fechaCreacion;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        if (nombreUsuario == null || nombreUsuario.isBlank()) {
            throw new IllegalArgumentException("nombreUsuario no puede ser nulo o vacío");
        }
        this.nombreUsuario = nombreUsuario;
    }

    @Override
    public String toString() {
        // El password no se imprime
        return "Cuenta{" +
                "idCuenta=" + idCuenta +
                ", " + super.toString() +
                ", correo='" + correo + '\'' +
                ", fechaCreacion=" + fechaCreacion +
                ", nombreUsuario='" + nombreUsuario + '\'' +
                '}';
    }
}
