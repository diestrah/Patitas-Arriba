package patitasarriba.bl.impl.producto;

import patitasarriba.bl.ArticuloBL;
import patitasarriba.bl.BLException;
import patitasarriba.dao.*;
import patitasarriba.dao.impl.atencion.AtencionDAOImpl;
import patitasarriba.dao.impl.boleta.BoletaDAOImpl;
import patitasarriba.dao.impl.producto.ArticuloDAOImpl;
import patitasarriba.dao.impl.producto.CategoriaDAOImpl;
import patitasarriba.dao.impl.receta.RecetaDAOImpl;
import patitasarriba.modelo.atencion.Atencion;
import patitasarriba.modelo.atencion.InsumoUtilizado;
import patitasarriba.modelo.boleta.Boleta;
import patitasarriba.modelo.boleta.DetalleBoleta;
import patitasarriba.modelo.boleta.DetalleBoletaArticulo;
import patitasarriba.modelo.producto.Articulo;
import patitasarriba.modelo.producto.Categoria;
import patitasarriba.modelo.receta.DetalleReceta;
import patitasarriba.modelo.receta.Receta;

import java.sql.SQLException;
import java.util.List;

public class ArticuloBLImpl implements ArticuloBL {
    private final ArticuloDAO articuloDAO = new ArticuloDAOImpl();
    private final CategoriaDAO categoriaDAO = new CategoriaDAOImpl();
    private final AtencionDAO atencionDAO = new AtencionDAOImpl();
    private final RecetaDAO recetaDAO = new RecetaDAOImpl();
    private final BoletaDAO boletaDAO = new BoletaDAOImpl();
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
            throw new BLException("No se pudo insertar el articulo", e);
        }
    }

    @Override
    public void update(Articulo articulo) throws BLException {
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
    public void delete(Integer id) throws BLException {
        validarExiste(id);
        validarSinUso (id);
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

    // Si el articulo se uso y se da de baja, atenciones, recetas o boletas dejarian de poder leerse
    private void validarSinUso(int idArticulo) throws BLException {
        try {
            for (Atencion atencion : atencionDAO.findAll()) {
                for (InsumoUtilizado insumo : atencion.getInsumosUtilizados()) {
                    if (insumo.getArticulo().getId() == idArticulo) {
                        throw new BLException("No se puede eliminar: el artículo se usó en la atención " + atencion.getId());
                    }
                }
            }
            for (Receta receta : recetaDAO.findAll()) {
                for (DetalleReceta detalle : receta.getDetalles()) {
                    if (detalle.getArticulo().getId() == idArticulo) {
                        throw new BLException("No se puede eliminar: el artículo está en la receta " + receta.getId());
                    }
                }
            }
            for (Boleta boleta : boletaDAO.findAll()) {
                for (DetalleBoleta detalle : boleta.getDetalles()) {
                    if (detalle instanceof DetalleBoletaArticulo linea && linea.getArticulo().getId() == idArticulo) {
                        throw new BLException("No se puede eliminar: el artículo se vendió en la boleta "
                                + boleta.getSerieCorrelativo());
                    }
                }
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar si el artículo está en uso", e);
        }
    }
}
