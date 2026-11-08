package patitasarriba.rest.mapper;

import patitasarriba.bl.ValidacionException;
import patitasarriba.modelo.producto.Categoria;
import patitasarriba.rest.dto.CategoriaDTO;

import java.util.ArrayList;
import java.util.List;

public final class CategoriaMapper {
    private CategoriaMapper() {
    }

    public static CategoriaDTO toDTO(Categoria categoria) {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(categoria.getId());
        dto.setActivo(categoria.isActivo());
        dto.setNombre(categoria.getNombre());
        dto.setDescripcion(categoria.getDescripcion());
        return dto;
    }

    public static List<CategoriaDTO> toDTOs(List<Categoria> categorias) {
        List<CategoriaDTO> dtos = new ArrayList<>();
        for (Categoria categoria : categorias) {
            dtos.add(toDTO(categoria));
        }
        return dtos;
    }

    public static Categoria toModelo(CategoriaDTO dto) throws ValidacionException {
        Categoria categoria = new Categoria();
        try {
            categoria.setId(dto.getId());
            categoria.setActivo(dto.isActivo());
            categoria.setNombre(dto.getNombre());
            categoria.setDescripcion(dto.getDescripcion());
        } catch (IllegalArgumentException e) {
            throw new ValidacionException("Datos de la categoria invalidos: " + e.getMessage());
        }
        return categoria;
    }
}
