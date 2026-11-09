package patitasarriba.bl;

import patitasarriba.modelo.boleta.Boleta;

public interface BoletaBL extends RegistroBL<Boleta, Integer> {
    // RF18: boleta sin guardar con todo lo pendiente de cobro del cliente (servicios atendidos e
    // insumos cobrables aun no cobrados), con sus precios. Se completa con el metodo de pago y,
    // si el cliente compra algo mas, con lineas de venta en caja; luego se emite con insert
    Boleta prepararCobro(Integer idCliente) throws BLException;
}
