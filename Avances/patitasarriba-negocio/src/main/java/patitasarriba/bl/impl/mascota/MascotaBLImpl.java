package patitasarriba.bl.impl.mascota;

import patitasarriba.bl.BLException;
import patitasarriba.bl.MascotaBL;
import patitasarriba.modelo.mascota.Mascota;
import patitasarriba.dao.MascotaDAO; // esto está en la rama feature/daos
import patitasarriba.dao.impl.mascota.MascotaDAOImpl; // esto está en la rama feature/daos

import java.util.List;

public class MascotaBLImpl implements MascotaBL {
    private final MascotaDAO mascotaDAO = new MascotaDAOImpl();

    @Override
    public List<Mascota> findAll() throws BLException {
        try{
            return mascotaDAO.findAll();
        } catch (Exception e) {
            throw new BLException("Error al obtener todas las mascotas", e);
        }
    }

    @Override
    public Mascota findById(Integer id) throws BLException {
        try{
            return mascotaDAO.findById(id);
        } catch (Exception e) {
            throw new BLException("Error al obtener la mascota con id: " + id, e);
        }
    }

    @Override
    public void insert(Mascota mascota) throws BLException {
        // validaciones de negocio para la mascota
        try{
            mascotaDAO.insert(mascota);
        } catch (Exception e) {
            throw new BLException("Error al insertar la mascota", e);
        }
    }

    @Override
    public void update(Mascota mascota) throws BLException {
        // validaciones de negocio para la mascota
        try{
            mascotaDAO.update(mascota);
        } catch (Exception e) {
            throw new BLException("Error al actualizar la mascota", e);
        }
    }

    @Override
    public void delete(Integer id) throws BLException {
        // validaciones de negocio para la mascota
        try{
            mascotaDAO.delete(id);
        } catch (Exception e) {
            throw new BLException("Error al eliminar la mascota con id: " + id, e);
        }
    }
}
