package patitasarriba.rest.dto;

public class DetalleBoletaArticuloDTO {
    private int id;
    private boolean activo;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;
    private ArticuloDTO articulo;
    private InsumoUtilizadoRefDTO insumoUtilizado;

    public DetalleBoletaArticuloDTO() {
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
    // real del articulo. Lo que venga aqui en el POST/PUT se ignora.
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

    public ArticuloDTO getArticulo() {
        return articulo;
    }

    public void setArticulo(ArticuloDTO articulo) {
        this.articulo = articulo;
    }

    public InsumoUtilizadoRefDTO getInsumoUtilizado() {
        return insumoUtilizado;
    }

    public void setInsumoUtilizado(InsumoUtilizadoRefDTO insumoUtilizado) {
        this.insumoUtilizado = insumoUtilizado;
    }
}
