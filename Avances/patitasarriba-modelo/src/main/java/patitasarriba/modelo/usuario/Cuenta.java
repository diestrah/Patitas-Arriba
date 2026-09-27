package patitasarriba.modelo.usuario;

import java.time.LocalDate;

public class Cuenta {
    private int idCuenta;
    private String password;
    private String correo;
    private LocalDate fechaCreacion;
    private String nombreUsuario;


    public Cuenta(int idCuenta, String password, String correo, LocalDate fechaCreacion, String nombreUsuario) {
        setIdCuenta(idCuenta);
        setPassword(password);
        setCorreo(correo);
        setFechaCreacion(fechaCreacion);
        setNombreUsuario(nombreUsuario);
    }

    // Constructor de copia
    public Cuenta(Cuenta cuenta) {
        if (cuenta == null) {
            throw new IllegalArgumentException("cuenta no puede ser nula");
        }
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
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("password no puede ser nulo");
        }
        this.password = password;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        if (correo == null || correo.isEmpty()) {
            throw new IllegalArgumentException("correo no puede ser nulo");
        }
        this.correo = correo;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        if (nombreUsuario == null || nombreUsuario.isEmpty()) {
            throw new IllegalArgumentException("nombreUsuario no puede ser nulo");
        }
        this.nombreUsuario = nombreUsuario;
    }

    @Override
    public String toString() {
        return "Cuenta{" +
                "idCuenta=" + idCuenta +
                ", password='" + password + '\'' +
                ", correo='" + correo + '\'' +
                ", fechaCreacion=" + fechaCreacion +
                ", nombreUsuario='" + nombreUsuario + '\'' +
                '}';
    }
}
