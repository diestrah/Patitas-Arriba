package patitasarriba.rest;

import conexion.DBManager;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

// Reportes en PDF con JasperReports. Sigue el servlet del profesor (softprog-reportes, ReporeteOrdenVenta):
// los .jasper y el logo estan en src/main/resources, se llena el reporte con una conexion y se exporta a PDF.
// La diferencia es que aqui es un recurso JAX-RS dentro del mismo REST, no un WAR aparte.
@Path("/reportes")
public class ReporteResource {
    private static final String REPORTE_AGENDA = "reportes/Reporte_AgendaDiaria.jasper";
    private static final String NOMBRE_LOGO = "imagenes/logo-patitas.png";

    // RF13. GET /reportes/agenda-diaria?fecha=AAAA-MM-DD[&veterinario={id}][&generadoPor={nombre}]
    // Sin veterinario salen todos (RN-CI-14); generadoPor es opcional y solo aparece en el pie de pagina.
    @GET
    @Path("/agenda-diaria")
    @Produces("application/pdf")
    public Response agendaDiaria(@QueryParam("fecha") String fecha,
                                 @QueryParam("veterinario") Integer idVeterinario,
                                 @QueryParam("generadoPor") String generadoPor) {
        if (fecha == null || fecha.isBlank()) {
            return error(Response.Status.BAD_REQUEST, "Falta la fecha del reporte (fecha=AAAA-MM-DD)");
        }
        LocalDate dia;
        try {
            dia = LocalDate.parse(fecha);
        } catch (DateTimeParseException ex) {
            return error(Response.Status.BAD_REQUEST, "La fecha debe tener el formato AAAA-MM-DD");
        }

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("fecha", java.sql.Date.valueOf(dia));
        parametros.put("id_veterinario", idVeterinario);
        parametros.put("generado_por", generadoPor);
        return generar(REPORTE_AGENDA, parametros, "agenda-diaria-" + dia + ".pdf");
    }

    // Carga el .jasper y el logo del classpath, llena el reporte con una conexion del pool y devuelve el PDF
    private Response generar(String nombreReporte, Map<String, Object> parametros, String nombreArchivo) {
        try (InputStream reporte = getClass().getClassLoader().getResourceAsStream(nombreReporte);
             InputStream logo = getClass().getClassLoader().getResourceAsStream(NOMBRE_LOGO)) {
            if (reporte == null) {
                throw new FileNotFoundException("No se encontro el reporte: " + nombreReporte);
            }
            if (logo == null) {
                throw new FileNotFoundException("No se encontro el logo: " + NOMBRE_LOGO);
            }
            Image imagen = ImageIO.read(logo);
            parametros.put("logo", imagen);

            try (Connection conn = DBManager.getInstance().getConnection()) {
                JasperPrint impresion = JasperFillManager.fillReport(reporte, parametros, conn);
                byte[] pdf = JasperExportManager.exportReportToPdf(impresion);
                return Response.ok(pdf)
                        .type("application/pdf")
                        .header("Content-Disposition", "inline; filename=\"" + nombreArchivo + "\"")
                        .build();
            }
        } catch (IOException | SQLException | JRException ex) {
            return error(Response.Status.INTERNAL_SERVER_ERROR, "Error al generar el reporte: " + ex.getMessage());
        }
    }

    private Response error(Response.Status estado, String mensaje) {
        return Response.status(estado)
                .type("application/json")
                .entity(Map.of("error", mensaje))
                .build();
    }
}
