package patitasarriba.modelo.boleta;

import patitasarriba.modelo.Registro;

public abstract class DetalleBoleta extends Registro {
    private int idDetalleBoleta;
    private Boleta boleta;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;

    public DetalleBoleta() {
    }

    public DetalleBoleta(int idDetalleBoleta, boolean activo, Boleta boleta, int cantidad, double precioUnitario) {
        super(activo);
        setIdDetalleBoleta(idDetalleBoleta);
        setBoleta(boleta);
        setCantidad(cantidad);
        setPrecioUnitario(precioUnitario);
    }

    // Constructor de copia
    public DetalleBoleta(final DetalleBoleta detalleBoleta) {
        if (detalleBoleta == null) {
            throw new IllegalArgumentException("detalleBoleta no puede ser nulo");
        }
        super(detalleBoleta);
        setIdDetalleBoleta(detalleBoleta.getIdDetalleBoleta());
        setBoleta(detalleBoleta.getBoleta());
        setCantidad(detalleBoleta.getCantidad());
        setPrecioUnitario(detalleBoleta.getPrecioUnitario());
        setSubtotal(detalleBoleta.getSubtotal());
    }

    // Getters y Setters
    public int getIdDetalleBoleta() {
        return idDetalleBoleta;
    }

    public void setIdDetalleBoleta(int idDetalleBoleta) {
        if (idDetalleBoleta < 0) {
            throw new IllegalArgumentException("idDetalleBoleta no puede ser negativo");
        }
        this.idDetalleBoleta = idDetalleBoleta;
    }

    // boleta es el contenedor del detalle (composicion): se guarda la referencia, no una copia,
    // para que el detalle siga apuntando a la misma boleta que lo contiene
    public Boleta getBoleta() {
        return boleta;
    }

    public void setBoleta(Boleta boleta) {
        if (boleta == null) {
            throw new IllegalArgumentException("boleta no puede ser nula");
        }
        this.boleta = boleta;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("cantidad debe ser mayor que 0");
        }
        this.cantidad = cantidad;
        actualizarSubtotal();
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        if (precioUnitario < 0) {
            throw new IllegalArgumentException("precioUnitario no puede ser negativo");
        }
        this.precioUnitario = precioUnitario;
        actualizarSubtotal();
    }

    // En la base el subtotal es una columna calculada (cantidad * precio_unitario):
    // el modelo lo recalcula al cambiar la cantidad o el precio, y el DAO puede asignar el valor leido
    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        if (subtotal < 0) {
            throw new IllegalArgumentException("subtotal no puede ser negativo");
        }
        this.subtotal = subtotal;
    }

    private void actualizarSubtotal() {
        this.subtotal = this.cantidad * this.precioUnitario;
    }

    @Override
    public String toString() {
        int idBoleta = -1;
        if (boleta != null) {
            idBoleta = boleta.getIdBoleta();
        }

        return "idDetalleBoleta=" + idDetalleBoleta +
                ", " + super.toString() +
                ", boleta=" + idBoleta +
                ", cantidad=" + cantidad +
                ", precioUnitario=" + precioUnitario +
                ", subtotal=" + subtotal;
    }
}
