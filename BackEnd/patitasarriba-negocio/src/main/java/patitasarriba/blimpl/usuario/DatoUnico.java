package patitasarriba.blimpl.usuario;

import patitasarriba.bl.excepciones.ValidacionException;
import patitasarriba.modelo.usuario.Persona;

import java.sql.SQLException;

// Verifica que un campo UNIQUE de persona (DNI, teléfono, colegiatura) no lo use otro registro.
// Las búsquedas por campo UNIQUE no filtran activo, así que también detectan registros dados de baja.
final class DatoUnico {

    @FunctionalInterface
    interface Busqueda {
        Persona buscar() throws SQLException;
    }

    private DatoUnico() {
    }

    static void verificarLibre(Busqueda busqueda, int idPropio, String dato) throws SQLException, ValidacionException {
        String mensaje = dato + " ya se encuentra en uso";
        Persona otro;
        try {
            otro = busqueda.buscar();
        } catch (IllegalArgumentException e) {
            // El DAO falla al leer una persona dada de baja cuya cuenta también se dio de baja: igual está en uso
            throw new ValidacionException(mensaje);
        }
        if (otro != null && otro.getId() != idPropio) {
            throw new ValidacionException(mensaje);
        }
    }

    static boolean tieneValor(String valor) {
        return valor != null && !valor.isBlank();
    }
}
