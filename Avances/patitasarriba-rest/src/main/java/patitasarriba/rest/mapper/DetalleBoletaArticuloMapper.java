package patitasarriba.rest.mapper;

import patitasarriba.bl.ArticuloBL;
import patitasarriba.bl.AtencionBL;
import patitasarriba.bl.BLException;
import patitasarriba.bl.NoEncontradoException;
import patitasarriba.bl.ValidacionException;
import patitasarriba.bl.impl.atencion.AtencionBLImpl;
import patitasarriba.bl.impl.producto.ArticuloBLImpl;
import patitasarriba.modelo.atencion.InsumoUtilizado;
import patitasarriba.modelo.boleta.Boleta;
import patitasarriba.modelo.boleta.DetalleBoletaArticulo;
import patitasarriba.modelo.producto.Articulo;
import patitasarriba.rest.dto.DetalleBoletaArticuloDTO;
import patitasarriba.rest.dto.InsumoUtilizadoRefDTO;

import java.util.ArrayList;
import java.util.List;

// DetalleBoletaArticulo no es un recurso propio: viaja embebido dentro de BoletaDTO.detallesArticulo.
public final class DetalleBoletaArticuloMapper {
    private static final ArticuloBL articuloBL = new ArticuloBLImpl();
    private static final AtencionBL atencionBL = new AtencionBLImpl();

    private DetalleBoletaArticuloMapper() {
    }

    public static DetalleBoletaArticuloDTO toDTO(DetalleBoletaArticulo detalle) {
        DetalleBoletaArticuloDTO dto = new DetalleBoletaArticuloDTO();
        dto.setId(detalle.getId());
        dto.setActivo(detalle.isActivo());
        dto.setCantidad(detalle.getCantidad());
        dto.setPrecioUnitario(detalle.getPrecioUnitario());
        dto.setSubtotal(detalle.getSubtotal());
        dto.setArticulo(detalle.getArticulo() != null ? ArticuloMapper.toDTO(detalle.getArticulo()) : null);
        if (detalle.getInsumoUtilizado() != null) {
            InsumoUtilizadoRefDTO ref = new InsumoUtilizadoRefDTO();
            ref.setId(detalle.getInsumoUtilizado().getId());
            ref.setIdAtencion(detalle.getInsumoUtilizado().getAtencion().getId());
            dto.setInsumoUtilizado(ref);
        }
        return dto;
    }

    public static List<DetalleBoletaArticuloDTO> toDTOs(List<DetalleBoletaArticulo> detalles) {
        List<DetalleBoletaArticuloDTO> dtos = new ArrayList<>();
        for (DetalleBoletaArticulo detalle : detalles) {
            dtos.add(toDTO(detalle));
        }
        return dtos;
    }

    // boleta es el contenedor (DetalleBoleta.setBoleta exige que no sea nula): se pasa la
    // instancia que BoletaMapper esta construyendo. precioUnitario/subtotal del DTO se ignoran
    // (quedan en 0 hasta que BoletaBLImpl.validarYCalcular los recalcule).
    public static DetalleBoletaArticulo toModelo(DetalleBoletaArticuloDTO dto, Boleta boleta) throws BLException {
        if (dto.getArticulo() == null) {
            throw new ValidacionException("Cada detalle de articulo debe tener un articulo");
        }
        Articulo articulo;
        try {
            articulo = articuloBL.findById(dto.getArticulo().getId());
        } catch (NoEncontradoException e) {
            throw new ValidacionException("No existe el articulo con id " + dto.getArticulo().getId());
        }

        // findInsumoById busca por el propio id del insumo (una sola consulta directa, ver el
        // comentario en AtencionBLImpl); idAtencion del ref ya no hace falta para resolverlo,
        // solo se usa en toDTO para informar de que atencion viene.
        InsumoUtilizado insumoUtilizado = null;
        InsumoUtilizadoRefDTO ref = dto.getInsumoUtilizado();
        if (ref != null) {
            try {
                insumoUtilizado = atencionBL.findInsumoById(ref.getId());
            } catch (NoEncontradoException e) {
                throw new ValidacionException("No existe el insumo utilizado con id " + ref.getId());
            }
        }

        DetalleBoletaArticulo detalle = new DetalleBoletaArticulo();
        try {
            detalle.setId(dto.getId());
            detalle.setActivo(dto.isActivo());
            detalle.setBoleta(boleta);
            detalle.setCantidad(dto.getCantidad());
            detalle.setPrecioUnitario(0);
            detalle.setArticulo(articulo);
            if (insumoUtilizado != null) {
                detalle.setInsumoUtilizado(insumoUtilizado);
            }
        } catch (IllegalArgumentException e) {
            throw new ValidacionException("Datos del detalle de articulo invalidos: " + e.getMessage());
        }
        return detalle;
    }
}
