package patitasarriba.modelo.atencion;

import patitasarriba.modelo.producto.Articulo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AtencionTratamiento {
    private Tratamiento tratamiento;
    private List<InsumoUtilizado> insumosUtilizados;


    public AtencionTratamiento(Tratamiento tratamiento, List<InsumoUtilizado> insumosUtilizados) {
        setTratamiento(tratamiento);
        setInsumosUtilizados(insumosUtilizados);
    }

    // Constructor de copia
    public AtencionTratamiento(AtencionTratamiento atencionTratamiento) {
        if (atencionTratamiento == null) {
            throw new IllegalArgumentException("atencionTratamiento no puede ser nulo");
        }
        setTratamiento(atencionTratamiento.getTratamiento());
        setInsumosUtilizados(atencionTratamiento.getInsumosUtilizados());
    }

    // Getters y Setters
    public Tratamiento getTratamiento() {
        return tratamiento;
    }

    public void setTratamiento(Tratamiento tratamiento) {
        if (tratamiento == null) {
            throw new IllegalArgumentException("tratamiento no puede ser nulo");
        }
        this.tratamiento = tratamiento;
    }

    public List<InsumoUtilizado> getInsumosUtilizados() {
        return Collections.unmodifiableList(insumosUtilizados);
    }

    public void setInsumosUtilizados(List<InsumoUtilizado> insumosUtilizados) {
        if (insumosUtilizados == null) {
            throw new IllegalArgumentException("insumosUtilizados no puede ser nulo");
        }
        this.insumosUtilizados = new ArrayList<>(insumosUtilizados);
    }

    public void agregarInsumo(Articulo articulo, int cantidadUtilizada, String unidadMedida) {
        insumosUtilizados.add(new InsumoUtilizado(articulo, cantidadUtilizada, unidadMedida));
    }

    @Override
    public String toString() {
        return "AtencionTratamiento{" +
                "tratamiento=" + tratamiento +
                ", insumosUtilizados=" + insumosUtilizados +
                '}';
    }
}
