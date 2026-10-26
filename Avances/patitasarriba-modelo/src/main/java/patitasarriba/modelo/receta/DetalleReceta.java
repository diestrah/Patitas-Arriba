package patitasarriba.modelo.receta;

import patitasarriba.modelo.Registro;
import patitasarriba.modelo.producto.Articulo;

public class DetalleReceta extends Registro {
    private int idDetalleReceta;
    private Receta receta;
    private Articulo articulo;
    private String dosis;
    private String frecuencia;
    private int duracionDias;
    private int cantidadTotal;

    public DetalleReceta() {
    }

    public DetalleReceta(int idDetalleReceta, boolean activo, Receta receta, Articulo articulo, String dosis,
                         String frecuencia, int duracionDias, int cantidadTotal) {
        super(activo);
        setIdDetalleReceta(idDetalleReceta);
        setReceta(receta);
        setArticulo(articulo);
        setDosis(dosis);
        setFrecuencia(frecuencia);
        setDuracionDias(duracionDias);
        setCantidadTotal(cantidadTotal);
    }

    // Constructor de copia
    public DetalleReceta(final DetalleReceta detalleReceta) {
        if (detalleReceta == null) {
            throw new IllegalArgumentException("detalleReceta no puede ser nulo");
        }
        super(detalleReceta);
        setIdDetalleReceta(detalleReceta.getIdDetalleReceta());
        setReceta(detalleReceta.getReceta());
        setArticulo(detalleReceta.getArticulo());
        setDosis(detalleReceta.getDosis());
        setFrecuencia(detalleReceta.getFrecuencia());
        setDuracionDias(detalleReceta.getDuracionDias());
        setCantidadTotal(detalleReceta.getCantidadTotal());
    }

    // Getters y Setters
    public int getIdDetalleReceta() {
        return idDetalleReceta;
    }

    public void setIdDetalleReceta(int idDetalleReceta) {
        if (idDetalleReceta < 0) {
            throw new IllegalArgumentException("idDetalleReceta no puede ser negativo");
        }
        this.idDetalleReceta = idDetalleReceta;
    }

    // receta es el contenedor del detalle (composicion): se guarda la referencia, no una copia,
    // para que el detalle siga apuntando a la misma receta que lo contiene
    public Receta getReceta() {
        return receta;
    }

    public void setReceta(Receta receta) {
        if (receta == null) {
            throw new IllegalArgumentException("receta no puede ser nula");
        }
        this.receta = receta;
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

    public String getDosis() {
        return dosis;
    }

    public void setDosis(String dosis) {
        if (dosis == null || dosis.isBlank()) {
            throw new IllegalArgumentException("dosis no puede ser nula o vacía");
        }
        this.dosis = dosis;
    }

    public String getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(String frecuencia) {
        if (frecuencia == null || frecuencia.isBlank()) {
            throw new IllegalArgumentException("frecuencia no puede ser nula o vacía");
        }
        this.frecuencia = frecuencia;
    }

    public int getDuracionDias() {
        return duracionDias;
    }

    public void setDuracionDias(int duracionDias) {
        if (duracionDias <= 0) {
            throw new IllegalArgumentException("duracionDias debe ser mayor que 0");
        }
        this.duracionDias = duracionDias;
    }

    public int getCantidadTotal() {
        return cantidadTotal;
    }

    public void setCantidadTotal(int cantidadTotal) {
        if (cantidadTotal <= 0) {
            throw new IllegalArgumentException("cantidadTotal debe ser mayor que 0");
        }
        this.cantidadTotal = cantidadTotal;
    }

    @Override
    public String toString() {
        int idReceta = -1;
        if (receta != null) {
            idReceta = receta.getIdReceta();
        }

        String nombreArticulo = null;
        if (articulo != null) {
            nombreArticulo = articulo.getNombre();
        }

        return "DetalleReceta{" +
                "idDetalleReceta=" + idDetalleReceta +
                ", " + super.toString() +
                ", receta=" + idReceta +
                ", articulo=" + nombreArticulo +
                ", dosis='" + dosis + '\'' +
                ", frecuencia='" + frecuencia + '\'' +
                ", duracionDias=" + duracionDias +
                ", cantidadTotal=" + cantidadTotal +
                '}';
    }
}
