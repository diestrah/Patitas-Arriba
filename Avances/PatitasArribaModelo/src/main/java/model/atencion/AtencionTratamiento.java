package model.atencion;

import java.util.List;

public class AtencionTratamiento {
    private Tratamiento tratamiento;
    private List<InsumoUtilizado> insumosUtilizados;


    public AtencionTratamiento(Tratamiento tratamiento, List<InsumoUtilizado> insumosUtilizados) {
        this.tratamiento = tratamiento;
        this.insumosUtilizados = insumosUtilizados;
    }

    // Getters y Setters
    public Tratamiento getTratamiento() {
        return tratamiento;
    }

    public void setTratamiento(Tratamiento tratamiento) {
        this.tratamiento = tratamiento;
    }

    public List<InsumoUtilizado> getInsumosUtilizados() {
        return insumosUtilizados;
    }

    public void setInsumosUtilizados(List<InsumoUtilizado> insumosUtilizados) {
        this.insumosUtilizados = insumosUtilizados;
    }

    @Override
    public String toString() {
        return "AtencionTratamiento{" +
                "tratamiento=" + tratamiento +
                ", insumosUtilizados=" + insumosUtilizados +
                '}';
    }
}
