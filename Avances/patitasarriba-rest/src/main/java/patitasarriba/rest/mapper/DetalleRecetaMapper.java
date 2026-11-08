package patitasarriba.rest.mapper;

import patitasarriba.bl.ArticuloBL;
import patitasarriba.bl.BLException;
import patitasarriba.bl.NoEncontradoException;
import patitasarriba.bl.ValidacionException;
import patitasarriba.bl.impl.producto.ArticuloBLImpl;
import patitasarriba.modelo.producto.Articulo;
import patitasarriba.modelo.receta.DetalleReceta;
import patitasarriba.modelo.receta.Receta;
import patitasarriba.rest.dto.DetalleRecetaDTO;

import java.util.ArrayList;
import java.util.List;

// DetalleReceta no es un recurso propio: viaja embebido dentro de RecetaDTO.
public final class DetalleRecetaMapper {
    private static final ArticuloBL articuloBL = new ArticuloBLImpl();

    private DetalleRecetaMapper() {
    }

    public static DetalleRecetaDTO toDTO(DetalleReceta detalle) {
        DetalleRecetaDTO dto = new DetalleRecetaDTO();
        dto.setId(detalle.getId());
        dto.setActivo(detalle.isActivo());
        dto.setArticulo(detalle.getArticulo() != null ? ArticuloMapper.toDTO(detalle.getArticulo()) : null);
        dto.setDosis(detalle.getDosis());
        dto.setFrecuencia(detalle.getFrecuencia());
        dto.setDuracionDias(detalle.getDuracionDias());
        dto.setCantidadTotal(detalle.getCantidadTotal());
        return dto;
    }

    public static List<DetalleRecetaDTO> toDTOs(List<DetalleReceta> detalles) {
        List<DetalleRecetaDTO> dtos = new ArrayList<>();
        for (DetalleReceta detalle : detalles) {
            dtos.add(toDTO(detalle));
        }
        return dtos;
    }

    // receta es el contenedor (DetalleReceta.setReceta exige que no sea nula): se pasa la
    // instancia que RecetaMapper esta construyendo.
    public static DetalleReceta toModelo(DetalleRecetaDTO dto, Receta receta) throws BLException {
        if (dto.getArticulo() == null) {
            throw new ValidacionException("Cada detalle de receta debe tener un articulo");
        }
        Articulo articulo;
        try {
            articulo = articuloBL.findById(dto.getArticulo().getId());
        } catch (NoEncontradoException e) {
            throw new ValidacionException("No existe el articulo con id " + dto.getArticulo().getId());
        }

        DetalleReceta detalle = new DetalleReceta();
        try {
            detalle.setId(dto.getId());
            detalle.setActivo(dto.isActivo());
            detalle.setReceta(receta);
            detalle.setArticulo(articulo);
            detalle.setDosis(dto.getDosis());
            detalle.setFrecuencia(dto.getFrecuencia());
            detalle.setDuracionDias(dto.getDuracionDias());
            detalle.setCantidadTotal(dto.getCantidadTotal());
        } catch (IllegalArgumentException e) {
            throw new ValidacionException("Datos del detalle de receta invalidos: " + e.getMessage());
        }
        return detalle;
    }
}
