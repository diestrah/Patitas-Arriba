package patitasarriba.rest.mapper;

import patitasarriba.bl.excepciones.BLException;
import patitasarriba.bl.usuario.CuentaBL;
import patitasarriba.bl.excepciones.NoEncontradoException;
import patitasarriba.bl.excepciones.ValidacionException;
import patitasarriba.blimpl.usuario.CuentaBLImpl;
import patitasarriba.modelo.usuario.Cliente;
import patitasarriba.modelo.usuario.Cuenta;
import patitasarriba.rest.dto.ClienteDTO;

import java.util.ArrayList;
import java.util.List;

public final class ClienteMapper {
    private static final CuentaBL cuentaBL = new CuentaBLImpl();

    private ClienteMapper() {
    }

    public static ClienteDTO toDTO(Cliente cliente) {
        ClienteDTO dto = new ClienteDTO();
        dto.setId(cliente.getId());
        dto.setActivo(cliente.isActivo());
        dto.setCuenta(cliente.getCuenta() != null ? CuentaMapper.toDTO(cliente.getCuenta()) : null);
        dto.setDni(cliente.getDni());
        dto.setNombres(cliente.getNombres());
        dto.setApellidoPaterno(cliente.getApellidoPaterno());
        dto.setApellidoMaterno(cliente.getApellidoMaterno());
        dto.setTelefono(cliente.getTelefono());
        return dto;
    }

    public static List<ClienteDTO> toDTOs(List<Cliente> clientes) {
        List<ClienteDTO> dtos = new ArrayList<>();
        for (Cliente cliente : clientes) {
            dtos.add(toDTO(cliente));
        }
        return dtos;
    }

    // La cuenta se referencia por id: ClienteBLImpl.insert/update exige que ya exista
    // (ver validarCuentaActiva), no se crea en cascada desde aqui.
    public static Cliente toModelo(ClienteDTO dto) throws BLException {
        if (dto.getCuenta() == null) {
            throw new ValidacionException("El cliente debe tener una cuenta");
        }
        Cuenta cuenta;
        try {
            cuenta = cuentaBL.findById(dto.getCuenta().getId());
        } catch (NoEncontradoException e) {
            throw new ValidacionException("No existe la cuenta con id " + dto.getCuenta().getId());
        }

        Cliente cliente = new Cliente();
        try {
            cliente.setId(dto.getId());
            cliente.setActivo(dto.isActivo());
            cliente.setCuenta(cuenta);
            cliente.setDni(dto.getDni());
            cliente.setNombres(dto.getNombres());
            cliente.setApellidoPaterno(dto.getApellidoPaterno());
            cliente.setApellidoMaterno(dto.getApellidoMaterno());
            cliente.setTelefono(dto.getTelefono());
        } catch (IllegalArgumentException e) {
            throw new ValidacionException("Datos del cliente invalidos: " + e.getMessage());
        }
        return cliente;
    }

    // Alta hecha por un administrador: la cuenta no existe todavia, la crea AccesoBL junto con la persona
    public static Cliente toModeloSinCuenta(ClienteDTO dto) throws BLException {
        Cliente cliente = new Cliente();
        try {
            cliente.setDni(dto.getDni());
            cliente.setNombres(dto.getNombres());
            cliente.setApellidoPaterno(dto.getApellidoPaterno());
            cliente.setApellidoMaterno(dto.getApellidoMaterno());
            cliente.setTelefono(dto.getTelefono());
        } catch (IllegalArgumentException e) {
            throw new ValidacionException("Datos del cliente invalidos: " + e.getMessage());
        }
        return cliente;
    }
}
