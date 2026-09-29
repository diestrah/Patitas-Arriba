package patitasarriba.modelo.producto;

public class Articulo extends Producto {
    private int stockActual;
    private int stockMinimo;
    private String marca;
    private CategoriaArticulo categoria;

    public Articulo() {
    }

    public Articulo(int id, boolean activo, String nombre, double precioBase, String descripcion,
                    int stockActual, int stockMinimo, String marca, CategoriaArticulo categoria) {
        super(id, activo, nombre, precioBase, descripcion);
        setStockActual(stockActual);
        setStockMinimo(stockMinimo);
        setMarca(marca);
        setCategoria(categoria);
    }

    // Constructor de copia
    public Articulo(final Articulo articulo) {
        super(articulo);
        setStockActual(articulo.getStockActual());
        setStockMinimo(articulo.getStockMinimo());
        setMarca(articulo.getMarca());
        setCategoria(articulo.getCategoria());
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

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        if (marca == null || marca.isEmpty()) {
            throw new IllegalArgumentException("marca no puede ser nulo o vacío");
        }
        this.marca = marca;
    }

    public CategoriaArticulo getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaArticulo categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("categoria no puede ser nulo");
        }
        this.categoria = categoria;
    }

    @Override
    public String toString() {
        return "Articulo{" +
                super.toString() +
                ", stockActual=" + stockActual +
                ", stockMinimo=" + stockMinimo +
                ", marca='" + marca + '\'' +
                ", categoria=" + categoria +
                '}';
    }
}
