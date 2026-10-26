package patitasarriba.modelo.atencion;

import patitasarriba.modelo.Registro;
import patitasarriba.modelo.producto.Articulo;

public class InsumoUtilizado extends Registro {
    private int idInsumoUtilizado;
    private Atencion atencion;
    private Articulo articulo;
    private int cantidadUtilizada;
    // true = lo paga el cliente aparte (va a la boleta); false = incluido en el precio del servicio
    private boolean esCobrable;

    public InsumoUtilizado() {
    }

    public InsumoUtilizado(int idInsumoUtilizado, boolean activo, Atencion atencion, Articulo articulo,
                           int cantidadUtilizada, boolean esCobrable) {
        super(activo);
        setIdInsumoUtilizado(idInsumoUtilizado);
        setAtencion(atencion);
        setArticulo(articulo);
        setCantidadUtilizada(cantidadUtilizada);
        setEsCobrable(esCobrable);
    }

    // Constructor de copia
    public InsumoUtilizado(final InsumoUtilizado insumoUtilizado) {
        if (insumoUtilizado == null) {
            throw new IllegalArgumentException("insumoUtilizado no puede ser nulo");
        }
        super(insumoUtilizado);
        setIdInsumoUtilizado(insumoUtilizado.getIdInsumoUtilizado());
        setAtencion(insumoUtilizado.getAtencion());
        setArticulo(insumoUtilizado.getArticulo());
        setCantidadUtilizada(insumoUtilizado.getCantidadUtilizada());
        setEsCobrable(insumoUtilizado.isEsCobrable());
    }

    // Getters y Setters
    public int getIdInsumoUtilizado() {
        return idInsumoUtilizado;
    }

    public void setIdInsumoUtilizado(int idInsumoUtilizado) {
        if (idInsumoUtilizado < 0) {
            throw new IllegalArgumentException("idInsumoUtilizado no puede ser negativo");
        }
        this.idInsumoUtilizado = idInsumoUtilizado;
    }

    // atencion es el contenedor del insumo (composicion): se guarda la referencia, no una copia,
    // para que el insumo siga apuntando a la misma atencion que lo contiene
    public Atencion getAtencion() {
        return atencion;
    }

    public void setAtencion(Atencion atencion) {
        if (atencion == null) {
            throw new IllegalArgumentException("atencion no puede ser nula");
        }
        this.atencion = atencion;
    }

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

    public int getCantidadUtilizada() {
        return cantidadUtilizada;
    }

    public void setCantidadUtilizada(int cantidadUtilizada) {
        if (cantidadUtilizada <= 0) {
            throw new IllegalArgumentException("cantidadUtilizada debe ser mayor que 0");
        }
        this.cantidadUtilizada = cantidadUtilizada;
    }

    public boolean isEsCobrable() {
        return esCobrable;
    }

    public void setEsCobrable(boolean esCobrable) {
        this.esCobrable = esCobrable;
    }

    @Override
    public String toString() {
        int idAtencion = -1;
        if (atencion != null) {
            idAtencion = atencion.getIdAtencion();
        }

        String nombreArticulo = null;
        if (articulo != null) {
            nombreArticulo = articulo.getNombre();
        }

        return "InsumoUtilizado{" +
                "idInsumoUtilizado=" + idInsumoUtilizado +
                ", " + super.toString() +
                ", atencion=" + idAtencion +
                ", articulo=" + nombreArticulo +
                ", cantidadUtilizada=" + cantidadUtilizada +
                ", esCobrable=" + esCobrable +
                '}';
    }
}
