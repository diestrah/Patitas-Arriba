package patitasarriba.rest.mapper;

import patitasarriba.bl.excepciones.BLException;
import patitasarriba.bl.atencion.DiagnosticoBL;
import patitasarriba.bl.excepciones.NoEncontradoException;
import patitasarriba.bl.excepciones.ValidacionException;
import patitasarriba.blimpl.atencion.DiagnosticoBLImpl;
import patitasarriba.modelo.atencion.Atencion;
import patitasarriba.modelo.atencion.AtencionDiagnostico;
import patitasarriba.modelo.atencion.Diagnostico;
import patitasarriba.modelo.atencion.NivelGravedad;
import patitasarriba.rest.dto.AtencionDiagnosticoDTO;

import java.util.ArrayList;
import java.util.List;

// AtencionDiagnostico no es un recurso propio: viaja embebido dentro de AtencionDTO. No tiene id
// propio en la base (clave compuesta id_atencion + id_diagnostico), por eso el DTO tampoco lo tiene.
public final class AtencionDiagnosticoMapper {
    private static final DiagnosticoBL diagnosticoBL = new DiagnosticoBLImpl();

    private AtencionDiagnosticoMapper() {
    }

    public static AtencionDiagnosticoDTO toDTO(AtencionDiagnostico diagnostico) {
        AtencionDiagnosticoDTO dto = new AtencionDiagnosticoDTO();
        dto.setActivo(diagnostico.isActivo());
        dto.setDiagnostico(diagnostico.getDiagnostico() != null ? DiagnosticoMapper.toDTO(diagnostico.getDiagnostico()) : null);
        dto.setNivelGravedad(diagnostico.getNivelGravedad().name());
        dto.setDetalleDiagnostico(diagnostico.getDetalleDiagnostico());
        return dto;
    }

    public static List<AtencionDiagnosticoDTO> toDTOs(List<AtencionDiagnostico> diagnosticos) {
        List<AtencionDiagnosticoDTO> dtos = new ArrayList<>();
        for (AtencionDiagnostico diagnostico : diagnosticos) {
            dtos.add(toDTO(diagnostico));
        }
        return dtos;
    }

    // atencion es el contenedor (AtencionDiagnostico.setAtencion exige que no sea nula): se pasa
    // la instancia que AtencionMapper esta construyendo.
    public static AtencionDiagnostico toModelo(AtencionDiagnosticoDTO dto, Atencion atencion) throws BLException {
        if (dto.getDiagnostico() == null) {
            throw new ValidacionException("Cada diagnostico debe tener un diagnostico del catalogo");
        }
        Diagnostico diagnosticoReal;
        try {
            diagnosticoReal = diagnosticoBL.findById(dto.getDiagnostico().getId());
        } catch (NoEncontradoException e) {
            throw new ValidacionException("No existe el diagnostico con id " + dto.getDiagnostico().getId());
        }

        AtencionDiagnostico atencionDiagnostico = new AtencionDiagnostico();
        try {
            atencionDiagnostico.setActivo(dto.isActivo());
            atencionDiagnostico.setAtencion(atencion);
            atencionDiagnostico.setDiagnostico(diagnosticoReal);
            atencionDiagnostico.setNivelGravedad(NivelGravedad.valueOf(dto.getNivelGravedad()));
            atencionDiagnostico.setDetalleDiagnostico(dto.getDetalleDiagnostico());
        } catch (IllegalArgumentException e) {
            throw new ValidacionException("Datos del diagnostico de la atencion invalidos: " + e.getMessage());
        }
        return atencionDiagnostico;
    }
}
