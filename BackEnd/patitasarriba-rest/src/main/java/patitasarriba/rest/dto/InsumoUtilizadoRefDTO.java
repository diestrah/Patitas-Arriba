package patitasarriba.rest.dto;

// Referencia liviana a un InsumoUtilizado YA EXISTENTE (de una atencion ya registrada), usada
// dentro de DetalleBoletaArticuloDTO para cobrar un insumo. AtencionBL.findInsumoById solo
// necesita el id del insumo para resolverlo; idAtencion se mantiene por informacion (de donde
// viene) al convertir a DTO de salida. idDetalleCita (solo de salida, en GET /boletas/pendientes)
// dice de que servicio de la cita viene el insumo; en las demas respuestas va nulo.
public class InsumoUtilizadoRefDTO {
    private int id;
    private int idAtencion;
    private Integer idDetalleCita;

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

    public Integer getIdDetalleCita() {
        return idDetalleCita;
    }

    public void setIdDetalleCita(Integer idDetalleCita) {
        this.idDetalleCita = idDetalleCita;
    }
}
