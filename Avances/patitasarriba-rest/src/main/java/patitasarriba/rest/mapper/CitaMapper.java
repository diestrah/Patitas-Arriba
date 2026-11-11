package patitasarriba.rest.mapper;

import patitasarriba.bl.usuario.AdministradorBL;
import patitasarriba.bl.excepciones.BLException;
import patitasarriba.bl.mascota.MascotaBL;
import patitasarriba.bl.excepciones.NoEncontradoException;
import patitasarriba.bl.excepciones.ValidacionException;
import patitasarriba.blimpl.mascota.MascotaBLImpl;
import patitasarriba.blimpl.usuario.AdministradorBLImpl;
import patitasarriba.modelo.cita.Cita;
import patitasarriba.modelo.cita.DetalleCita;
import patitasarriba.modelo.cita.EstadoCita;
import patitasarriba.modelo.mascota.Mascota;
import patitasarriba.modelo.usuario.Administrador;
import patitasarriba.rest.dto.CitaDTO;
import patitasarriba.rest.dto.DetalleCitaDTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public final class CitaMapper {
    private static final MascotaBL mascotaBL = new MascotaBLImpl();
    private static final AdministradorBL administradorBL = new AdministradorBLImpl();

    private CitaMapper() {
    }

    public static CitaDTO toDTO(Cita cita) {
        CitaDTO dto = new CitaDTO();
        dto.setId(cita.getId());
        dto.setActivo(cita.isActivo());
        dto.setFechaHora(cita.getFechaHora().toString());
        dto.setEstado(cita.getEstado().name());
        dto.setMascota(cita.getMascota() != null ? MascotaMapper.toDTO(cita.getMascota()) : null);
        dto.setAdministrador(cita.getAdministrador() != null ? AdministradorMapper.toDTO(cita.getAdministrador()) : null);
        dto.setEsEmergencia(cita.isEsEmergencia());
        dto.setDetalles(DetalleCitaMapper.toDTOs(cita.getDetalles()));
        return dto;
    }

    public static List<CitaDTO> toDTOs(List<Cita> citas) {
        List<CitaDTO> dtos = new ArrayList<>();
        for (Cita cita : citas) {
            dtos.add(toDTO(cita));
        }
        return dtos;
    }

    // mascota y administrador se referencian por id; administrador es opcional (solo se envia
    // al aprobar/rechazar, ver CitaBLImpl.validarCambioDeEstado). Los detalles se construyen
    // despues de tener la Cita en memoria, porque DetalleCita.setCita exige una Cita no nula
    // (es el mismo orden que usa Cita.agregarDetalle en el modelo).
    public static Cita toModelo(CitaDTO dto) throws BLException {
        if (dto.getMascota() == null) {
            throw new ValidacionException("La cita debe tener una mascota");
        }
        Mascota mascota;
        try {
            mascota = mascotaBL.findById(dto.getMascota().getId());
        } catch (NoEncontradoException e) {
            throw new ValidacionException("No existe la mascota con id " + dto.getMascota().getId());
        }

        Administrador administrador = null;
        if (dto.getAdministrador() != null) {
            try {
                administrador = administradorBL.findById(dto.getAdministrador().getId());
            } catch (NoEncontradoException e) {
                throw new ValidacionException("No existe el administrador con id " + dto.getAdministrador().getId());
            }
        }

        Cita cita = new Cita();
        try {
            cita.setId(dto.getId());
            cita.setActivo(dto.isActivo());
            cita.setFechaHora(LocalDateTime.parse(dto.getFechaHora()));
            cita.setEstado(EstadoCita.valueOf(dto.getEstado()));
            cita.setMascota(mascota);
            cita.setAdministrador(administrador);
            cita.setEsEmergencia(dto.isEsEmergencia());
        } catch (IllegalArgumentException | DateTimeParseException e) {
            throw new ValidacionException("Datos de la cita invalidos: " + e.getMessage());
        }

        if (dto.getDetalles() == null || dto.getDetalles().isEmpty()) {
            throw new ValidacionException("La cita debe tener al menos un servicio");
        }
        List<DetalleCita> detalles = new ArrayList<>();
        for (DetalleCitaDTO detalleDTO : dto.getDetalles()) {
            detalles.add(DetalleCitaMapper.toModelo(detalleDTO, cita));
        }
        cita.setDetalles(detalles);

        return cita;
    }
}
