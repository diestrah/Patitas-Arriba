package patitasarriba.modelo.atencion;

import patitasarriba.modelo.Registro;
import patitasarriba.modelo.producto.Articulo;

public class InsumoUtilizado extends Registro {
    private Articulo articulo;
    private int cantidadUtilizada;
    private String unidadMedida;

    public InsumoUtilizado() {
    }

    public InsumoUtilizado(int id, boolean activo, Articulo articulo, int cantidadUtilizada, String unidadMedida) {
        super(id, activo);
        setArticulo(articulo);
        setCantidadUtilizada(cantidadUtilizada);
        setUnidadMedida(unidadMedida);
    }

    // Constructor de copia
    public InsumoUtilizado(final InsumoUtilizado insumoUtilizado) {
        if (insumoUtilizado == null) {
            throw new IllegalArgumentException("insumoUtilizado no puede ser nulo");
        }
        super(insumoUtilizado);
        setArticulo(insumoUtilizado.getArticulo());
        setCantidadUtilizada(insumoUtilizado.getCantidadUtilizada());
        setUnidadMedida(insumoUtilizado.getUnidadMedida());
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

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        if (unidadMedida == null || unidadMedida.isEmpty()) {
            throw new IllegalArgumentException("unidadMedida no puede ser nulo o vacío");
        }
        this.unidadMedida = unidadMedida;
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
                ", unidadMedida='" + unidadMedida + "'" +
                '}';
    }
}
