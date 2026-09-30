package patitasarriba.modelo.atencion;

import patitasarriba.modelo.Registro;
import patitasarriba.modelo.mascota.Mascota;
import patitasarriba.modelo.cita.Cita;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AtencionMedica extends Registro {
    private LocalDateTime fechaHora;
    private String motivoConsulta;
    private double pesoFisico;
    private String observaciones;
    private Mascota mascota;
    private Cita cita;
    private List<AtencionTratamiento> tratamientos;
    private List<AtencionDiagnostico> diagnosticos;

    public AtencionMedica() {
        this.tratamientos = new ArrayList<>();
        this.diagnosticos = new ArrayList<>();
    }

    public AtencionMedica(int id, boolean activo, LocalDateTime fechaHora, String motivoConsulta, double pesoFisico, String observaciones, Mascota mascota, Cita cita, List<AtencionTratamiento> tratamientos, List<AtencionDiagnostico> diagnosticos) {
        super(id, activo);
        setFechaHora(fechaHora);
        setMotivoConsulta(motivoConsulta);
        setPesoFisico(pesoFisico);
        setObservaciones(observaciones);
        setMascota(mascota);
        setCita(cita);
        setTratamientos(tratamientos);
        setDiagnosticos(diagnosticos);
    }

    // Constructor de copia
    public AtencionMedica(final AtencionMedica atencionMedica) {
        if (atencionMedica == null) {
            throw new IllegalArgumentException("atencionMedica no puede ser nula");
        }
        super(atencionMedica);
        setFechaHora(atencionMedica.getFechaHora());
        setMotivoConsulta(atencionMedica.getMotivoConsulta());
        setPesoFisico(atencionMedica.getPesoFisico());
        setObservaciones(atencionMedica.getObservaciones());
        setMascota(atencionMedica.getMascota());
        setCita(atencionMedica.getCita());
        setTratamientos(atencionMedica.getTratamientos());
        setDiagnosticos(atencionMedica.getDiagnosticos());
    }

    // Getters y Setters
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
        if (motivoConsulta == null || motivoConsulta.isEmpty()) {
            throw new IllegalArgumentException("motivoConsulta no puede ser nulo o vacío");
        }
        this.motivoConsulta = motivoConsulta;
    }

    public double getPesoFisico() {
        return pesoFisico;
    }

    public void setPesoFisico(double pesoFisico) {
        this.pesoFisico = pesoFisico;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        // observaciones es opcional: una atencion puede no tener notas adicionales
        this.observaciones = observaciones;
    }

    public Mascota getMascota() {
        return mascota;
    }

    public void setMascota(Mascota mascota) {
        if (mascota == null) {
            throw new IllegalArgumentException("mascota no puede ser nula");
        }
        this.mascota = mascota;
    }

    public Cita getCita() {
        return cita;
    }

    public void setCita(Cita cita) {
        if (cita == null) {
            throw new IllegalArgumentException("cita no puede ser nula");
        }
        this.cita = cita;
    }

    public List<AtencionTratamiento> getTratamientos() {
        return Collections.unmodifiableList(tratamientos);
    }

    public void setTratamientos(List<AtencionTratamiento> tratamientos) {
        if (tratamientos == null) {
            throw new IllegalArgumentException("tratamientos no puede ser nulo");
        }
        this.tratamientos = new ArrayList<>(tratamientos);
    }

    public List<AtencionDiagnostico> getDiagnosticos() {
        return Collections.unmodifiableList(diagnosticos);
    }

    public void setDiagnosticos(List<AtencionDiagnostico> diagnosticos) {
        if (diagnosticos == null) {
            throw new IllegalArgumentException("diagnosticos no puede ser nulo");
        }
        this.diagnosticos = new ArrayList<>(diagnosticos);
    }

    public void agregarDiagnostico(int idAtencionDiagnostico, boolean activo, NivelGravedad nivelGravedad, String detalleDiagnostico, Diagnostico diagnostico) {
        diagnosticos.add(new AtencionDiagnostico(idAtencionDiagnostico, activo, nivelGravedad, detalleDiagnostico, diagnostico));
    }

    // Retorna el tratamiento creado para poder agregarle insumos despues
    public AtencionTratamiento agregarTratamiento(int idAtencionTratamiento, boolean activo, Tratamiento tratamiento) {
        AtencionTratamiento atencionTratamiento = new AtencionTratamiento(idAtencionTratamiento, activo, tratamiento, new ArrayList<>());
        tratamientos.add(atencionTratamiento);
        return atencionTratamiento;
    }

    @Override
    public String toString() {
        String nombreMascota = null;
        if (mascota != null) {
            nombreMascota = mascota.getNombre();
        }

        int idCita = -1;
        if (cita != null) {
            idCita = cita.getId();
        }

        return "AtencionMedica{" +
                super.toString() +
                ", fechaHora=" + fechaHora +
                ", motivoConsulta='" + motivoConsulta + "'" +
                ", pesoFisico=" + pesoFisico +
                ", observaciones='" + observaciones + "'" +
                ", mascota=" + nombreMascota +
                ", cita=" + idCita +
                ", tratamientos=" + tratamientos +
                ", diagnosticos=" + diagnosticos +
                '}';
    }
}
