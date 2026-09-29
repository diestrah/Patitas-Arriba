package patitasarriba.bl.impl.producto;

import patitasarriba.bl.BLException;
import patitasarriba.bl.CategoriaArticuloBL;
import patitasarriba.dao.CategoriaArticuloDAO;
import patitasarriba.dao.impl.producto.CategoriaArticuloDAOImpl;
import patitasarriba.modelo.producto.CategoriaArticulo;

import java.sql.SQLException;
import java.util.List;

public class CategoriaArticuloBLImpl implements CategoriaArticuloBL {
    private final CategoriaArticuloDAO categoriaArticuloDAO = new CategoriaArticuloDAOImpl();

    @Override
    public List<CategoriaArticulo> findAll() throws BLException {
        try {
            return categoriaArticuloDAO.findAll();
        } catch (SQLException e) {
            throw new BLException("No se pudo listar las categorias de articulo", e);
        }
    }

    @Override
    public CategoriaArticulo findById(Integer id) throws BLException {
        try {
            return categoriaArticuloDAO.findById(id);
        } catch (SQLException e) {
            throw new BLException("No se pudo recuperar la categoria de articulo", e);
        }
    }

    @Override
    public void insert(CategoriaArticulo categoria) throws BLException {
        try {
            categoriaArticuloDAO.insert(categoria);
        } catch (SQLException e) {
            throw new BLException("No se pudo registrar la categoria de articulo", e);
        }
    }

    @Override
    public void update(CategoriaArticulo categoria) throws BLException {
        validarExiste(categoria.getId());
        try {
            categoriaArticuloDAO.update(categoria);
        } catch (SQLException e) {
            throw new BLException("No se pudo actualizar la categoria de articulo", e);
        }
    }

    @Override
    public void delete(Integer id) throws BLException {
        try {
            categoriaArticuloDAO.delete(id);
        } catch (SQLException e) {
            throw new BLException("No se pudo eliminar la categoria de articulo", e);
        }
    }

    private void validarExiste(int id) throws BLException {
        try {
            if (categoriaArticuloDAO.findById(id) == null) {
                throw new BLException("No existe una categoria de articulo con id " + id);
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar la existencia de la categoria de articulo", e);
        }
    }
}
