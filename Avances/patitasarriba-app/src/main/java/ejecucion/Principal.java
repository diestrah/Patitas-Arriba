package ejecucion;

import patitasarriba.modelo.atencion.*;
import patitasarriba.modelo.boleta.*;
import patitasarriba.modelo.cita.*;
import patitasarriba.modelo.horario.*;
import patitasarriba.modelo.mascota.*;
import patitasarriba.modelo.producto.*;
import patitasarriba.modelo.receta.*;
import patitasarriba.modelo.usuario.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

// Prueba del modelo en memoria (sin base de datos): recorre el flujo
// cita -> atencion -> receta -> boleta del diagrama de clases.
public class Principal {
    public static void main(String[] args) {

        // ===================== USUARIOS =====================
        Cuenta cuentaCliente = new Cuenta(1, true, "pass123", "juan@gmail.com", LocalDate.of(2026, 1, 10), "juanp");
        Cliente cliente1 = new Cliente(1, true, cuentaCliente, "12345678", "Juan", "Perez", "Gomez", "987654321");

        Cuenta cuentaVet = new Cuenta(2, true, "vet123", "carlos@patitasarriba.com", LocalDate.of(2025, 3, 15), "carlosr");
        Veterinario vet1 = new Veterinario(2, true, cuentaVet, "11223344", "Carlos", "Ramirez", "Soto", "955444333",
                new ArrayList<>(), "CMVP-4521");

        Cuenta cuentaAdmin = new Cuenta(3, true, "admin123", "ana@patitasarriba.com", LocalDate.of(2025, 1, 5), "anat");
        Administrador admin1 = new Administrador(3, true, cuentaAdmin, "44332211", "Ana", "Torres", "Vega", "944555666",
                new ArrayList<>());

        // Horarios del personal (catalogo de horarios)
        Horario lunes = new Horario(1, true, DiaSemana.LUNES, LocalTime.of(9, 0), LocalTime.of(18, 0));
        Horario martes = new Horario(2, true, DiaSemana.MARTES, LocalTime.of(9, 0), LocalTime.of(13, 0));
        vet1.agregarHorario(lunes);
        vet1.agregarHorario(martes);
        admin1.agregarHorario(lunes);

        // ===================== MASCOTA =====================
        Mascota firulais = new Mascota(1, true, cliente1, "Firulais", SexoMascota.MACHO, 8.5,
                LocalDate.of(2022, 4, 10), "Labrador", TipoMascota.PERRO);

        // ===================== CATALOGO =====================
        Servicio consulta = new Servicio(1, true, "Consulta General", 50.0, "Revision general",
                TipoServicio.CONSULTA_MEDICA, 30);
        // Servicio sin descripcion ni duracion definida (ambos son opcionales)
        Servicio banio = new Servicio(2, true, "Bano y corte", 40.0, null, TipoServicio.ESTETICA, null);

        Categoria farmacologia = new Categoria(1, true, "Farmacologia", "Medicamentos para diagnostico y tratamiento");
        Categoria higiene = new Categoria(2, true, "Higiene", "Productos de limpieza y cuidado");

        List<Categoria> categoriasAmoxicilina = new ArrayList<>();
        categoriasAmoxicilina.add(farmacologia);
        Articulo amoxicilina = new Articulo(3, true, "Amoxicilina 250mg", 25.0, "Antibiotico", 40, 10,
                categoriasAmoxicilina);
        Articulo algodon = new Articulo(4, true, "Algodon", 2.0, null, 100, 20, new ArrayList<>());
        algodon.agregarCategoria(higiene);
        Articulo shampoo = new Articulo(5, true, "Shampoo antipulgas", 25.0, "Frasco 250ml", 15, 5, new ArrayList<>());
        shampoo.agregarCategoria(higiene);

        Diagnostico otitis = new Diagnostico(1, true, "Otitis", "Infeccion en el oido");

        // ===================== CITA =====================
        // El cliente solicita la cita: queda PENDIENTE y sin administrador
        Cita cita1 = new Cita(1, true, LocalDateTime.of(2026, 11, 2, 10, 0), EstadoCita.PENDIENTE, firulais,
                null, false, new ArrayList<>());
        // Cada servicio pedido es un detalle, con su veterinario asignado
        cita1.agregarDetalle(1, true, "Control por dolor en el oido", consulta, vet1);
        cita1.agregarDetalle(2, true, null, banio, vet1);

        // El administrador aprueba la cita
        cita1.setAdministrador(admin1);
        cita1.setEstado(EstadoCita.APROBADO);

        DetalleCita detalleConsulta = cita1.getDetalles().get(0);
        DetalleCita detalleBanio = cita1.getDetalles().get(1);

        // ===================== ATENCIONES =====================
        // Atencion medica: ejecucion del detalle "consulta" (relacion 1 a 1)
        Atencion atencionConsulta = new Atencion(1, true, detalleConsulta, new ArrayList<>(), new ArrayList<>(),
                LocalDateTime.of(2026, 11, 2, 10, 15), "Dolor en el oido", "Revision en curso");
        detalleConsulta.setAtencion(atencionConsulta);

        // Insumos usados: la amoxicilina se cobra aparte, el algodon va incluido en el servicio
        atencionConsulta.agregarInsumoUtilizado(1, true, amoxicilina, 1, true);
        atencionConsulta.agregarInsumoUtilizado(2, true, algodon, 3, false);

        // El stock se descuenta al registrar el insumo (regla que aplicara la capa de negocio)
        int stockAmoxicilinaAntes = amoxicilina.getStockActual();
        int stockAlgodonAntes = algodon.getStockActual();
        for (InsumoUtilizado insumo : atencionConsulta.getInsumosUtilizados()) {
            Articulo articuloCatalogo = buscarArticulo(insumo.getArticulo().getIdProducto(), amoxicilina, algodon, shampoo);
            articuloCatalogo.setStockActual(articuloCatalogo.getStockActual() - insumo.getCantidadUtilizada());
        }

        atencionConsulta.agregarDiagnostico(true, otitis, NivelGravedad.LEVE, "Otitis en oido derecho");

        // Atencion estetica: no tiene motivo de consulta (es opcional)
        Atencion atencionBanio = new Atencion(2, true, detalleBanio, new ArrayList<>(), new ArrayList<>(),
                LocalDateTime.of(2026, 11, 2, 11, 0), null, null);
        detalleBanio.setAtencion(atencionBanio);

        // Todos los servicios de la cita fueron atendidos
        cita1.setEstado(EstadoCita.COMPLETADO);

        // ===================== RECETA =====================
        Receta receta1 = new Receta(1, true, atencionConsulta, new ArrayList<>(), LocalDate.of(2026, 11, 2),
                "Administrar con alimento");
        receta1.agregarDetalle(1, true, amoxicilina, "1 comprimido", "Cada 12 horas", 7, 14);

        // ===================== BOLETA =====================
        Boleta boleta1 = new Boleta(1, true, cliente1, new ArrayList<>(), "B001-000001",
                LocalDateTime.of(2026, 11, 2, 11, 45), 0.0, MetodoPago.TARJETA_DE_CREDITO);

        // 1) Una linea de servicio por cada detalle de la cita
        int idLinea = 1;
        for (DetalleCita detalle : cita1.getDetalles()) {
            boleta1.agregarDetalleServicio(idLinea++, true, 1, detalle.getServicio().getPrecioBase(), detalle);
        }
        // 2) Los insumos cobrables de las atenciones
        for (InsumoUtilizado insumo : atencionConsulta.getInsumosUtilizados()) {
            if (insumo.isEsCobrable()) {
                boleta1.agregarDetalleArticulo(idLinea++, true, insumo.getCantidadUtilizada(),
                        insumo.getArticulo().getPrecioBase(), insumo.getArticulo(), insumo);
            }
        }
        // 3) Venta directa en caja (sin insumo)
        int stockShampooAntes = shampoo.getStockActual();
        boleta1.agregarDetalleArticulo(idLinea, true, 1, shampoo.getPrecioBase(), shampoo, null);
        shampoo.setStockActual(shampoo.getStockActual() - 1);

        // El total es la suma de los subtotales
        double total = 0;
        for (DetalleBoleta detalle : boleta1.getDetalles()) {
            total += detalle.getSubtotal();
        }
        boleta1.setTotal(total);

        // ===================== IMPRESION =====================
        System.out.println("============== CITA ==============");
        System.out.println("idCita: " + cita1.getIdCita() + " | fecha: " + cita1.getFechaHora()
                + " | estado: " + cita1.getEstado() + " | emergencia: " + cita1.isEsEmergencia());
        System.out.println("mascota: " + cita1.getMascota().getNombre()
                + " | duenio: " + cita1.getMascota().getCliente().getNombres());
        System.out.println("aprobada por: " + cita1.getAdministrador().getNombres());
        for (DetalleCita detalle : cita1.getDetalles()) {
            System.out.println("  - " + detalle.getServicio().getNombre() + " (" + detalle.getServicio().getTipo() + ")"
                    + " | veterinario: " + detalle.getVeterinario().getNombres()
                    + " | atencion: " + detalle.getAtencion().getIdAtencion());
        }

        System.out.println("\n============== ATENCION MEDICA ==============");
        System.out.println(atencionConsulta);
        System.out.println("Servicio atendido: " + atencionConsulta.getDetalleCita().getServicio().getNombre());

        System.out.println("\n============== RECETA ==============");
        System.out.println(receta1);

        System.out.println("\n============== BOLETA ==============");
        System.out.println("serie: " + boleta1.getSerieCorrelativo() + " | fecha: " + boleta1.getFecha());
        System.out.println("cliente: " + boleta1.getCliente().getNombres() + " " + boleta1.getCliente().getApellidoPaterno());
        System.out.println("metodoPago: " + boleta1.getMetodoPago());
        System.out.println("detalles:");
        for (DetalleBoleta detalle : boleta1.getDetalles()) {
            // Polimorfismo: instanceof y casteo
            if (detalle instanceof DetalleBoletaServicio) {
                DetalleBoletaServicio lineaServicio = (DetalleBoletaServicio) detalle;
                System.out.println("  - Servicio: " + lineaServicio.getDetalleCita().getServicio().getNombre()
                        + " | Cantidad: " + detalle.getCantidad() + " | Subtotal: " + detalle.getSubtotal());
            } else if (detalle instanceof DetalleBoletaArticulo) {
                DetalleBoletaArticulo lineaArticulo = (DetalleBoletaArticulo) detalle;
                String origen;
                if (lineaArticulo.getInsumoUtilizado() == null) {
                    origen = "venta directa";
                } else {
                    origen = "insumo de atencion " + lineaArticulo.getInsumoUtilizado().getAtencion().getIdAtencion();
                }
                System.out.println("  - Articulo: " + lineaArticulo.getArticulo().getNombre() + " (" + origen + ")"
                        + " | Cantidad: " + detalle.getCantidad() + " | Subtotal: " + detalle.getSubtotal());
            }
        }
        System.out.println("total: " + boleta1.getTotal());

        System.out.println("\n============== STOCK ==============");
        System.out.println(amoxicilina.getNombre() + ": " + stockAmoxicilinaAntes + " -> " + amoxicilina.getStockActual());
        System.out.println(algodon.getNombre() + ": " + stockAlgodonAntes + " -> " + algodon.getStockActual());
        System.out.println(shampoo.getNombre() + ": " + stockShampooAntes + " -> " + shampoo.getStockActual());

        System.out.println("\n============== HORARIOS DEL VETERINARIO ==============");
        for (Horario horario : vet1.getHorarios()) {
            System.out.println("  - " + horario.getDiaSemana() + " " + horario.getHoraInicio() + " - " + horario.getHoraFin());
        }

        // ===================== ENCAPSULAMIENTO =====================
        System.out.println("\n============== ENCAPSULAMIENTO ==============");
        // Los getters de objetos devuelven copias: modificar la copia no afecta a la cita
        Mascota copiaMascota = cita1.getMascota();
        copiaMascota.setNombre("Otro nombre");
        System.out.println("Copia defensiva: la cita sigue con '" + cita1.getMascota().getNombre() + "'");

        // Constructor de copia
        Cita copiaCita = new Cita(cita1);
        System.out.println("Copia de cita: idCita=" + copiaCita.getIdCita() + ", detalles=" + copiaCita.getDetalles().size());

        // Las listas que se devuelven no se pueden modificar desde fuera
        try {
            boleta1.getDetalles().clear();
            System.out.println("Lista no modificable: NO se bloqueo (falla)");
        } catch (UnsupportedOperationException ex) {
            System.out.println("Lista no modificable -> Bloqueado");
        }

        // ===================== VALIDACIONES =====================
        System.out.println("\n============== VALIDACIONES ==============");
        probar("Cita sin mascota", () -> cita1.setMascota(null));
        probar("Detalle de boleta con cantidad 0", () -> new DetalleBoletaArticulo().setCantidad(0));
        probar("Articulo con stock negativo", () -> amoxicilina.setStockActual(-1));
        probar("Producto con precio negativo", () -> consulta.setPrecioBase(-10));
        probar("Mascota con peso negativo", () -> firulais.setPeso(-2));
        probar("Cliente con nombres vacios", () -> cliente1.setNombres("   "));
        probar("Diagnostico sin nivel de gravedad",
                () -> new AtencionDiagnostico().setNivelGravedad(null));
        probar("Insumo con cantidad 0", () -> new InsumoUtilizado().setCantidadUtilizada(0));
        probar("Servicio con duracion 0", () -> banio.setDuracionEstimada(0));
        probar("Boleta sin serie", () -> boleta1.setSerieCorrelativo(""));
        probar("Receta sin atencion", () -> receta1.setAtencion(null));
        probar("Id negativo", () -> cita1.setIdCita(-5));
    }

    private static Articulo buscarArticulo(int idProducto, Articulo... articulos) {
        for (Articulo articulo : articulos) {
            if (articulo.getIdProducto() == idProducto) {
                return articulo;
            }
        }
        throw new IllegalArgumentException("No existe el articulo " + idProducto);
    }

    private static void probar(String caso, Runnable accion) {
        try {
            accion.run();
            System.out.println(caso + ": NO se rechazo (falla la validacion)");
        } catch (IllegalArgumentException ex) {
            System.out.println(caso + " -> Rechazado: " + ex.getMessage());
        }
    }
}
