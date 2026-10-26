package patitasarriba.modelo.boleta;

import patitasarriba.modelo.atencion.InsumoUtilizado;
import patitasarriba.modelo.producto.Articulo;

public class DetalleBoletaArticulo extends DetalleBoleta {
    private Articulo articulo;
    // Opcional: null = venta directa en caja; con valor = cobro de un insumo usado en una atencion
    private InsumoUtilizado insumoUtilizado;

    public DetalleBoletaArticulo() {
    }

    public DetalleBoletaArticulo(int idDetalleBoleta, boolean activo, Boleta boleta, int cantidad,
                                 double precioUnitario, Articulo articulo, InsumoUtilizado insumoUtilizado) {
        super(idDetalleBoleta, activo, boleta, cantidad, precioUnitario);
        setArticulo(articulo);
        setInsumoUtilizado(insumoUtilizado);
    }

    // Constructor de copia
    public DetalleBoletaArticulo(final DetalleBoletaArticulo detalleBoletaArticulo) {
        if (detalleBoletaArticulo == null) {
            throw new IllegalArgumentException("detalleBoletaArticulo no puede ser nulo");
        }
        super(detalleBoletaArticulo);
        setArticulo(detalleBoletaArticulo.getArticulo());
        setInsumoUtilizado(detalleBoletaArticulo.getInsumoUtilizado());
    }

    // Getters y Setters
    public Articulo getArticulo() {
        if (articulo == null) {
            return null;
        }
        return new Articulo(articulo);
    }

    public void setArticulo(Articulo articulo) {
        if (articulo == null) {
            throw new IllegalArgumentException("articulo no puede ser nulo");
        }
        this.articulo = new Articulo(articulo);
    }

    public InsumoUtilizado getInsumoUtilizado() {
        if (insumoUtilizado == null) {
            return null;
        }
        return new InsumoUtilizado(insumoUtilizado);
    }

    public void setInsumoUtilizado(InsumoUtilizado insumoUtilizado) {
        // insumoUtilizado es opcional. Que su articulo coincida con el de la linea,
        // que sea cobrable y que la cantidad no supere la utilizada lo valida la capa de negocio
        if (insumoUtilizado == null) {
            this.insumoUtilizado = null;
        } else {
            this.insumoUtilizado = new InsumoUtilizado(insumoUtilizado);
        }
    }

    @Override
    public String toString() {
        String nombreArticulo = null;
        if (articulo != null) {
            nombreArticulo = articulo.getNombre();
        }

        int idInsumoUtilizado = -1;
        if (insumoUtilizado != null) {
            idInsumoUtilizado = insumoUtilizado.getIdInsumoUtilizado();
        }

        return "DetalleBoletaArticulo{" +
                super.toString() +
                ", articulo=" + nombreArticulo +
                ", insumoUtilizado=" + idInsumoUtilizado +
                '}';
    }
}
