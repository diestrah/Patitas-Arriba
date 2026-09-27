package patitasarriba.modelo.boleta;

import patitasarriba.modelo.Registro;
import patitasarriba.modelo.producto.Producto;

public class DetalleBoleta extends Registro {
    private int cantidad;
    private double subTotal;
    private Producto producto;

    public DetalleBoleta() {
    }

    public DetalleBoleta(int id, boolean activo, int cantidad, double subTotal, Producto producto) {
        super(id, activo);
        setCantidad(cantidad);
        setSubTotal(subTotal);
        setProducto(producto);
    }

    // Constructor de copia
    public DetalleBoleta(final DetalleBoleta detalleBoleta) {
        if (detalleBoleta == null) {
            throw new IllegalArgumentException("detalleBoleta no puede ser nulo");
        }
        super(detalleBoleta);
        setCantidad(detalleBoleta.getCantidad());
        setSubTotal(detalleBoleta.getSubTotal());
        setProducto(detalleBoleta.getProducto());
    }

    // Getters y Setters
    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("cantidad debe ser mayor que 0");
        }
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
        if (producto == null) {
            throw new IllegalArgumentException("producto no puede ser nulo");
        }
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
