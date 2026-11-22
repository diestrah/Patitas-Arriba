package patitasarriba.modelo;

// Padre de todas las clases abstractas y de las clases concretas sin padre.
// Solo guarda el estado logico del registro (borrado logico): cada clase define su propio id.
public abstract class Registro {
    // Por defecto activo, igual que la columna activo (DEFAULT 1) de la base
    private boolean activo = true;

    public Registro() {
    }

    public Registro(boolean activo) {
        setActivo(activo);
    }

    // Constructor de copia
    public Registro(final Registro registro) {
        if (registro == null) {
            throw new IllegalArgumentException("registro no puede ser nulo");
        }
        setActivo(registro.isActivo());
    }

    // Getters y Setters
    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return "activo=" + activo;
    }
}
