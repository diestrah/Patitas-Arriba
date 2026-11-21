package patitasarriba.rest.mapper;

import patitasarriba.bl.excepciones.BLException;
import patitasarriba.bl.cita.CitaBL;
import patitasarriba.bl.excepciones.NoEncontradoException;
import patitasarriba.bl.excepciones.ValidacionException;
import patitasarriba.blimpl.cita.CitaBLImpl;
import patitasarriba.modelo.boleta.Boleta;
import patitasarriba.modelo.boleta.DetalleBoletaServicio;
import patitasarriba.modelo.cita.DetalleCita;
import patitasarriba.rest.dto.DetalleBoletaServicioDTO;

import java.util.ArrayList;
import java.util.List;

// DetalleBoletaServicio no es un recurso propio: viaja embebido dentro de BoletaDTO.detallesServicio.
public final class DetalleBoletaServicioMapper {
    private static final CitaBL citaBL = new CitaBLImpl();

    private DetalleBoletaServicioMapper() {
    }

    public static DetalleBoletaServicioDTO toDTO(DetalleBoletaServicio detalle) {
        DetalleBoletaServicioDTO dto = new DetalleBoletaServicioDTO();
        dto.setId(detalle.getId());
        dto.setActivo(detalle.isActivo());
        dto.setCantidad(detalle.getCantidad());
        dto.setPrecioUnitario(detalle.getPrecioUnitario());
        dto.setSubtotal(detalle.getSubtotal());
        dto.setDetalleCita(detalle.getDetalleCita() != null ? DetalleCitaMapper.toDTO(detalle.getDetalleCita()) : null);
        return dto;
    }

    public static List<DetalleBoletaServicioDTO> toDTOs(List<DetalleBoletaServicio> detalles) {
        List<DetalleBoletaServicioDTO> dtos = new ArrayList<>();
        for (DetalleBoletaServicio detalle : detalles) {
            dtos.add(toDTO(detalle));
        }
        return dtos;
    }

    // boleta es el contenedor; detalleCita se referencia por id, resuelto con
    // CitaBL.findDetalleById (una sola consulta directa, ver el comentario en CitaBLImpl).
    // precioUnitario/subtotal del DTO se ignoran (quedan en 0 hasta que
    // BoletaBLImpl.validarYCalcular los recalcule).
    public static DetalleBoletaServicio toModelo(DetalleBoletaServicioDTO dto, Boleta boleta) throws BLException {
        if (dto.getDetalleCita() == null) {
            throw new ValidacionException("Cada detalle de servicio debe estar asociado a un servicio de una cita");
        }
        DetalleCita detalleCita;
        try {
            detalleCita = citaBL.findDetalleById(dto.getDetalleCita().getId());
        } catch (NoEncontradoException e) {
            throw new ValidacionException("No existe el detalle de cita con id " + dto.getDetalleCita().getId());
        }

        DetalleBoletaServicio detalle = new DetalleBoletaServicio();
        try {
            detalle.setId(dto.getId());
            detalle.setActivo(dto.isActivo());
            detalle.setBoleta(boleta);
            detalle.setCantidad(dto.getCantidad());
            detalle.setPrecioUnitario(0);
            detalle.setDetalleCita(detalleCita);
        } catch (IllegalArgumentException e) {
            throw new ValidacionException("Datos del detalle de servicio invalidos: " + e.getMessage());
        }
        return detalle;
    }
}
