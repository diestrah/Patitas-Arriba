package patitasarriba.rest.mapper;

import patitasarriba.bl.excepciones.BLException;
import patitasarriba.bl.producto.CategoriaBL;
import patitasarriba.bl.excepciones.NoEncontradoException;
import patitasarriba.bl.excepciones.ValidacionException;
import patitasarriba.blimpl.producto.CategoriaBLImpl;
import patitasarriba.modelo.producto.Articulo;
import patitasarriba.modelo.producto.Categoria;
import patitasarriba.rest.dto.ArticuloDTO;
import patitasarriba.rest.dto.CategoriaDTO;

import java.util.ArrayList;
import java.util.List;

public final class ArticuloMapper {
    private static final CategoriaBL categoriaBL = new CategoriaBLImpl();

    private ArticuloMapper() {
    }

    public static ArticuloDTO toDTO(Articulo articulo) {
        ArticuloDTO dto = new ArticuloDTO();
        dto.setId(articulo.getId());
        dto.setActivo(articulo.isActivo());
        dto.setNombre(articulo.getNombre());
        dto.setPrecioBase(articulo.getPrecioBase());
        dto.setDescripcion(articulo.getDescripcion());
        dto.setStockActual(articulo.getStockActual());
        dto.setStockMinimo(articulo.getStockMinimo());

        List<CategoriaDTO> categorias = new ArrayList<>();
        for (Categoria categoria : articulo.getCategorias()) {
            categorias.add(CategoriaMapper.toDTO(categoria));
        }
        dto.setCategorias(categorias);
        return dto;
    }

    public static List<ArticuloDTO> toDTOs(List<Articulo> articulos) {
        List<ArticuloDTO> dtos = new ArrayList<>();
        for (Articulo articulo : articulos) {
            dtos.add(toDTO(articulo));
        }
        return dtos;
    }

    // Las categorias se referencian por id: ya deben existir en el catalogo.
    public static Articulo toModelo(ArticuloDTO dto) throws BLException {
        List<Categoria> categorias = new ArrayList<>();
        for (CategoriaDTO categoriaDTO : dto.getCategorias()) {
            try {
                categorias.add(categoriaBL.findById(categoriaDTO.getId()));
            } catch (NoEncontradoException e) {
                throw new ValidacionException("No existe la categoria con id " + categoriaDTO.getId());
            }
        }

        Articulo articulo = new Articulo();
        try {
            articulo.setId(dto.getId());
            articulo.setActivo(dto.isActivo());
            articulo.setNombre(dto.getNombre());
            articulo.setPrecioBase(dto.getPrecioBase());
            articulo.setDescripcion(dto.getDescripcion());
            articulo.setStockActual(dto.getStockActual());
            articulo.setStockMinimo(dto.getStockMinimo());
            articulo.setCategorias(categorias);
        } catch (IllegalArgumentException e) {
            throw new ValidacionException("Datos del articulo invalidos: " + e.getMessage());
        }
        return articulo;
    }
}
