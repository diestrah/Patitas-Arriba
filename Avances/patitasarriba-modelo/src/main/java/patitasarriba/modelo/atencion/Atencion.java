package patitasarriba.modelo.atencion;

import patitasarriba.modelo.Registro;
import patitasarriba.modelo.cita.DetalleCita;
import patitasarriba.modelo.producto.Articulo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Ejecucion de un servicio pedido en la cita (relacion 1 a 1 con DetalleCita)
public class Atencion extends Registro {
    private int idAtencion;
    private DetalleCita detalleCita;
    private List<InsumoUtilizado> insumosUtilizados;
    private List<AtencionDiagnostico> diagnosticos;
    private LocalDateTime fechaHora;
    private String motivoConsulta;
    private String observaciones;

    public Atencion() {
        this.insumosUtilizados = new ArrayList<>();
        this.diagnosticos = new ArrayList<>();
    }

    public Atencion(int idAtencion, boolean activo, DetalleCita detalleCita, List<InsumoUtilizado> insumosUtilizados,
                    List<AtencionDiagnostico> diagnosticos, LocalDateTime fechaHora, String motivoConsulta,
                    String observaciones) {
        super(activo);
        setIdAtencion(idAtencion);
        setDetalleCita(detalleCita);
        setInsumosUtilizados(insumosUtilizados);
        setDiagnosticos(diagnosticos);
        setFechaHora(fechaHora);
        setMotivoConsulta(motivoConsulta);
        setObservaciones(observaciones);
    }

    // Constructor de copia
    public Atencion(final Atencion atencion) {
        if (atencion == null) {
            throw new IllegalArgumentException("atencion no puede ser nula");
        }
        super(atencion);
        setIdAtencion(atencion.getIdAtencion());
        setDetalleCita(atencion.getDetalleCita());
        setInsumosUtilizados(atencion.getInsumosUtilizados());
        setDiagnosticos(atencion.getDiagnosticos());
        setFechaHora(atencion.getFechaHora());
        setMotivoConsulta(atencion.getMotivoConsulta());
        setObservaciones(atencion.getObservaciones());
    }

    // Getters y Setters
    public int getIdAtencion() {
        return idAtencion;
    }

    public void setIdAtencion(int idAtencion) {
        if (idAtencion < 0) {
            throw new IllegalArgumentException("idAtencion no puede ser negativo");
        }
        this.idAtencion = idAtencion;
    }

    public DetalleCita getDetalleCita() {
        if (detalleCita == null) {
            return null;
        }
        return new DetalleCita(detalleCita);
    }

    public void setDetalleCita(DetalleCita detalleCita) {
        if (detalleCita == null) {
            throw new IllegalArgumentException("detalleCita no puede ser nulo");
        }
        this.detalleCita = new DetalleCita(detalleCita);
        // La copia guardada apunta a esta atencion (relacion 1 a 1)
        this.detalleCita.setAtencion(this);
    }

    public List<InsumoUtilizado> getInsumosUtilizados() {
        return Collections.unmodifiableList(insumosUtilizados);
    }

    public void setInsumosUtilizados(List<InsumoUtilizado> insumosUtilizados) {
        if (insumosUtilizados == null) {
            throw new IllegalArgumentException("insumosUtilizados no puede ser nulo");
        }
        for (InsumoUtilizado insumoUtilizado : insumosUtilizados) {
            if (insumoUtilizado == null) {
                throw new IllegalArgumentException("insumosUtilizados no puede contener elementos nulos");
            }
        }
        this.insumosUtilizados = new ArrayList<>(insumosUtilizados);
    }

    public List<AtencionDiagnostico> getDiagnosticos() {
        return Collections.unmodifiableList(diagnosticos);
    }

    public void setDiagnosticos(List<AtencionDiagnostico> diagnosticos) {
        if (diagnosticos == null) {
            throw new IllegalArgumentException("diagnosticos no puede ser nulo");
        }
        for (AtencionDiagnostico diagnostico : diagnosticos) {
            if (diagnostico == null) {
                throw new IllegalArgumentException("diagnosticos no puede contener elementos nulos");
            }
        }
        this.diagnosticos = new ArrayList<>(diagnosticos);
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        if (fechaHora == null) {
            throw new IllegalArgumentException("fechaHora no puede ser nula");
        }
        this.fechaHora = fechaHora;
    }

    public String getMotivoConsulta() {
        return motivoConsulta;
    }

    public void setMotivoConsulta(String motivoConsulta) {
        // motivoConsulta es opcional: una atencion estetica no tiene motivo de consulta.
        // La capa de negocio lo exige en los servicios medicos.
        this.motivoConsulta = motivoConsulta;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        // observaciones es opcional: una atencion puede no tener notas adicionales
        this.observaciones = observaciones;
    }

    public void agregarInsumoUtilizado(int idInsumoUtilizado, boolean activo, Articulo articulo,
                                       int cantidadUtilizada, boolean esCobrable) {
        insumosUtilizados.add(new InsumoUtilizado(idInsumoUtilizado, activo, this, articulo, cantidadUtilizada, esCobrable));
    }

    public void agregarDiagnostico(boolean activo, Diagnostico diagnostico, NivelGravedad nivelGravedad,
                                   String detalleDiagnostico) {
        diagnosticos.add(new AtencionDiagnostico(activo, this, diagnostico, nivelGravedad, detalleDiagnostico));
    }

    @Override
    public String toString() {
        int idDetalleCita = -1;
        if (detalleCita != null) {
            idDetalleCita = detalleCita.getIdDetalleCita();
        }

        return "Atencion{" +
                "idAtencion=" + idAtencion +
                ", " + super.toString() +
                ", detalleCita=" + idDetalleCita +
                ", fechaHora=" + fechaHora +
                ", motivoConsulta='" + motivoConsulta + '\'' +
                ", observaciones='" + observaciones + '\'' +
                ", insumosUtilizados=" + insumosUtilizados +
                ", diagnosticos=" + diagnosticos +
                '}';
    }
}
