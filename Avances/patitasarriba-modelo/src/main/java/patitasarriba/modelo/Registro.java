package patitasarriba.modelo;

public abstract class Registro {
    private int id;
    private boolean activo;

    public Registro() {
    }

    public Registro(final Registro registro) {
        if (registro == null) {
            throw new IllegalArgumentException("El registro no puede ser nulo");
        }
        setId(registro.getId());
        setActivo(registro.isActivo());
    }

    public Registro(int id, boolean activo) {
        this.id = id;
        this.activo = activo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return String.format("%-5d\t%-5b", id, activo);
    }
}

