package patitasarriba.modelo.producto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Articulo extends Producto {
    private int stockActual;
    private int stockMinimo;
    private List<Categoria> categorias;

    public Articulo() {
        this.categorias = new ArrayList<>();
    }

    public Articulo(int idProducto, boolean activo, String nombre, double precioBase, String descripcion,
                    int stockActual, int stockMinimo, List<Categoria> categorias) {
        super(idProducto, activo, nombre, precioBase, descripcion);
        setStockActual(stockActual);
        setStockMinimo(stockMinimo);
        setCategorias(categorias);
    }

    // Constructor de copia
    public Articulo(final Articulo articulo) {
        if (articulo == null) {
            throw new IllegalArgumentException("articulo no puede ser nulo");
        }
        super(articulo);
        setStockActual(articulo.getStockActual());
        setStockMinimo(articulo.getStockMinimo());
        setCategorias(articulo.getCategorias());
    }

    // Getters y Setters
    public int getStockActual() {
        return stockActual;
    }

    public void setStockActual(int stockActual) {
        if (stockActual < 0) {
            throw new IllegalArgumentException("stockActual no puede ser negativo");
        }
        this.stockActual = stockActual;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(int stockMinimo) {
        if (stockMinimo < 0) {
            throw new IllegalArgumentException("stockMinimo no puede ser negativo");
        }
        this.stockMinimo = stockMinimo;
    }

    public List<Categoria> getCategorias() {
        return Collections.unmodifiableList(categorias);
    }

    public void setCategorias(List<Categoria> categorias) {
        if (categorias == null) {
            throw new IllegalArgumentException("categorias no puede ser nulo");
        }
        for (Categoria categoria : categorias) {
            if (categoria == null) {
                throw new IllegalArgumentException("categorias no puede contener elementos nulos");
            }
        }
        this.categorias = new ArrayList<>(categorias);
    }

    public void agregarCategoria(Categoria categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("categoria no puede ser nula");
        }
        categorias.add(categoria);
    }

    public void eliminarCategoria(Categoria categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("categoria no puede ser nula");
        }
        categorias.removeIf(c -> c.getIdCategoria() == categoria.getIdCategoria());
    }

    @Override
    public String toString() {
        List<String> nombresCategorias = new ArrayList<>();
        for (Categoria categoria : categorias) {
            nombresCategorias.add(categoria.getNombre());
        }

        return "Articulo{" +
                super.toString() +
                ", stockActual=" + stockActual +
                ", stockMinimo=" + stockMinimo +
                ", categorias=" + nombresCategorias +
                '}';
    }
}
