package patitasarriba.rest.dto;

// Referencia liviana a un InsumoUtilizado YA EXISTENTE (de una atencion ya registrada), usada
// dentro de DetalleBoletaArticuloDTO para cobrar un insumo. No existe un InsumoUtilizadoBL propio
// (ver InsumoUtilizadoResolver), por eso se necesitan los dos ids para ubicarlo.
public class InsumoUtilizadoRefDTO {
    private int id;
    private int idAtencion;

    public InsumoUtilizadoRefDTO() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdAtencion() {
        return idAtencion;
    }

    public void setIdAtencion(int idAtencion) {
        this.idAtencion = idAtencion;
    }
}
