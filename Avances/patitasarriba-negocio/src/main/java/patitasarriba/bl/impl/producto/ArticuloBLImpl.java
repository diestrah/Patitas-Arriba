package patitasarriba.bl.impl.producto;

import patitasarriba.bl.ArticuloBL;
import patitasarriba.bl.BLException;
import patitasarriba.dao.ArticuloDAO;
import patitasarriba.dao.CategoriaArticuloDAO;
import patitasarriba.dao.impl.producto.ArticuloDAOImpl;
import patitasarriba.dao.impl.producto.CategoriaArticuloDAOImpl;
import patitasarriba.modelo.producto.Articulo;

import java.sql.SQLException;
import java.util.List;

public class ArticuloBLImpl implements ArticuloBL {
    private final ArticuloDAO articuloDAO = new ArticuloDAOImpl();
    private final CategoriaArticuloDAO categoriaArticuloDAO = new CategoriaArticuloDAOImpl();

    @Override
    public List<Articulo> findAll() throws BLException {
        try {
            return articuloDAO.findAll();
        } catch (SQLException e) {
            throw new BLException("No se pudo listar los articulos", e);
        }
    }

    @Override
    public Articulo findById(Integer id) throws BLException {
        try {
            return articuloDAO.findById(id);
        } catch (SQLException e) {
            throw new BLException("No se pudo recuperar el articulo", e);
        }
    }

    @Override
    public void insert(Articulo articulo) throws BLException {
        validarPrecio(articulo);
        validarCategoriaExiste(articulo);
        try {
            articuloDAO.insert(articulo);
        } catch (SQLException e) {
            throw new BLException("No se pudo registrar el articulo", e);
        }
    }

    @Override
    public void update(Articulo articulo) throws BLException {
        validarExiste(articulo.getId());
        validarPrecio(articulo);
        validarCategoriaExiste(articulo);
        try {
            articuloDAO.update(articulo);
        } catch (SQLException e) {
            throw new BLException("No se pudo actualizar el articulo", e);
        }
    }

    @Override
    public void delete(Integer id) throws BLException {
        try {
            articuloDAO.delete(id);
        } catch (SQLException e) {
            throw new BLException("No se pudo eliminar el articulo", e);
        }
    }

    private void validarPrecio(Articulo articulo) throws BLException {
        if (articulo.getPrecioBase() <= 0) {
            throw new BLException("El precio base del articulo debe ser mayor a 0");
        }
    }

    private void validarCategoriaExiste(Articulo articulo) throws BLException {
        try {
            if (categoriaArticuloDAO.findById(articulo.getCategoria().getId()) == null) {
                throw new BLException("La categoria asignada al articulo no existe");
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar la categoria del articulo", e);
        }
    }

    private void validarExiste(int id) throws BLException {
        try {
            if (articuloDAO.findById(id) == null) {
                throw new BLException("No existe un articulo con id " + id);
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar la existencia del articulo", e);
        }
    }
}
