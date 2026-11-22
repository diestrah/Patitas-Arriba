package patitasarriba.modelo.producto;

public class Servicio extends Producto {
    private TipoServicio tipo;
    // Integer porque es opcional en la base: null = duracion no definida
    private Integer duracionEstimada;

    public Servicio() {
    }

    public Servicio(int idProducto, boolean activo, String nombre, double precioBase, String descripcion,
                    TipoServicio tipo, Integer duracionEstimada) {
        super(idProducto, activo, nombre, precioBase, descripcion);
        setTipo(tipo);
        setDuracionEstimada(duracionEstimada);
    }

    // Constructor de copia
    public Servicio(final Servicio servicio) {
        if (servicio == null) {
            throw new IllegalArgumentException("servicio no puede ser nulo");
        }
        super(servicio);
        setTipo(servicio.getTipo());
        setDuracionEstimada(servicio.getDuracionEstimada());
    }

    // Getters y Setters
    public TipoServicio getTipo() {
        return tipo;
    }

    public void setTipo(TipoServicio tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("tipo no puede ser nulo");
        }
        this.tipo = tipo;
    }

    public Integer getDuracionEstimada() {
        return duracionEstimada;
    }

    public void setDuracionEstimada(Integer duracionEstimada) {
        // duracionEstimada es opcional; si se indica, debe ser positiva (en minutos)
        if (duracionEstimada != null && duracionEstimada <= 0) {
            throw new IllegalArgumentException("duracionEstimada debe ser mayor que 0");
        }
        this.duracionEstimada = duracionEstimada;
    }

    @Override
    public String toString() {
        return "Servicio{" +
                super.toString() +
                ", tipo=" + tipo +
                ", duracionEstimada=" + duracionEstimada +
                '}';
    }
}
