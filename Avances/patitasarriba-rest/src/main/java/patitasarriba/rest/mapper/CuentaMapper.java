package patitasarriba.rest.mapper;

import patitasarriba.bl.ValidacionException;
import patitasarriba.modelo.usuario.Cuenta;
import patitasarriba.rest.dto.CuentaDTO;

import java.util.ArrayList;
import java.util.List;

public final class CuentaMapper {
    private CuentaMapper() {
    }

    // El password nunca sale en la respuesta (igual de riguroso que Cuenta.toString() en el
    // modelo, que tampoco lo imprime): el campo queda en null al convertir a DTO de salida.
    public static CuentaDTO toDTO(Cuenta cuenta) {
        CuentaDTO dto = new CuentaDTO();
        dto.setId(cuenta.getId());
        dto.setActivo(cuenta.isActivo());
        dto.setNombreUsuario(cuenta.getNombreUsuario());
        dto.setCorreo(cuenta.getCorreo());
        dto.setFechaCreacion(cuenta.getFechaCreacion().toString());
        return dto;
    }

    public static List<CuentaDTO> toDTOs(List<Cuenta> cuentas) {
        List<CuentaDTO> dtos = new ArrayList<>();
        for (Cuenta cuenta : cuentas) {
            dtos.add(toDTO(cuenta));
        }
        return dtos;
    }

    // Aqui si se usa dto.getPassword(): es la contraseña en texto plano que manda el cliente en
    // el POST/PUT; CuentaBLImpl.insert/update se encarga de hashearla antes de guardarla.
    public static Cuenta toModelo(CuentaDTO dto) throws ValidacionException {
        Cuenta cuenta = new Cuenta();
        try {
            cuenta.setId(dto.getId());
            cuenta.setActivo(dto.isActivo());
            if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                cuenta.setPassword(dto.getPassword());
            }
            cuenta.setCorreo(dto.getCorreo());
            cuenta.setNombreUsuario(dto.getNombreUsuario());
        } catch (IllegalArgumentException e) {
            throw new ValidacionException("Datos de la cuenta invalidos: " + e.getMessage());
        }
        return cuenta;
    }
}
