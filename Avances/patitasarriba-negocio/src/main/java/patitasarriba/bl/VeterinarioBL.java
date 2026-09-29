package patitasarriba.bl;

import patitasarriba.modelo.usuario.Veterinario;

public interface VeterinarioBL extends RegistroBL<Veterinario, Integer> {
    Veterinario findByDni(String dni) throws BLException;
}
