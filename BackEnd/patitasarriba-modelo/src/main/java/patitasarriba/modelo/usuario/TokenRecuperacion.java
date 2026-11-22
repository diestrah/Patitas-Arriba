package patitasarriba.modelo.usuario;

import java.time.LocalDateTime;

// Codigo de un solo uso con vencimiento para restablecer la contrasena de una cuenta.
// Solo se guarda el hash: el codigo en claro viaja unicamente en el correo
public class TokenRecuperacion {
    private int id;
    private int idCuenta;
    private String tokenHash;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaVencimiento;
    private boolean usado;

    public TokenRecuperacion() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdCuenta() { return idCuenta; }
    public void setIdCuenta(int idCuenta) { this.idCuenta = idCuenta; }

    public String getTokenHash() { return tokenHash; }
    public void setTokenHash(String tokenHash) { this.tokenHash = tokenHash; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDateTime fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public boolean isUsado() { return usado; }
    public void setUsado(boolean usado) { this.usado = usado; }

    // Sirve solo si no se uso y aun no vencio
    public boolean estaVigente(LocalDateTime ahora) {
        return !usado && fechaVencimiento != null && fechaVencimiento.isAfter(ahora);
    }
}
