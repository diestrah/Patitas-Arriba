package patitasarriba.rest.mapper;

import patitasarriba.bl.excepciones.ValidacionException;
import patitasarriba.modelo.horario.DiaSemana;
import patitasarriba.modelo.horario.Horario;
import patitasarriba.rest.dto.HorarioDTO;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public final class HorarioMapper {
    private HorarioMapper() {
    }

    public static HorarioDTO toDTO(Horario horario) {
        HorarioDTO dto = new HorarioDTO();
        dto.setId(horario.getId());
        dto.setActivo(horario.isActivo());
        dto.setDiaSemana(horario.getDiaSemana().name());
        dto.setHoraInicio(horario.getHoraInicio().toString());
        dto.setHoraFin(horario.getHoraFin().toString());
        return dto;
    }

    public static List<HorarioDTO> toDTOs(List<Horario> horarios) {
        List<HorarioDTO> dtos = new ArrayList<>();
        for (Horario horario : horarios) {
            dtos.add(toDTO(horario));
        }
        return dtos;
    }

    public static Horario toModelo(HorarioDTO dto) throws ValidacionException {
        Horario horario = new Horario();
        try {
            horario.setId(dto.getId());
            horario.setActivo(dto.isActivo());
            horario.setDiaSemana(DiaSemana.valueOf(dto.getDiaSemana()));
            horario.setHoraInicio(LocalTime.parse(dto.getHoraInicio()));
            horario.setHoraFin(LocalTime.parse(dto.getHoraFin()));
        } catch (IllegalArgumentException | DateTimeParseException | NullPointerException e) {
            throw new ValidacionException("diaSemana, horaInicio (HH:mm) o horaFin (HH:mm) invalidos");
        }
        return horario;
    }
}
