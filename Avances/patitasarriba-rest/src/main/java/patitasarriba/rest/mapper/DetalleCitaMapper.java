package patitasarriba.rest.mapper;

import patitasarriba.bl.BLException;
import patitasarriba.bl.NoEncontradoException;
import patitasarriba.bl.ServicioBL;
import patitasarriba.bl.ValidacionException;
import patitasarriba.bl.VeterinarioBL;
import patitasarriba.bl.impl.producto.ServicioBLImpl;
import patitasarriba.bl.impl.usuario.VeterinarioBLImpl;
import patitasarriba.modelo.cita.Cita;
import patitasarriba.modelo.cita.DetalleCita;
import patitasarriba.modelo.producto.Servicio;
import patitasarriba.modelo.usuario.Veterinario;
import patitasarriba.rest.dto.DetalleCitaDTO;

import java.util.ArrayList;
import java.util.List;

// DetalleCita no es un recurso propio: viaja embebido dentro de CitaDTO (ver REST_SERVICIOS.md §5.12).
public final class DetalleCitaMapper {
    private static final ServicioBL servicioBL = new ServicioBLImpl();
    private static final VeterinarioBL veterinarioBL = new VeterinarioBLImpl();

    private DetalleCitaMapper() {
    }

    public static DetalleCitaDTO toDTO(DetalleCita detalle) {
        DetalleCitaDTO dto = new DetalleCitaDTO();
        dto.setId(detalle.getId());
        dto.setActivo(detalle.isActivo());
        dto.setIdCita(detalle.getCita() != null ? detalle.getCita().getId() : 0);
        dto.setObservaciones(detalle.getObservaciones());
        dto.setServicio(detalle.getServicio() != null ? ServicioMapper.toDTO(detalle.getServicio()) : null);
        dto.setVeterinario(detalle.getVeterinario() != null ? VeterinarioMapper.toDTO(detalle.getVeterinario()) : null);
        return dto;
    }

    public static List<DetalleCitaDTO> toDTOs(List<DetalleCita> detalles) {
        List<DetalleCitaDTO> dtos = new ArrayList<>();
        for (DetalleCita detalle : detalles) {
            dtos.add(toDTO(detalle));
        }
        return dtos;
    }

    // cita es el contenedor (DetalleCita.setCita exige que no sea nulo): se pasa la instancia
    // que CitaMapper esta construyendo, igual que Cita.agregarDetalle en el modelo.
    public static DetalleCita toModelo(DetalleCitaDTO dto, Cita cita) throws BLException {
        if (dto.getServicio() == null) {
            throw new ValidacionException("Cada servicio de la cita debe tener un servicio");
        }
        if (dto.getVeterinario() == null) {
            throw new ValidacionException("Cada servicio de la cita debe tener un veterinario");
        }
        Servicio servicio;
        try {
            servicio = servicioBL.findById(dto.getServicio().getId());
        } catch (NoEncontradoException e) {
            throw new ValidacionException("No existe el servicio con id " + dto.getServicio().getId());
        }
        Veterinario veterinario;
        try {
            veterinario = veterinarioBL.findById(dto.getVeterinario().getId());
        } catch (NoEncontradoException e) {
            throw new ValidacionException("No existe el veterinario con id " + dto.getVeterinario().getId());
        }

        DetalleCita detalle = new DetalleCita();
        try {
            detalle.setId(dto.getId());
            detalle.setActivo(dto.isActivo());
            detalle.setObservaciones(dto.getObservaciones());
            detalle.setServicio(servicio);
            detalle.setVeterinario(veterinario);
            detalle.setCita(cita);
        } catch (IllegalArgumentException e) {
            throw new ValidacionException("Datos del detalle de cita invalidos: " + e.getMessage());
        }
        return detalle;
    }
}
