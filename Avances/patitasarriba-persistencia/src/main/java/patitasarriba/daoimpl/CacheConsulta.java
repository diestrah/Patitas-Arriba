package patitasarriba.daoimpl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

// Recuerda los findById hechos durante UNA consulta (por ejemplo, armar la lista de citas) para no
// pedir a la base el mismo veterinario, servicio o mascota una y otra vez. Vive solo mientras dura
// esa consulta (lo abre y lo cierra ConexionDAO) y nunca se usa dentro de una transaccion, asi que
// no guarda datos viejos entre una operacion y otra.
final class CacheConsulta {
    private static final ThreadLocal<Map<String, Object>> CACHE = new ThreadLocal<>();

    private CacheConsulta() { }

    interface Carga<T> {
        T cargar() throws SQLException;
    }

    static void abrir() {
        CACHE.set(new HashMap<>());
    }

    static void cerrar() {
        CACHE.remove();
    }

    // Una escritura dentro de la misma consulta invalida lo recordado
    static void limpiar() {
        Map<String, Object> cache = CACHE.get();
        if (cache != null) cache.clear();
    }

    @SuppressWarnings("unchecked")
    static <T> T obtener(Class<?> tipo, Integer id, Carga<T> carga) throws SQLException {
        Map<String, Object> cache = CACHE.get();
        if (cache == null || id == null) return carga.cargar();
        String clave = tipo.getName() + "#" + id;
        if (cache.containsKey(clave)) return (T) cache.get(clave);
        T valor = carga.cargar();
        cache.put(clave, valor);
        return valor;
    }
}
