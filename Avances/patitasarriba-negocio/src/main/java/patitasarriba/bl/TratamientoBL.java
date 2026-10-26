package patitasarriba.bl;

public interface TratamientoBL extends RegistroBL<Tratamiento, Integer>{
    Tratamiento findByName(String nombre) throws BLException;
}
