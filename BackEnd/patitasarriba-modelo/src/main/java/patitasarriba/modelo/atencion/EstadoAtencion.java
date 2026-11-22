package patitasarriba.modelo.atencion;

import patitasarriba.modelo.Registro;

// Vista liviana de una atencion: de que servicio de cita es y si sigue activa (activo = false: anulada).
// Sirve para saber que servicios ya se atendieron sin cargar la atencion completa (RN-CI-10, RN-AT-02)
public class EstadoAtencion extends Registro {
    private int idAtencion;
    private int idDetalleCita;

    public EstadoAtencion() {
    }

    public EstadoAtencion(int idAtencion, int idDetalleCita, boolean activo) {
        super(activo);
        setIdAtencion(idAtencion);
        setIdDetalleCita(idDetalleCita);
    }

    public int getIdAtencion() {
        return idAtencion;
    }

    public void setIdAtencion(int idAtencion) {
        if (idAtencion < 0) {
            throw new IllegalArgumentException("idAtencion no puede ser negativo");
        }
        this.idAtencion = idAtencion;
    }

    public int getIdDetalleCita() {
        return idDetalleCita;
    }

    public void setIdDetalleCita(int idDetalleCita) {
        if (idDetalleCita < 0) {
            throw new IllegalArgumentException("idDetalleCita no puede ser negativo");
        }
        this.idDetalleCita = idDetalleCita;
    }
}
