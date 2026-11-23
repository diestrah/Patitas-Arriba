package patitasarriba.rest.dto;

public class DetalleBoletaServicioDTO {
    private int id;
    // Si el JSON no trae "activo", queda en true (igual que el modelo Registro)
    private boolean activo = true;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;
    private DetalleCitaDTO detalleCita;

    public DetalleBoletaServicioDTO() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    // De solo lectura: BoletaBLImpl.validarYCalcular siempre lo recalcula desde el precio base
    // real del servicio. Lo que venga aqui en el POST/PUT se ignora.
    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public DetalleCitaDTO getDetalleCita() {
        return detalleCita;
    }

    public void setDetalleCita(DetalleCitaDTO detalleCita) {
        this.detalleCita = detalleCita;
    }
}
