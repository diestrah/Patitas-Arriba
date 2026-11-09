package patitasarriba.bl;

import patitasarriba.modelo.producto.Articulo;

public interface ArticuloBL extends RegistroBL<Articulo, Integer> {
    // RN-AR-07: reposicion (cantidad > 0) o ajuste de inventario (cantidad < 0, por merma o conteo).
    // Es la unica forma de cambiar el stock fuera de atenciones y ventas; modificar el articulo no lo toca
    void ajustarStock(Integer idArticulo, int cantidad) throws BLException;
}
