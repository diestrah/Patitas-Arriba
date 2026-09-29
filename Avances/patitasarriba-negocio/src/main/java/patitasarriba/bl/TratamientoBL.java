package patitasarriba.bl;

import patitasarriba.modelo.atencion.Tratamiento;

public interface TratamientoBL extends RegistroBL<Tratamiento, Integer>{
    Tratamiento findByName(String nombre) throws BLException;
}
