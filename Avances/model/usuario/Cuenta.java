package model.usuario;

import java.time.LocalDate;

public class Cuenta {
    private int idCuenta;
    private String password;
    private String correo;
    private LocalDate fechaCreacion;
    private String nombreUsuario;


    public Cuenta(int idCuenta, String password, String correo, LocalDate fechaCreacion, String nombreUsuario) {
        this.idCuenta = idCuenta;
        this.password = password;
        this.correo = correo;
        this.fechaCreacion = fechaCreacion;
        this.nombreUsuario = nombreUsuario;
    }

    // Getters y Setters
    public int getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(int idCuenta) {
        this.idCuenta = idCuenta;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
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
