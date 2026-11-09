package patitasarriba.bl.util;

import patitasarriba.modelo.Registro;

import java.util.Collection;

// RN-GE-06: el estado activo solo cambia al dar de baja. Registrar o modificar siempre guarda
// el registro (y sus lineas) como activo, sin importar lo que venga en el objeto recibido
public final class Activos {

    private Activos() {
    }

    public static void marcar(Registro registro) {
        if (registro != null) registro.setActivo(true);
    }

    public static void marcarTodos(Collection<? extends Registro> registros) {
        if (registros == null) return;
        for (Registro registro : registros) {
            if (registro != null) registro.setActivo(true);
        }
    }
}
