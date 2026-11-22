package patitasarriba.rest.mapper;

import patitasarriba.bl.atencion.AtencionBL;
import patitasarriba.bl.excepciones.BLException;
import patitasarriba.bl.excepciones.NoEncontradoException;
import patitasarriba.bl.excepciones.ValidacionException;
import patitasarriba.blimpl.atencion.AtencionBLImpl;
import patitasarriba.modelo.atencion.Atencion;
import patitasarriba.modelo.receta.DetalleReceta;
import patitasarriba.modelo.receta.Receta;
import patitasarriba.rest.dto.DetalleRecetaDTO;
import patitasarriba.rest.dto.RecetaDTO;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public final class RecetaMapper {
    private static final AtencionBL atencionBL = new AtencionBLImpl();

    private RecetaMapper() {
    }

    public static RecetaDTO toDTO(Receta receta) {
        RecetaDTO dto = new RecetaDTO();
        dto.setId(receta.getId());
        dto.setActivo(receta.isActivo());
        dto.setAtencion(receta.getAtencion() != null ? AtencionMapper.toDTO(receta.getAtencion()) : null);
        dto.setFechaEmision(receta.getFechaEmision().toString());
        dto.setIndicacionesGenerales(receta.getIndicacionesGenerales());
        dto.setDetalles(DetalleRecetaMapper.toDTOs(receta.getDetalles()));
        return dto;
    }

    public static List<RecetaDTO> toDTOs(List<Receta> recetas) {
        List<RecetaDTO> dtos = new ArrayList<>();
        for (Receta receta : recetas) {
            dtos.add(toDTO(receta));
        }
        return dtos;
    }

    // la atencion se referencia por id: ya debe existir (y no puede ser de un servicio estetico,
    // eso lo valida RecetaBLImpl.validarServicioMedico). Los detalles se construyen despues de
    // tener la Receta en memoria, porque DetalleReceta.setReceta exige una Receta no nula.
    public static Receta toModelo(RecetaDTO dto) throws BLException {
        if (dto.getAtencion() == null) {
            throw new ValidacionException("La receta debe estar asociada a una atencion medica");
        }
        Atencion atencion;
        try {
            atencion = atencionBL.findById(dto.getAtencion().getId());
        } catch (NoEncontradoException e) {
            throw new ValidacionException("No existe la atencion con id " + dto.getAtencion().getId());
        }

        Receta receta = new Receta();
        try {
            receta.setId(dto.getId());
            receta.setActivo(dto.isActivo());
            receta.setAtencion(atencion);
            receta.setFechaEmision(LocalDate.parse(dto.getFechaEmision()));
            receta.setIndicacionesGenerales(dto.getIndicacionesGenerales());
        } catch (IllegalArgumentException | DateTimeParseException e) {
            throw new ValidacionException("Datos de la receta invalidos: " + e.getMessage());
        }

        if (dto.getDetalles() == null || dto.getDetalles().isEmpty()) {
            throw new ValidacionException("La receta debe tener al menos un detalle");
        }
        List<DetalleReceta> detalles = new ArrayList<>();
        for (DetalleRecetaDTO detalleDTO : dto.getDetalles()) {
            detalles.add(DetalleRecetaMapper.toModelo(detalleDTO, receta));
        }
        receta.setDetalles(detalles);

        return receta;
    }
}
