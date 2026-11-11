package patitasarriba.bl.usuario;

import patitasarriba.bl.excepciones.BLException;
import patitasarriba.bl.RegistroBL;
import patitasarriba.modelo.usuario.Veterinario;

public interface VeterinarioBL extends RegistroBL<Veterinario, Integer> {
    Veterinario findByDni(String dni) throws BLException;
}
