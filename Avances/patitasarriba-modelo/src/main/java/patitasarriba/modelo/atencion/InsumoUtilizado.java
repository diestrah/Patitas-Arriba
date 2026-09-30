package patitasarriba.modelo.atencion;

import patitasarriba.modelo.Registro;
import patitasarriba.modelo.producto.Articulo;

public class InsumoUtilizado extends Registro {
    private Articulo articulo;
    private int cantidadUtilizada;

    public InsumoUtilizado() {
    }

    public InsumoUtilizado(int id, boolean activo, Articulo articulo, int cantidadUtilizada) {
        super(id, activo);
        setArticulo(articulo);
        setCantidadUtilizada(cantidadUtilizada);
    }

    // Constructor de copia
    public InsumoUtilizado(final InsumoUtilizado insumoUtilizado) {
        if (insumoUtilizado == null) {
            throw new IllegalArgumentException("insumoUtilizado no puede ser nulo");
        }
        super(insumoUtilizado);
        setArticulo(insumoUtilizado.getArticulo());
        setCantidadUtilizada(insumoUtilizado.getCantidadUtilizada());
    }

    // Getters y Setters
    public Articulo getArticulo() {
        return articulo;
    }

    public void setArticulo(Articulo articulo) {
        if (articulo == null) {
            throw new IllegalArgumentException("articulo no puede ser nulo");
        }
        this.articulo = articulo;
    }

    public int getCantidadUtilizada() {
        return cantidadUtilizada;
    }

    public void setCantidadUtilizada(int cantidadUtilizada) {
        if (cantidadUtilizada <= 0) {
            throw new IllegalArgumentException("cantidadUtilizada debe ser mayor que 0");
        }
        this.cantidadUtilizada = cantidadUtilizada;
    }

    @Override
    public String toString() {
        String nombreArticulo = null;
        if (articulo != null) {
            nombreArticulo = articulo.getNombre();
        }

        return "InsumoUtilizado{" +
                super.toString() +
                "articulo=" + nombreArticulo +
                ", cantidadUtilizada=" + cantidadUtilizada +
                '}';
    }
}
