package patitasarriba.rest.mapper;

import patitasarriba.bl.BLException;
import patitasarriba.bl.ClienteBL;
import patitasarriba.bl.NoEncontradoException;
import patitasarriba.bl.ValidacionException;
import patitasarriba.bl.impl.usuario.ClienteBLImpl;
import patitasarriba.modelo.boleta.Boleta;
import patitasarriba.modelo.boleta.DetalleBoleta;
import patitasarriba.modelo.boleta.DetalleBoletaArticulo;
import patitasarriba.modelo.boleta.DetalleBoletaServicio;
import patitasarriba.modelo.boleta.MetodoPago;
import patitasarriba.modelo.usuario.Cliente;
import patitasarriba.rest.dto.BoletaDTO;
import patitasarriba.rest.dto.DetalleBoletaArticuloDTO;
import patitasarriba.rest.dto.DetalleBoletaServicioDTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public final class BoletaMapper {
    private static final ClienteBL clienteBL = new ClienteBLImpl();

    private BoletaMapper() {
    }

    // Los detalles son polimorficos (DetalleBoletaArticulo / DetalleBoletaServicio): se reparten
    // en las dos listas del DTO segun su tipo real.
    public static BoletaDTO toDTO(Boleta boleta) {
        BoletaDTO dto = new BoletaDTO();
        dto.setId(boleta.getId());
        dto.setActivo(boleta.isActivo());
        dto.setCliente(boleta.getCliente() != null ? ClienteMapper.toDTO(boleta.getCliente()) : null);
        dto.setSerieCorrelativo(boleta.getSerieCorrelativo());
        dto.setFecha(boleta.getFecha().toString());
        dto.setTotal(boleta.getTotal());
        dto.setMetodoPago(boleta.getMetodoPago().name());

        List<DetalleBoletaArticuloDTO> detallesArticulo = new ArrayList<>();
        List<DetalleBoletaServicioDTO> detallesServicio = new ArrayList<>();
        for (DetalleBoleta detalle : boleta.getDetalles()) {
            if (detalle instanceof DetalleBoletaArticulo articulo) {
                detallesArticulo.add(DetalleBoletaArticuloMapper.toDTO(articulo));
            } else if (detalle instanceof DetalleBoletaServicio servicio) {
                detallesServicio.add(DetalleBoletaServicioMapper.toDTO(servicio));
            }
        }
        dto.setDetallesArticulo(detallesArticulo);
        dto.setDetallesServicio(detallesServicio);
        return dto;
    }

    public static List<BoletaDTO> toDTOs(List<Boleta> boletas) {
        List<BoletaDTO> dtos = new ArrayList<>();
        for (Boleta boleta : boletas) {
            dtos.add(toDTO(boleta));
        }
        return dtos;
    }

    // El cliente se referencia por id. El total se ignora al leerlo del DTO (queda en 0 hasta que
    // BoletaBLImpl.validarYCalcular lo recalcule desde los detalles, igual que precioUnitario/
    // subtotal de cada detalle). Los detalles se combinan en el unico List<DetalleBoleta> que
    // pide el modelo, construidos despues de tener la Boleta en memoria (setBoleta exige no nula).
    public static Boleta toModelo(BoletaDTO dto) throws BLException {
        if (dto.getCliente() == null) {
            throw new ValidacionException("La boleta debe tener un cliente asociado");
        }
        Cliente cliente;
        try {
            cliente = clienteBL.findById(dto.getCliente().getId());
        } catch (NoEncontradoException e) {
            throw new ValidacionException("No existe el cliente con id " + dto.getCliente().getId());
        }

        Boleta boleta = new Boleta();
        try {
            boleta.setId(dto.getId());
            boleta.setActivo(dto.isActivo());
            boleta.setCliente(cliente);
            // Al registrar puede venir vacío: el número lo asigna la capa de negocio (RN-BO-03)
            if (dto.getSerieCorrelativo() != null && !dto.getSerieCorrelativo().isBlank()) {
                boleta.setSerieCorrelativo(dto.getSerieCorrelativo());
            }
            boleta.setFecha(LocalDateTime.parse(dto.getFecha()));
            boleta.setMetodoPago(MetodoPago.valueOf(dto.getMetodoPago()));
        } catch (IllegalArgumentException | DateTimeParseException e) {
            throw new ValidacionException("Datos de la boleta invalidos: " + e.getMessage());
        }

        List<DetalleBoleta> detalles = new ArrayList<>();
        for (DetalleBoletaArticuloDTO detalleDTO : dto.getDetallesArticulo()) {
            detalles.add(DetalleBoletaArticuloMapper.toModelo(detalleDTO, boleta));
        }
        for (DetalleBoletaServicioDTO detalleDTO : dto.getDetallesServicio()) {
            detalles.add(DetalleBoletaServicioMapper.toModelo(detalleDTO, boleta));
        }
        if (detalles.isEmpty()) {
            throw new ValidacionException("La boleta debe tener al menos un detalle");
        }
        boleta.setDetalles(detalles);

        return boleta;
    }
}
