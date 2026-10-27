package patitasarriba.bl.impl.producto;

import patitasarriba.bl.BLException;
import patitasarriba.bl.CategoriaBL;
import patitasarriba.dao.CategoriaDAO;
import patitasarriba.dao.impl.producto.CategoriaDAOImpl;
import patitasarriba.modelo.producto.Categoria;

import java.sql.SQLException;
import java.util.List;

public class CategoriaBLImpl implements CategoriaBL {
    private final CategoriaDAO categoriaDAO = new CategoriaDAOImpl();

    @Override
    public List<Categoria> findAll() throws BLException {
        try {
            return categoriaDAO.findAll();
        } catch (SQLException e) {
            throw new BLException("No se pudo listar las categorias de articulo", e);
        }
    }

    @Override
    public Categoria findById(Integer id) throws BLException {
        try {
            return categoriaDAO.findById(id);
        } catch (SQLException e) {
            throw new BLException("No se pudo recuperar la categoria de articulo", e);
        }
    }

    @Override
    public void insert(Categoria categoria) throws BLException {
        validar(categoria);
        try {
            categoriaDAO.insert(categoria);
        } catch (SQLException e) {
            throw new BLException("No se pudo registrar la categoria de articulo", e);
        }
    }

    @Override
    public void update(Categoria categoria) throws BLException {
        validar(categoria);
        validarExiste(categoria.getId());
        try {
            categoriaDAO.update(categoria);
        } catch (SQLException e) {
            throw new BLException("No se pudo actualizar la categoria de articulo", e);
        }
    }

    @Override
    public void delete(Integer id) throws BLException {
        try {
            categoriaDAO.delete(id);
        } catch (SQLException e) {
            throw new BLException("No se pudo eliminar la categoria de articulo", e);
        }
    }

    private void validarExiste(int id) throws BLException {
        try {
            if (categoriaDAO.findById(id) == null) {
                throw new BLException("No existe una categoria de articulo con id " + id);
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar la existencia de la categoria de articulo", e);
        }

    }

    private void validar(Categoria categoria) throws BLException {
        if (categoria == null) {
            throw new BLException("La categoria de articulo no puede ser nula");
        }
        if (categoria.getNombre() == null || categoria.getNombre().isBlank()) {
            throw new BLException("El nombre de la categoria es obligatorio");
        }
    }
}
