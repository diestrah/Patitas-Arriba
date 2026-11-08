package patitasarriba.rest.mapper;

import patitasarriba.bl.BLException;
import patitasarriba.bl.ValidacionException;
import patitasarriba.modelo.atencion.Atencion;
import patitasarriba.modelo.atencion.AtencionDiagnostico;
import patitasarriba.modelo.atencion.InsumoUtilizado;
import patitasarriba.modelo.cita.DetalleCita;
import patitasarriba.rest.dto.AtencionDTO;
import patitasarriba.rest.dto.AtencionDiagnosticoDTO;
import patitasarriba.rest.dto.InsumoUtilizadoDTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public final class AtencionMapper {
    private AtencionMapper() {
    }

    public static AtencionDTO toDTO(Atencion atencion) {
        AtencionDTO dto = new AtencionDTO();
        dto.setId(atencion.getId());
        dto.setActivo(atencion.isActivo());
        dto.setDetalleCita(atencion.getDetalleCita() != null ? DetalleCitaMapper.toDTO(atencion.getDetalleCita()) : null);
        dto.setFechaHora(atencion.getFechaHora().toString());
        dto.setMotivoConsulta(atencion.getMotivoConsulta());
        dto.setObservaciones(atencion.getObservaciones());
        dto.setInsumosUtilizados(InsumoUtilizadoMapper.toDTOs(atencion.getInsumosUtilizados()));
        dto.setDiagnosticos(AtencionDiagnosticoMapper.toDTOs(atencion.getDiagnosticos()));
        return dto;
    }

    public static List<AtencionDTO> toDTOs(List<Atencion> atenciones) {
        List<AtencionDTO> dtos = new ArrayList<>();
        for (Atencion atencion : atenciones) {
            dtos.add(toDTO(atencion));
        }
        return dtos;
    }

    // detalleCita se referencia por id: es el DetalleCita de una cita ya creada (no existe un
    // DetalleCitaBL propio, ver DetalleCitaResolver). Los insumos y diagnosticos se construyen
    // despues de tener la Atencion en memoria, porque sus setAtencion() exigen una Atencion no nula.
    public static Atencion toModelo(AtencionDTO dto) throws BLException {
        if (dto.getDetalleCita() == null) {
            throw new ValidacionException("La atencion debe estar asociada a un detalle de cita");
        }
        DetalleCita detalleCita = DetalleCitaResolver.buscarPorId(dto.getDetalleCita().getId());

        Atencion atencion = new Atencion();
        try {
            atencion.setId(dto.getId());
            atencion.setActivo(dto.isActivo());
            atencion.setDetalleCita(detalleCita);
            atencion.setFechaHora(LocalDateTime.parse(dto.getFechaHora()));
            atencion.setMotivoConsulta(dto.getMotivoConsulta());
            atencion.setObservaciones(dto.getObservaciones());
        } catch (IllegalArgumentException | DateTimeParseException e) {
            throw new ValidacionException("Datos de la atencion invalidos: " + e.getMessage());
        }

        List<InsumoUtilizado> insumos = new ArrayList<>();
        for (InsumoUtilizadoDTO insumoDTO : dto.getInsumosUtilizados()) {
            insumos.add(InsumoUtilizadoMapper.toModelo(insumoDTO, atencion));
        }
        atencion.setInsumosUtilizados(insumos);

        List<AtencionDiagnostico> diagnosticos = new ArrayList<>();
        for (AtencionDiagnosticoDTO diagnosticoDTO : dto.getDiagnosticos()) {
            diagnosticos.add(AtencionDiagnosticoMapper.toModelo(diagnosticoDTO, atencion));
        }
        atencion.setDiagnosticos(diagnosticos);

        return atencion;
    }
}
