package patitasarriba.modelo.atencion;

import patitasarriba.modelo.Registro;
import patitasarriba.modelo.usuario.Veterinario;
import patitasarriba.modelo.receta.Receta;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AtencionMedica extends Registro {
    private LocalDateTime fechaHora;
    private String motivoConsulta;
    private double pesoFisico;
    private String observaciones;
    private Veterinario veterinario;
    private Receta receta;
    private List<AtencionTratamiento> tratamientos;
    private List<AtencionDiagnostico> diagnosticos;

    public AtencionMedica() {
        this.tratamientos = new ArrayList<>();
        this.diagnosticos = new ArrayList<>();
    }

    public AtencionMedica(int id, boolean activo, LocalDateTime fechaHora, String motivoConsulta, double pesoFisico, String observaciones, Veterinario veterinario, Receta receta, List<AtencionTratamiento> tratamientos, List<AtencionDiagnostico> diagnosticos) {
        super(id, activo);
        setFechaHora(fechaHora);
        setMotivoConsulta(motivoConsulta);
        setPesoFisico(pesoFisico);
        setObservaciones(observaciones);
        setVeterinario(veterinario);
        setReceta(receta);
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
        setVeterinario(atencionMedica.getVeterinario());
        setReceta(atencionMedica.getReceta());
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

    public Veterinario getVeterinario() {
        return veterinario;
    }

    public void setVeterinario(Veterinario veterinario) {
        if (veterinario == null) {
            throw new IllegalArgumentException("veterinario no puede ser nulo");
        }
        this.veterinario = veterinario;
    }

    public Receta getReceta() {
        return receta;
    }

    public void setReceta(Receta receta) {
        // receta es opcional: se asigna recien cuando termina la consulta
        this.receta = receta;
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
        String dniVeterinario = null;
        if (veterinario != null) {
            dniVeterinario = veterinario.getDni();
        }

        int idReceta = -1;
        if (receta != null) {
            idReceta = receta.getId();
        }

        String textoReceta;
        if (idReceta == -1) {
            textoReceta = "ninguna";
        } else {
            textoReceta = String.valueOf(idReceta);
        }

        return "AtencionMedica{" +
                super.toString() +
                ", fechaHora=" + fechaHora +
                ", motivoConsulta='" + motivoConsulta + "'" +
                ", pesoFisico=" + pesoFisico +
                ", observaciones='" + observaciones + "'" +
                ", veterinario=" + dniVeterinario +
                ", receta=" + textoReceta +
                ", tratamientos=" + tratamientos +
                ", diagnosticos=" + diagnosticos +
                '}';
    }
}
