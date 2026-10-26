package patitasarriba.modelo.boleta;

import patitasarriba.modelo.cita.DetalleCita;

public class DetalleBoletaServicio extends DetalleBoleta {
    private DetalleCita detalleCita;

    public DetalleBoletaServicio() {
    }

    public DetalleBoletaServicio(int idDetalleBoleta, boolean activo, Boleta boleta, int cantidad,
                                 double precioUnitario, DetalleCita detalleCita) {
        super(idDetalleBoleta, activo, boleta, cantidad, precioUnitario);
        setDetalleCita(detalleCita);
    }

    // Constructor de copia
    public DetalleBoletaServicio(final DetalleBoletaServicio detalleBoletaServicio) {
        if (detalleBoletaServicio == null) {
            throw new IllegalArgumentException("detalleBoletaServicio no puede ser nulo");
        }
        super(detalleBoletaServicio);
        setDetalleCita(detalleBoletaServicio.getDetalleCita());
    }

    // Getters y Setters
    public DetalleCita getDetalleCita() {
        if (detalleCita == null) {
            return null;
        }
        return new DetalleCita(detalleCita);
    }

    public void setDetalleCita(DetalleCita detalleCita) {
        if (detalleCita == null) {
            throw new IllegalArgumentException("detalleCita no puede ser nulo");
        }
        this.detalleCita = new DetalleCita(detalleCita);
    }

    @Override
    public String toString() {
        int idDetalleCita = -1;
        if (detalleCita != null) {
            idDetalleCita = detalleCita.getIdDetalleCita();
        }

        return "DetalleBoletaServicio{" +
                super.toString() +
                ", detalleCita=" + idDetalleCita +
                '}';
    }
}
