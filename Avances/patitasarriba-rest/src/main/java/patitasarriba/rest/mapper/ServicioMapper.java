package patitasarriba.rest.mapper;

import patitasarriba.bl.excepciones.ValidacionException;
import patitasarriba.modelo.producto.Servicio;
import patitasarriba.modelo.producto.TipoServicio;
import patitasarriba.rest.dto.ServicioDTO;

import java.util.ArrayList;
import java.util.List;

public final class ServicioMapper {
    private ServicioMapper() {
    }

    public static ServicioDTO toDTO(Servicio servicio) {
        ServicioDTO dto = new ServicioDTO();
        dto.setId(servicio.getId());
        dto.setActivo(servicio.isActivo());
        dto.setNombre(servicio.getNombre());
        dto.setPrecioBase(servicio.getPrecioBase());
        dto.setDescripcion(servicio.getDescripcion());
        dto.setTipo(servicio.getTipo().name());
        dto.setDuracionEstimada(servicio.getDuracionEstimada());
        return dto;
    }

    public static List<ServicioDTO> toDTOs(List<Servicio> servicios) {
        List<ServicioDTO> dtos = new ArrayList<>();
        for (Servicio servicio : servicios) {
            dtos.add(toDTO(servicio));
        }
        return dtos;
    }

    public static Servicio toModelo(ServicioDTO dto) throws ValidacionException {
        Servicio servicio = new Servicio();
        try {
            servicio.setId(dto.getId());
            servicio.setActivo(dto.isActivo());
            servicio.setNombre(dto.getNombre());
            servicio.setPrecioBase(dto.getPrecioBase());
            servicio.setDescripcion(dto.getDescripcion());
            servicio.setTipo(TipoServicio.valueOf(dto.getTipo()));
            servicio.setDuracionEstimada(dto.getDuracionEstimada());
        } catch (IllegalArgumentException e) {
            throw new ValidacionException("Datos del servicio invalidos: " + e.getMessage());
        }
        return servicio;
    }
}
