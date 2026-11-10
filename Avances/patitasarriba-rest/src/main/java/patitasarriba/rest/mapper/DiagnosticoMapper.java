package patitasarriba.rest.mapper;

import patitasarriba.bl.ValidacionException;
import patitasarriba.modelo.atencion.Diagnostico;
import patitasarriba.rest.dto.DiagnosticoDTO;

import java.util.ArrayList;
import java.util.List;

public final class DiagnosticoMapper {
    private DiagnosticoMapper() {
    }

    public static DiagnosticoDTO toDTO(Diagnostico diagnostico) {
        DiagnosticoDTO dto = new DiagnosticoDTO();
        dto.setId(diagnostico.getId());
        dto.setActivo(diagnostico.isActivo());
        dto.setNombreEnfermedad(diagnostico.getNombreEnfermedad());
        dto.setDescripcion(diagnostico.getDescripcion());
        return dto;
    }

    public static List<DiagnosticoDTO> toDTOs(List<Diagnostico> diagnosticos) {
        List<DiagnosticoDTO> dtos = new ArrayList<>();
        for (Diagnostico diagnostico : diagnosticos) {
            dtos.add(toDTO(diagnostico));
        }
        return dtos;
    }

    public static Diagnostico toModelo(DiagnosticoDTO dto) throws ValidacionException {
        Diagnostico diagnostico = new Diagnostico();
        try {
            diagnostico.setId(dto.getId());
            diagnostico.setActivo(dto.isActivo());
            diagnostico.setNombreEnfermedad(dto.getNombreEnfermedad());
            diagnostico.setDescripcion(dto.getDescripcion());
        } catch (IllegalArgumentException e) {
            throw new ValidacionException("Datos del diagnostico invalidos: " + e.getMessage());
        }
        return diagnostico;
    }
}
