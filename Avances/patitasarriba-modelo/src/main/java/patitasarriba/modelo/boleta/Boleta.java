package patitasarriba.modelo.boleta;

import patitasarriba.modelo.Registro;
import patitasarriba.modelo.atencion.InsumoUtilizado;
import patitasarriba.modelo.cita.DetalleCita;
import patitasarriba.modelo.producto.Articulo;
import patitasarriba.modelo.usuario.Cliente;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Boleta extends Registro {
    private int idBoleta;
    private Cliente cliente;
    private List<DetalleBoleta> detalles;
    private String serieCorrelativo;
    private LocalDateTime fecha;
    private double total;
    private MetodoPago metodoPago;

    public Boleta() {
        this.detalles = new ArrayList<>();
    }

    public Boleta(int idBoleta, boolean activo, Cliente cliente, List<DetalleBoleta> detalles, String serieCorrelativo,
                  LocalDateTime fecha, double total, MetodoPago metodoPago) {
        super(activo);
        setIdBoleta(idBoleta);
        setCliente(cliente);
        setDetalles(detalles);
        setSerieCorrelativo(serieCorrelativo);
        setFecha(fecha);
        setTotal(total);
        setMetodoPago(metodoPago);
    }

    // Constructor de copia
    public Boleta(final Boleta boleta) {
        if (boleta == null) {
            throw new IllegalArgumentException("boleta no puede ser nula");
        }
        super(boleta);
        setIdBoleta(boleta.getIdBoleta());
        setCliente(boleta.getCliente());
        setDetalles(boleta.getDetalles());
        setSerieCorrelativo(boleta.getSerieCorrelativo());
        setFecha(boleta.getFecha());
        setTotal(boleta.getTotal());
        setMetodoPago(boleta.getMetodoPago());
    }

    // Getters y Setters
    public int getIdBoleta() {
        return idBoleta;
    }

    public void setIdBoleta(int idBoleta) {
        if (idBoleta < 0) {
            throw new IllegalArgumentException("idBoleta no puede ser negativo");
        }
        this.idBoleta = idBoleta;
    }

    public Cliente getCliente() {
        if (cliente == null) {
            return null;
        }
        return new Cliente(cliente);
    }

    public void setCliente(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("cliente no puede ser nulo");
        }
        this.cliente = new Cliente(cliente);
    }

    public List<DetalleBoleta> getDetalles() {
        return Collections.unmodifiableList(detalles);
    }

    public void setDetalles(List<DetalleBoleta> detalles) {
        if (detalles == null) {
            throw new IllegalArgumentException("detalles no puede ser nulo");
        }
        for (DetalleBoleta detalle : detalles) {
            if (detalle == null) {
                throw new IllegalArgumentException("detalles no puede contener elementos nulos");
            }
        }
        this.detalles = new ArrayList<>(detalles);
    }

    public String getSerieCorrelativo() {
        return serieCorrelativo;
    }

    public void setSerieCorrelativo(String serieCorrelativo) {
        if (serieCorrelativo == null || serieCorrelativo.isBlank()) {
            throw new IllegalArgumentException("serieCorrelativo no puede ser nulo o vacío");
        }
        this.serieCorrelativo = serieCorrelativo;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException("fecha no puede ser nula");
        }
        this.fecha = fecha;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        // Que el total sea la suma de los subtotales lo valida la capa de negocio
        if (total < 0) {
            throw new IllegalArgumentException("total no puede ser negativo");
        }
        this.total = total;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        if (metodoPago == null) {
            throw new IllegalArgumentException("metodoPago no puede ser nulo");
        }
        this.metodoPago = metodoPago;
    }

    // Linea de articulo: venta directa en caja (insumoUtilizado = null) o cobro de un insumo de una atencion
    public void agregarDetalleArticulo(int idDetalleBoleta, boolean activo, int cantidad, double precioUnitario,
                                       Articulo articulo, InsumoUtilizado insumoUtilizado) {
        detalles.add(new DetalleBoletaArticulo(idDetalleBoleta, activo, this, cantidad, precioUnitario,
                articulo, insumoUtilizado));
    }

    // Linea de servicio: cobro de un servicio pedido en la cita
    public void agregarDetalleServicio(int idDetalleBoleta, boolean activo, int cantidad, double precioUnitario,
                                       DetalleCita detalleCita) {
        detalles.add(new DetalleBoletaServicio(idDetalleBoleta, activo, this, cantidad, precioUnitario, detalleCita));
    }

    @Override
    public String toString() {
        String dniCliente = null;
        if (cliente != null) {
            dniCliente = cliente.getDni();
        }

        return "Boleta{" +
                "idBoleta=" + idBoleta +
                ", " + super.toString() +
                ", cliente=" + dniCliente +
                ", serieCorrelativo='" + serieCorrelativo + '\'' +
                ", fecha=" + fecha +
                ", total=" + total +
                ", metodoPago=" + metodoPago +
                ", detalles=" + detalles +
                '}';
    }
}
