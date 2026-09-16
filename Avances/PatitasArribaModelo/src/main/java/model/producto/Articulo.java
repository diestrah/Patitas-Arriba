package model.producto;

public class Articulo extends Producto {
    private int stockActual;
    private int stockMinimo;
    private String marca;
    private CategoriaArticulo categoria;


    public Articulo(int idProducto, String nombre, double precioBase, String descripcion, boolean estado,
                    int stockActual, int stockMinimo, String marca, CategoriaArticulo categoria) {
        super(idProducto, nombre, precioBase, descripcion, estado);
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.marca = marca;
        this.categoria = categoria;
    }

    // Getters y Setters
    public int getStockActual() {
        return stockActual;
    }

    public void setStockActual(int stockActual) {
        this.stockActual = stockActual;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public CategoriaArticulo getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaArticulo categoria) {
        this.categoria = categoria;
    }

    @Override
    public String toString() {
        return "Articulo{" +
                "idProducto=" + getIdProducto() +
                ", nombre='" + getNombre() + '\'' +
                ", stockActual=" + stockActual +
                ", stockMinimo=" + stockMinimo +
                ", marca='" + marca + '\'' +
                ", categoria=" + categoria +
                '}';
    }
}
