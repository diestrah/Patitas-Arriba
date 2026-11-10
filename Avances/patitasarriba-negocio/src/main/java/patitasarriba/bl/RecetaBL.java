package patitasarriba.bl;

import patitasarriba.modelo.receta.Receta;

import java.util.List;

public interface RecetaBL extends RegistroBL<Receta, Integer> {
    // RF16: recetas emitidas en una atencion
    List<Receta> findByAtencion(Integer idAtencion) throws BLException;
}
