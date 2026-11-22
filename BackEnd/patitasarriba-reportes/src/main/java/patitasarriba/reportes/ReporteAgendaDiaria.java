package patitasarriba.reportes;

import conexion.DBManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

// RF12. Reporte de agenda diaria en PDF.
// GET /reportes/agenda-diaria?fecha=AAAA-MM-DD[&veterinario={id}][&generadoPor={nombre}]
// Sin veterinario salen todos (RN-CI-14); generadoPor es opcional y solo aparece en el pie de pagina.
@WebServlet(name = "ReporteAgendaDiaria", urlPatterns = { "/reportes/agenda-diaria" })
public class ReporteAgendaDiaria extends HttpServlet {
    private final String NOMBRE_REPORTE = "reportes/Reporte_AgendaDiaria.jasper";
    private final String NOMBRE_LOGO = "imagenes/logo-patitas.png";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String fecha = request.getParameter("fecha");
        if (fecha == null || fecha.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta la fecha del reporte (fecha=AAAA-MM-DD)");
            return;
        }
        LocalDate dia;
        try {
            dia = LocalDate.parse(fecha);
        } catch (DateTimeParseException ex) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "La fecha debe tener el formato AAAA-MM-DD");
            return;
        }
        Integer idVeterinario = null;
        String veterinario = request.getParameter("veterinario");
        if (veterinario != null && !veterinario.isBlank()) {
            try {
                idVeterinario = Integer.parseInt(veterinario);
            } catch (NumberFormatException ex) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "El veterinario debe ser un numero");
                return;
            }
        }

        InputStream reporte = getClass().getClassLoader().getResourceAsStream(NOMBRE_REPORTE);
        if (reporte == null) {
            throw new FileNotFoundException("No se encontró el reporte: " + NOMBRE_REPORTE);
        }

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("fecha", java.sql.Date.valueOf(dia));
        parametros.put("id_veterinario", idVeterinario);
        parametros.put("generado_por", request.getParameter("generadoPor"));

        InputStream logo = getClass().getClassLoader().getResourceAsStream(NOMBRE_LOGO);
        if (logo == null) {
            throw new FileNotFoundException("No se encontró el logo: " + NOMBRE_LOGO);
        }
        Image imagen = ImageIO.read(logo);
        parametros.put("logo", imagen);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=\"agenda-diaria-" + dia + ".pdf\"");
        try (Connection conn = DBManager.getInstance().getConnection()) {
            JasperPrint jp = JasperFillManager.fillReport(reporte, parametros, conn);
            JasperExportManager.exportReportToPdfStream(jp, response.getOutputStream());
        }
        catch (SQLException | JRException ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al generar el reporte: "
                    + ex.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    public String getServletInfo() {
        return "Servlet para generar el reporte de agenda diaria";
    }
}
