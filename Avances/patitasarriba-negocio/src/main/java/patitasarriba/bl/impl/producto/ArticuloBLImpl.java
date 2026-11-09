package patitasarriba.bl.impl.producto;

import patitasarriba.bl.util.Activos;
import patitasarriba.bl.ArticuloBL;
import patitasarriba.bl.BLException;
import patitasarriba.bl.NoEncontradoException;
import patitasarriba.dao.*;
import patitasarriba.dao.impl.producto.ArticuloDAOImpl;
import patitasarriba.dao.impl.producto.CategoriaDAOImpl;
import patitasarriba.dao.transacciones.TransactionsManager;
import patitasarriba.modelo.producto.Articulo;
import patitasarriba.modelo.producto.Categoria;

import java.sql.SQLException;
import java.util.List;

public class ArticuloBLImpl implements ArticuloBL {
    private final ArticuloDAO articuloDAO = new ArticuloDAOImpl();
    private final CategoriaDAO categoriaDAO = new CategoriaDAOImpl();
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
            Articulo articulo = articuloDAO.findById(id);
            if(articulo==null){
                throw new NoEncontradoException("No se encontro el articulo con id :"+id);
            }
            return articulo;
        } catch (SQLException e) {
            throw new BLException("No se pudo recuperar el articulo", e);
        }
    }

    @Override
    public void insert(Articulo articulo) throws BLException {
        Activos.marcar(articulo);
        validarPrecio(articulo);
        validarCategoriaExiste(articulo);
        try {
            articuloDAO.insert(articulo);
        } catch (SQLException e) {
            throw new BLException("No se pudo insertar el articulo", e);
        }
    }

    @Override
    public void update(Articulo articulo) throws BLException {
        Activos.marcar(articulo);
        if (articulo == null) {
            throw new BLException("El articulo no puede ser nulo");
        }
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
    public void ajustarStock(Integer idArticulo, int cantidad) throws BLException {
        if (idArticulo == null) throw new BLException("El id del artículo no puede ser nulo");
        if (cantidad == 0) throw new BLException("La cantidad a ajustar no puede ser 0");
        Articulo articulo = findById(idArticulo);
        TransactionsManager.iniciar();
        try {
            // Atomico en la BD (RN-AR-04): un ajuste negativo no puede dejar el stock bajo 0
            if (!articuloDAO.ajustarStock(TransactionsManager.getConnection(), idArticulo, cantidad)) {
                throw new SQLException("Stock insuficiente de " + articulo.getNombre()
                        + " para retirar " + (-cantidad) + " unidades");
            }
            TransactionsManager.commit();
        } catch (SQLException | RuntimeException e) {
            if (TransactionsManager.activa()) TransactionsManager.rollback();
            throw new BLException("No se pudo ajustar el stock: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Integer id) throws BLException {
        validarExiste(id);
        try {
            articuloDAO.delete(id);
        } catch (SQLException e) {
            throw new BLException("No se pudo eliminar el articulo", e);
        }
    }

    private void validarPrecio(Articulo articulo) throws BLException {
        if (articulo == null) {
            throw new BLException("El articulo no puede ser nulo");
        }
        if (articulo.getNombre() == null || articulo.getNombre().isBlank()) {
            throw new BLException("El nombre del articulo es obligatorio");
        }
        if (articulo.getPrecioBase() <= 0) {
            throw new BLException("El precio base del articulo debe ser mayor a 0");
        }
        if (articulo.getStockActual() < 0 || articulo.getStockMinimo() < 0) {
            throw new BLException("El stock no puede ser negativo");
        }
    }

    private void validarCategoriaExiste(Articulo articulo) throws BLException {
        if (articulo.getCategorias() == null || articulo.getCategorias().isEmpty()) {
            throw new BLException("El articulo debe tener al menos una categoria");
        }
        try {
            for (Categoria categoria : articulo.getCategorias()) {
                if (categoria == null || categoriaDAO.findById(categoria.getId()) == null) {
                    throw new BLException("Una de las categorias asignadas al articulo no existe");
                }
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
