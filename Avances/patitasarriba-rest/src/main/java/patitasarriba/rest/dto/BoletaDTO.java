package patitasarriba.rest.dto;

import java.util.ArrayList;
import java.util.List;

public class BoletaDTO {
    private int id;
    // Si el JSON no trae "activo", queda en true (igual que el modelo Registro)
    private boolean activo = true;
    private ClienteDTO cliente;
    private String serieCorrelativo;
    private String fecha;
    private double total;
    private String metodoPago;
    // Los detalles van en dos listas en vez de una sola polimorfica (mas simple de mapear a/desde
    // JSON sin un campo discriminador): BoletaMapper las combina en el unico List<DetalleBoleta>
    // que pide el modelo.
    private List<DetalleBoletaArticuloDTO> detallesArticulo = new ArrayList<>();
    private List<DetalleBoletaServicioDTO> detallesServicio = new ArrayList<>();

    public BoletaDTO() {
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

    public ClienteDTO getCliente() {
        return cliente;
    }

    public void setCliente(ClienteDTO cliente) {
        this.cliente = cliente;
    }

    public String getSerieCorrelativo() {
        return serieCorrelativo;
    }

    public void setSerieCorrelativo(String serieCorrelativo) {
        this.serieCorrelativo = serieCorrelativo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    // De solo lectura: BoletaBLImpl.validarYCalcular siempre lo recalcula desde los detalles.
    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public List<DetalleBoletaArticuloDTO> getDetallesArticulo() {
        return detallesArticulo;
    }

    public void setDetallesArticulo(List<DetalleBoletaArticuloDTO> detallesArticulo) {
        this.detallesArticulo = detallesArticulo;
    }

    public List<DetalleBoletaServicioDTO> getDetallesServicio() {
        return detallesServicio;
    }

    public void setDetallesServicio(List<DetalleBoletaServicioDTO> detallesServicio) {
        this.detallesServicio = detallesServicio;
    }
}
