package ejecucion;

import model.atencion.*;
import model.boleta.*;
import model.cita.*;
import model.horario.*;
import model.mascota.*;
import model.producto.*;
import model.receta.*;
import model.usuario.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Principal {
    public static void main(String[] args) {

        Cuenta cuentaCliente = new Cuenta(1, "pass123", "juan@gmail.com", LocalDate.of(2026, 1, 10), "juanp");
        Cliente cliente1 = new Cliente("Juan", "Perez", "Gomez", "987654321", "12345678", cuentaCliente, new ArrayList<>());

        Cuenta cuentaVet = new Cuenta(2, "vet123", "carlos@patasarriba.com", LocalDate.of(2025, 3, 15), "carlosr");
        Veterinario vet1 = new Veterinario("Carlos", "Ramirez", "Soto", "955444333", "11223344", cuentaVet, true,
                new ArrayList<>(), "CMVP-4521");

        // Agrega horario al veterinario
        vet1.agregarHorario(new Horario(1, DiaSemana.LUNES, LocalTime.of(9, 0), LocalTime.of(13, 0)));

        Mascota firulais = new Mascota(1, "Firulais", SexoMascota.MACHO, 8.5, LocalDate.of(2022, 4, 10),
                TipoMascota.PERRO, "Labrador", cliente1, new ArrayList<>());
        // Agrega mascota al cliente
        cliente1.agregarMascota(firulais);

        // Catalogo de servicio, sin veterinario fijo
        ServicioMedico consulta = new ServicioMedico(1, "Consulta General", 50.0, "Revision general",
                true, 30, true, TipoServicioMedico.CONSULTA_MEDICA, false, false);

        // Catalogo de inventario, con stock propio
        Articulo amoxicilina = new Articulo(2, "Amoxicilina 250mg", 25.0, "Antibiotico", true, 40, 10,
                "VetPharma", CategoriaArticulo.FARMACOLOGIA);

        // Se agenda la cita, sin detalles aun
        Cita cita1 = new Cita(1, LocalDateTime.of(2026, 9, 15, 10, 0), EstadoCita.AGENDADA, firulais, vet1, new ArrayList<>());
        // Se confirma el servicio reservado
        List<DetalleCita> detallesCita = cita1.getDetalles();
        detallesCita.add(new DetalleCita(1, "Control por dolor en pata", consulta));

        // Catalogo reutilizable, existe de antemano
        Tratamiento tratamientoAntibiotico = new Tratamiento(1, "Aplicacion de antibiotico", "Inyeccion IM");
        Diagnostico diagnosticoOtitis = new Diagnostico(1, "Otitis", "Infeccion en el oido");

        // Se abre la consulta, sin diagnostico/tratamiento/receta aun
        AtencionMedica atencion1 = new AtencionMedica(1, LocalDateTime.of(2026, 9, 15, 10, 30),
                "Dolor en el oido", 8.5, "Revision en curso", vet1, null, new ArrayList<>(), new ArrayList<>());

        // Se decide el tratamiento, sin insumos aun
        AtencionTratamiento aplicacionAntibiotico = new AtencionTratamiento(tratamientoAntibiotico, new ArrayList<>());
        // Se registra el insumo usado (relacion N a N)
        List<InsumoUtilizado> insumos = aplicacionAntibiotico.getInsumosUtilizados();
        insumos.add(new InsumoUtilizado(amoxicilina, 14, "comprimidos"));
        // Se agrega el tratamiento a la consulta
        List<AtencionTratamiento> tratamientos = atencion1.getTratamientos();
        tratamientos.add(aplicacionAntibiotico);

        // Se registra el diagnostico de esta consulta
        List<AtencionDiagnostico> diagnosticos = atencion1.getDiagnosticos();
        diagnosticos.add(new AtencionDiagnostico("Leve", "Otitis en oido derecho", diagnosticoOtitis));

        // Al terminar, se emite la receta, sin medicamentos aun
        Receta receta1 = new Receta(1, LocalDate.of(2026, 9, 15), "Administrar con alimento", true, new ArrayList<>());
        // Se agrega el medicamento recetado
        List<DetalleReceta> detallesReceta = receta1.getDetalles();
        detallesReceta.add(new DetalleReceta(1, "1 comprimido", "Cada 12 horas", 7, 14, amoxicilina));
        // Se vincula la receta a la consulta
        atencion1.setReceta(receta1);

        // Se registra la consulta en el historial de la mascota
        List<AtencionMedica> historialFirulais = firulais.getAtencionesMedicas();
        historialFirulais.add(atencion1);

        // Al final de la visita, se emite la boleta, sin productos aun
        Boleta boleta1 = new Boleta(1, LocalDate.of(2026, 9, 15), 100.0, MetodoPago.TARJETA_DE_CREDITO,
                cliente1, cita1, new ArrayList<>());
        // Se factura el servicio y el medicamento
        List<DetalleBoleta> detallesBoleta = boleta1.getDetalles();
        detallesBoleta.add(new DetalleBoleta(1, 1, 50.0, consulta));
        detallesBoleta.add(new DetalleBoleta(2, 2, 50.0, amoxicilina));

        System.out.println("========== DATOS DE LA BOLETA =========");
        System.out.println("idBoleta: " + boleta1.getIdBoleta());
        System.out.println("fecha: " + boleta1.getFecha());
        System.out.println("cliente: " + cliente1.getNombres() + " " + cliente1.getApellidoPaterno());
        System.out.println("mascota: " + firulais.getNombre());
        System.out.println("metodoPago: " + boleta1.getMetodoPago());

        System.out.println("detalles:");
        int stockAntes = amoxicilina.getStockActual();
        for (DetalleBoleta detalle : boleta1.getDetalles()) {
            Producto producto = detalle.getProducto();

            // Polimorfismo: instanceof y casteo
            if (producto instanceof Articulo) {
                Articulo articulo = (Articulo) producto;
                System.out.println("  - Articulo: " + articulo.getNombre() + " | Cantidad: " + detalle.getCantidad() + " | Subtotal: " + detalle.getSubTotal());
                articulo.setStockActual(articulo.getStockActual() - detalle.getCantidad());
            } else if (producto instanceof Servicio) {
                System.out.println("  - Servicio: " + producto.getNombre() + " | Cantidad: " + detalle.getCantidad() + " | Subtotal: " + detalle.getSubTotal());
            }
        }

        System.out.println("total: " + boleta1.getTotal());

        System.out.println("\nStock " + amoxicilina.getNombre() + ": " + stockAntes + " -> " + amoxicilina.getStockActual());
    }
}
