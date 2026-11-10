package patitasarriba.rest.mapper;

import patitasarriba.bl.ArticuloBL;
import patitasarriba.bl.BLException;
import patitasarriba.bl.NoEncontradoException;
import patitasarriba.bl.ValidacionException;
import patitasarriba.bl.impl.producto.ArticuloBLImpl;
import patitasarriba.modelo.atencion.Atencion;
import patitasarriba.modelo.atencion.InsumoUtilizado;
import patitasarriba.modelo.producto.Articulo;
import patitasarriba.rest.dto.InsumoUtilizadoDTO;

import java.util.ArrayList;
import java.util.List;

// InsumoUtilizado no es un recurso propio: viaja embebido dentro de AtencionDTO.
public final class InsumoUtilizadoMapper {
    private static final ArticuloBL articuloBL = new ArticuloBLImpl();

    private InsumoUtilizadoMapper() {
    }

    public static InsumoUtilizadoDTO toDTO(InsumoUtilizado insumo) {
        InsumoUtilizadoDTO dto = new InsumoUtilizadoDTO();
        dto.setId(insumo.getId());
        dto.setActivo(insumo.isActivo());
        dto.setArticulo(insumo.getArticulo() != null ? ArticuloMapper.toDTO(insumo.getArticulo()) : null);
        dto.setCantidadUtilizada(insumo.getCantidadUtilizada());
        dto.setEsCobrable(insumo.isEsCobrable());
        return dto;
    }

    public static List<InsumoUtilizadoDTO> toDTOs(List<InsumoUtilizado> insumos) {
        List<InsumoUtilizadoDTO> dtos = new ArrayList<>();
        for (InsumoUtilizado insumo : insumos) {
            dtos.add(toDTO(insumo));
        }
        return dtos;
    }

    // atencion es el contenedor (InsumoUtilizado.setAtencion exige que no sea nula): se pasa la
    // instancia que AtencionMapper esta construyendo.
    public static InsumoUtilizado toModelo(InsumoUtilizadoDTO dto, Atencion atencion) throws BLException {
        if (dto.getArticulo() == null) {
            throw new ValidacionException("Cada insumo utilizado debe tener un articulo");
        }
        Articulo articulo;
        try {
            articulo = articuloBL.findById(dto.getArticulo().getId());
        } catch (NoEncontradoException e) {
            throw new ValidacionException("No existe el articulo con id " + dto.getArticulo().getId());
        }

        InsumoUtilizado insumo = new InsumoUtilizado();
        try {
            insumo.setId(dto.getId());
            insumo.setActivo(dto.isActivo());
            insumo.setAtencion(atencion);
            insumo.setArticulo(articulo);
            insumo.setCantidadUtilizada(dto.getCantidadUtilizada());
            insumo.setEsCobrable(dto.isEsCobrable());
        } catch (IllegalArgumentException e) {
            throw new ValidacionException("Datos del insumo utilizado invalidos: " + e.getMessage());
        }
        return insumo;
    }
}
