package patitasarriba.modelo.producto;

public class Servicio extends Producto {
    private TipoServicio tipo;
    private boolean requiereTriaje;
    private boolean requiereVacuna;
    private int duracionEstimada;

    public Servicio() {
    }

    public Servicio(int id, boolean activo, String nombre, double precioBase, String descripcion,
                    TipoServicio tipo, boolean requiereTriaje,
                    boolean requiereVacuna, int duracionEstimada) {
        super(id, activo, nombre, precioBase, descripcion);
        setTipo(tipo);
        setRequiereTriaje(requiereTriaje);
        setRequiereVacuna(requiereVacuna);
        setDuracionEstimada(duracionEstimada);
    }

    // Constructor de copia
    public Servicio(final Servicio servicio) {
        if (servicio == null) {
            throw new IllegalArgumentException("Servicio no puede ser nulo");
        }
        super(servicio);
        setTipo(servicio.getTipo());
        setRequiereTriaje(servicio.isRequiereTriaje());
        setRequiereVacuna(servicio.isRequiereVacuna());
        setDuracionEstimada(servicio.getDuracionEstimada());
    }

    // setters y getters

    public TipoServicio getTipo() {
        return tipo;
    }

    public void setTipo(TipoServicio tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("tipo no puede ser nulo");
        }
        this.tipo = tipo;
    }

    public boolean isRequiereTriaje() {
        return requiereTriaje;
    }

    public void setRequiereTriaje(boolean requiereTriaje) {
        this.requiereTriaje = requiereTriaje;
    }

    public boolean isRequiereVacuna() {
        return requiereVacuna;
    }

    public void setRequiereVacuna(boolean requiereVacuna) {
        this.requiereVacuna = requiereVacuna;
    }

    public int getDuracionEstimada() {
        return duracionEstimada;
    }

    public void setDuracionEstimada(int duracionEstimada) {
        if (duracionEstimada <= 0) {
            throw new IllegalArgumentException("Duracion estimada debe ser mayor que 0");
        }
        this.duracionEstimada = duracionEstimada;
    }

    public String toString() {
        return "Servicio{" +
                super.toString() +
                ", duracionEstimada=" + duracionEstimada +
                ", requiereVacuna=" + requiereVacuna +
                ", requiereTriaje=" + requiereTriaje +
                ", tipoServicio=" + tipo +
                '}';
    }
}
