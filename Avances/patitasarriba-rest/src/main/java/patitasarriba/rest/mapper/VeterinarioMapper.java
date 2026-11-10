package patitasarriba.rest.mapper;

import patitasarriba.bl.BLException;
import patitasarriba.bl.CuentaBL;
import patitasarriba.bl.HorarioBL;
import patitasarriba.bl.NoEncontradoException;
import patitasarriba.bl.ValidacionException;
import patitasarriba.bl.impl.horario.HorarioBLImpl;
import patitasarriba.bl.impl.usuario.CuentaBLImpl;
import patitasarriba.modelo.horario.Horario;
import patitasarriba.modelo.usuario.Cuenta;
import patitasarriba.modelo.usuario.Veterinario;
import patitasarriba.rest.dto.HorarioDTO;
import patitasarriba.rest.dto.VeterinarioDTO;

import java.util.ArrayList;
import java.util.List;

public final class VeterinarioMapper {
    private static final CuentaBL cuentaBL = new CuentaBLImpl();
    private static final HorarioBL horarioBL = new HorarioBLImpl();

    private VeterinarioMapper() {
    }

    public static VeterinarioDTO toDTO(Veterinario veterinario) {
        VeterinarioDTO dto = new VeterinarioDTO();
        dto.setId(veterinario.getId());
        dto.setActivo(veterinario.isActivo());
        dto.setCuenta(veterinario.getCuenta() != null ? CuentaMapper.toDTO(veterinario.getCuenta()) : null);
        dto.setDni(veterinario.getDni());
        dto.setNombres(veterinario.getNombres());
        dto.setApellidoPaterno(veterinario.getApellidoPaterno());
        dto.setApellidoMaterno(veterinario.getApellidoMaterno());
        dto.setTelefono(veterinario.getTelefono());
        dto.setNumeroColegiatura(veterinario.getNumeroColegiatura());

        List<HorarioDTO> horarios = new ArrayList<>();
        for (Horario horario : veterinario.getHorarios()) {
            horarios.add(HorarioMapper.toDTO(horario));
        }
        dto.setHorarios(horarios);
        return dto;
    }

    public static List<VeterinarioDTO> toDTOs(List<Veterinario> veterinarios) {
        List<VeterinarioDTO> dtos = new ArrayList<>();
        for (Veterinario veterinario : veterinarios) {
            dtos.add(toDTO(veterinario));
        }
        return dtos;
    }

    // La cuenta y los horarios se referencian por id (deben existir ya como catalogo):
    // ningun objeto se crea en cascada desde aqui.
    public static Veterinario toModelo(VeterinarioDTO dto) throws BLException {
        if (dto.getCuenta() == null) {
            throw new ValidacionException("El veterinario debe tener una cuenta");
        }
        Cuenta cuenta;
        try {
            cuenta = cuentaBL.findById(dto.getCuenta().getId());
        } catch (NoEncontradoException e) {
            throw new ValidacionException("No existe la cuenta con id " + dto.getCuenta().getId());
        }

        List<Horario> horarios = new ArrayList<>();
        for (HorarioDTO horarioDTO : dto.getHorarios()) {
            try {
                horarios.add(horarioBL.findById(horarioDTO.getId()));
            } catch (NoEncontradoException e) {
                throw new ValidacionException("No existe el horario con id " + horarioDTO.getId());
            }
        }

        Veterinario veterinario = new Veterinario();
        try {
            veterinario.setId(dto.getId());
            veterinario.setActivo(dto.isActivo());
            veterinario.setCuenta(cuenta);
            veterinario.setDni(dto.getDni());
            veterinario.setNombres(dto.getNombres());
            veterinario.setApellidoPaterno(dto.getApellidoPaterno());
            veterinario.setApellidoMaterno(dto.getApellidoMaterno());
            veterinario.setTelefono(dto.getTelefono());
            veterinario.setHorarios(horarios);
            veterinario.setNumeroColegiatura(dto.getNumeroColegiatura());
        } catch (IllegalArgumentException e) {
            throw new ValidacionException("Datos del veterinario invalidos: " + e.getMessage());
        }
        return veterinario;
    }
}
