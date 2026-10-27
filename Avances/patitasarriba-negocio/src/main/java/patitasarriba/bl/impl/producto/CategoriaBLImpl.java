package patitasarriba.bl.impl.producto;

import patitasarriba.bl.BLException;
import patitasarriba.bl.CategoriaBL;
import patitasarriba.dao.ArticuloDAO;
import patitasarriba.dao.CategoriaDAO;
import patitasarriba.dao.impl.producto.ArticuloDAOImpl;
import patitasarriba.dao.impl.producto.CategoriaDAOImpl;
import patitasarriba.modelo.producto.Categoria;

import java.sql.SQLException;
import java.util.List;

public class CategoriaBLImpl implements CategoriaBL {
    private final CategoriaDAO categoriaDAO = new CategoriaDAOImpl();
    private final ArticuloDAO articuloDAO = new ArticuloDAOImpl();

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
        validarNombreUnico(categoria);
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
        validarNombreUnico(categoria);
        try {
            categoriaDAO.update(categoria);
        } catch (SQLException e) {
            throw new BLException("No se pudo actualizar la categoria de articulo", e);
        }
    }

    @Override
    public void delete(Integer id) throws BLException {
        validarExiste(id);
        validarSinArticulos(id);
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

    private void validarNombreUnico(Categoria categoria) throws BLException {
        try {
            Categoria existente = categoriaDAO.findByNombre(categoria.getNombre());
            if (existente != null && existente.getId() != categoria.getId()) {
                throw new BLException("Ya existe una categoria con el nombre '" + categoria.getNombre() + "'");
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar la unicidad del nombre de la categoria", e);
        }
    }

    // Borrado logico: un articulo activo con esta categoria quedaria apuntando a una categoria inexistente
    private void validarSinArticulos(int id) throws BLException {
        try {
            if (!articuloDAO.findByCategoriaId(id).isEmpty()) {
                throw new BLException("No se puede eliminar la categoria: tiene articulos asignados");
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar los articulos de la categoria", e);
        }
    }

    private void validar(Categoria categoria) throws BLException {
        if (categoria == null) {
            throw new BLException("La categoria de articulo no puede ser nula");
        }
        if (categoria.getNombre() == null || categoria.getNombre().isBlank()) {
            throw new BLException("El nombre de la categoria es obligatorio");
        }

        if (categoria.getDescripcion() == null || categoria.getDescripcion().isBlank()) {
            throw new BLException("La descripción es obligatoria");
        }
    }
}
