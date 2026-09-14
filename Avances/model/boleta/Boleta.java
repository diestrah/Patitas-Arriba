package model.boleta;

import model.cita.Cita;
import model.usuario.Cliente;

import java.time.LocalDate;
import java.util.List;

public class Boleta {
    private int idBoleta;
    private LocalDate fecha;
    private double total;
    private MetodoPago metodoPago;
    private Cliente cliente;
    private Cita cita;
    private List<DetalleBoleta> detalles;

    public Boleta(int idBoleta, LocalDate fecha, double total, MetodoPago metodoPago, Cliente cliente, Cita cita, List<DetalleBoleta> detalles) {
        this.idBoleta = idBoleta;
        this.fecha = fecha;
        this.total = total;
        this.metodoPago = metodoPago;
        this.cliente = cliente;
        this.cita = cita;
        this.detalles = detalles;
    }

    // Getters y Setters
    public int getIdBoleta() {
        return idBoleta;
    }

    public void setIdBoleta(int idBoleta) {
        this.idBoleta = idBoleta;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Cita getCita() {
        return cita;
    }

    public void setCita(Cita cita) {
        this.cita = cita;
    }

    public List<DetalleBoleta> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleBoleta> detalles) {
        this.detalles = detalles;
    }

    @Override
    public String toString() {
        String dniCliente = null;
        if (cliente != null) {
            dniCliente = cliente.getDni();
        }

        int idCita = -1;
        if (cita != null) {
            idCita = cita.getIdCita();
        }

        String textoCita;
        if (idCita == -1) {
            textoCita = "ninguna";
        } else {
            textoCita = String.valueOf(idCita);
        }

        String textoDetalles = "";
        for (DetalleBoleta detalle : detalles) {
            textoDetalles = textoDetalles + detalle.toString() + "\n";
        }

        return "Boleta\n" +
                "idBoleta: " + idBoleta + "\n" +
                "fecha: " + fecha + "\n" +
                "total: " + total + "\n" +
                "metodoPago: " + metodoPago + "\n" +
                "cliente: " + dniCliente + "\n" +
                "cita: " + textoCita + "\n" +
                "detalles:\n" + textoDetalles;
    }
}
