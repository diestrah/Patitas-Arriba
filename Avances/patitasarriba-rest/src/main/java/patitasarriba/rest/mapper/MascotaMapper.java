package patitasarriba.rest.mapper;

import patitasarriba.bl.excepciones.BLException;
import patitasarriba.bl.usuario.ClienteBL;
import patitasarriba.bl.excepciones.NoEncontradoException;
import patitasarriba.bl.excepciones.ValidacionException;
import patitasarriba.blimpl.usuario.ClienteBLImpl;
import patitasarriba.modelo.mascota.Mascota;
import patitasarriba.modelo.mascota.SexoMascota;
import patitasarriba.modelo.mascota.TipoMascota;
import patitasarriba.modelo.usuario.Cliente;
import patitasarriba.rest.dto.MascotaDTO;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public final class MascotaMapper {
    private static final ClienteBL clienteBL = new ClienteBLImpl();

    private MascotaMapper() {
    }

    public static MascotaDTO toDTO(Mascota mascota) {
        MascotaDTO dto = new MascotaDTO();
        dto.setId(mascota.getId());
        dto.setActivo(mascota.isActivo());
        dto.setCliente(mascota.getCliente() != null ? ClienteMapper.toDTO(mascota.getCliente()) : null);
        dto.setNombre(mascota.getNombre());
        dto.setSexo(mascota.getSexo().name());
        dto.setPeso(mascota.getPeso());
        dto.setFechaNacimiento(mascota.getFechaNacimiento().toString());
        dto.setRaza(mascota.getRaza());
        dto.setTipoMascota(mascota.getTipoMascota().name());
        dto.setFoto(mascota.getFoto());
        return dto;
    }

    public static List<MascotaDTO> toDTOs(List<Mascota> mascotas) {
        List<MascotaDTO> dtos = new ArrayList<>();
        for (Mascota mascota : mascotas) {
            dtos.add(toDTO(mascota));
        }
        return dtos;
    }

    // El cliente se referencia por id: ya debe existir, no se crea en cascada desde aqui.
    public static Mascota toModelo(MascotaDTO dto) throws BLException {
        if (dto.getCliente() == null) {
            throw new ValidacionException("La mascota debe tener un cliente");
        }
        Cliente cliente;
        try {
            cliente = clienteBL.findById(dto.getCliente().getId());
        } catch (NoEncontradoException e) {
            throw new ValidacionException("No existe el cliente con id " + dto.getCliente().getId());
        }

        // La columna fecha_nacimiento es NOT NULL: sin esto el error decía "Datos de la mascota invalidos: text"
        if (dto.getFechaNacimiento() == null || dto.getFechaNacimiento().isBlank()) {
            throw new ValidacionException("La fecha de nacimiento es obligatoria");
        }
        Mascota mascota = new Mascota();
        try {
            mascota.setId(dto.getId());
            mascota.setActivo(dto.isActivo());
            mascota.setCliente(cliente);
            mascota.setNombre(dto.getNombre());
            mascota.setSexo(SexoMascota.valueOf(dto.getSexo()));
            mascota.setPeso(dto.getPeso());
            mascota.setFechaNacimiento(LocalDate.parse(dto.getFechaNacimiento()));
            mascota.setRaza(dto.getRaza());
            mascota.setTipoMascota(TipoMascota.valueOf(dto.getTipoMascota()));
            mascota.setFoto(dto.getFoto());
        } catch (IllegalArgumentException | DateTimeParseException | NullPointerException e) {
            throw new ValidacionException("Datos de la mascota invalidos: " + e.getMessage());
        }
        return mascota;
    }
}
