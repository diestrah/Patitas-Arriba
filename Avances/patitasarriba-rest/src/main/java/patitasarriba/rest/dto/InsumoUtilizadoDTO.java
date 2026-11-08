package patitasarriba.rest.dto;

public class InsumoUtilizadoDTO {
    private int id;
    private boolean activo;
    private ArticuloDTO articulo;
    private int cantidadUtilizada;
    private boolean esCobrable;

    public InsumoUtilizadoDTO() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public ArticuloDTO getArticulo() {
        return articulo;
    }

    public void setArticulo(ArticuloDTO articulo) {
        this.articulo = articulo;
    }

    public int getCantidadUtilizada() {
        return cantidadUtilizada;
    }

    public void setCantidadUtilizada(int cantidadUtilizada) {
        this.cantidadUtilizada = cantidadUtilizada;
    }

    public boolean isEsCobrable() {
        return esCobrable;
    }

    public void setEsCobrable(boolean esCobrable) {
        this.esCobrable = esCobrable;
    }
}
