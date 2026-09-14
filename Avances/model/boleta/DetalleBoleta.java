package model.boleta;

import model.producto.Producto;

public class DetalleBoleta {
    private int idDetalleBoleta;
    private int cantidad;
    private double subTotal;
    private Producto producto;


    public DetalleBoleta(int idDetalleBoleta, int cantidad, double subTotal, Producto producto) {
        this.idDetalleBoleta = idDetalleBoleta;
        this.cantidad = cantidad;
        this.subTotal = subTotal;
        this.producto = producto;
    }

    // Getters y Setters
    public int getIdDetalleBoleta() {
        return idDetalleBoleta;
    }

    public void setIdDetalleBoleta(int idDetalleBoleta) {
        this.idDetalleBoleta = idDetalleBoleta;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    @Override
    public String toString() {
        String nombreProducto = null;
        if (producto != null) {
            nombreProducto = producto.getNombre();
        }

        return "  - Producto: " + nombreProducto + " | Cantidad: " + cantidad + " | Subtotal: " + subTotal;
    }
}
