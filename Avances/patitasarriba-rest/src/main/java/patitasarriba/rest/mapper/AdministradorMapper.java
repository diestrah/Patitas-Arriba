package patitasarriba.rest.mapper;

import patitasarriba.bl.BLException;
import patitasarriba.bl.CuentaBL;
import patitasarriba.bl.HorarioBL;
import patitasarriba.bl.NoEncontradoException;
import patitasarriba.bl.ValidacionException;
import patitasarriba.bl.impl.horario.HorarioBLImpl;
import patitasarriba.bl.impl.usuario.CuentaBLImpl;
import patitasarriba.modelo.horario.Horario;
import patitasarriba.modelo.usuario.Administrador;
import patitasarriba.modelo.usuario.Cuenta;
import patitasarriba.rest.dto.AdministradorDTO;
import patitasarriba.rest.dto.HorarioDTO;

import java.util.ArrayList;
import java.util.List;

public final class AdministradorMapper {
    private static final CuentaBL cuentaBL = new CuentaBLImpl();
    private static final HorarioBL horarioBL = new HorarioBLImpl();

    private AdministradorMapper() {
    }

    public static AdministradorDTO toDTO(Administrador administrador) {
        AdministradorDTO dto = new AdministradorDTO();
        dto.setId(administrador.getId());
        dto.setActivo(administrador.isActivo());
        dto.setCuenta(administrador.getCuenta() != null ? CuentaMapper.toDTO(administrador.getCuenta()) : null);
        dto.setDni(administrador.getDni());
        dto.setNombres(administrador.getNombres());
        dto.setApellidoPaterno(administrador.getApellidoPaterno());
        dto.setApellidoMaterno(administrador.getApellidoMaterno());
        dto.setTelefono(administrador.getTelefono());

        List<HorarioDTO> horarios = new ArrayList<>();
        for (Horario horario : administrador.getHorarios()) {
            horarios.add(HorarioMapper.toDTO(horario));
        }
        dto.setHorarios(horarios);
        return dto;
    }

    public static List<AdministradorDTO> toDTOs(List<Administrador> administradores) {
        List<AdministradorDTO> dtos = new ArrayList<>();
        for (Administrador administrador : administradores) {
            dtos.add(toDTO(administrador));
        }
        return dtos;
    }

    // La cuenta y los horarios se referencian por id (deben existir ya como catalogo):
    // ningun objeto se crea en cascada desde aqui.
    public static Administrador toModelo(AdministradorDTO dto) throws BLException {
        if (dto.getCuenta() == null) {
            throw new ValidacionException("El administrador debe tener una cuenta");
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

        Administrador administrador = new Administrador();
        try {
            administrador.setId(dto.getId());
            administrador.setActivo(dto.isActivo());
            administrador.setCuenta(cuenta);
            administrador.setDni(dto.getDni());
            administrador.setNombres(dto.getNombres());
            administrador.setApellidoPaterno(dto.getApellidoPaterno());
            administrador.setApellidoMaterno(dto.getApellidoMaterno());
            administrador.setTelefono(dto.getTelefono());
            administrador.setHorarios(horarios);
        } catch (IllegalArgumentException e) {
            throw new ValidacionException("Datos del administrador invalidos: " + e.getMessage());
        }
        return administrador;
    }

    // Alta hecha por un administrador: la cuenta no existe todavia, la crea AccesoBL junto con la persona
    public static Administrador toModeloSinCuenta(AdministradorDTO dto) throws BLException {
        List<Horario> horarios = new ArrayList<>();
        for (HorarioDTO horarioDTO : dto.getHorarios()) {
            try {
                horarios.add(horarioBL.findById(horarioDTO.getId()));
            } catch (NoEncontradoException e) {
                throw new ValidacionException("No existe el horario con id " + horarioDTO.getId());
            }
        }

        Administrador administrador = new Administrador();
        try {
            administrador.setDni(dto.getDni());
            administrador.setNombres(dto.getNombres());
            administrador.setApellidoPaterno(dto.getApellidoPaterno());
            administrador.setApellidoMaterno(dto.getApellidoMaterno());
            administrador.setTelefono(dto.getTelefono());
            administrador.setHorarios(horarios);
        } catch (IllegalArgumentException e) {
            throw new ValidacionException("Datos del administrador invalidos: " + e.getMessage());
        }
        return administrador;
    }
}
