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
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

// RF15. Reporte de historial clinico de una mascota en PDF (maestro-detalle: una atencion por bloque
// y, dentro de cada una, el subreporte con sus diagnosticos, insumos y receta).
// GET /reportes/historial-clinico?mascota={id}[&generadoPor={nombre}]
@WebServlet(name = "ReporteHistorialClinico", urlPatterns = { "/reportes/historial-clinico" })
public class ReporteHistorialClinico extends HttpServlet {
    private final String NOMBRE_REPORTE = "reportes/Reporte_HistorialClinico.jasper";
    private final String SUB_REPORTE = "reportes/Reporte_HistorialClinico_Detalle.jasper";
    private final String NOMBRE_LOGO = "imagenes/logo-patitas.png";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int idMascota;
        try {
            idMascota = Integer.parseInt(request.getParameter("mascota"));
        } catch (NumberFormatException ex) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta la mascota del reporte (mascota={id})");
            return;
        }

        InputStream reporte = getClass().getClassLoader().getResourceAsStream(NOMBRE_REPORTE);
        if (reporte == null) {
            throw new FileNotFoundException("No se encontró el reporte: " + NOMBRE_REPORTE);
        }

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("id_mascota", idMascota);
        parametros.put("generado_por", request.getParameter("generadoPor"));

        InputStream logo = getClass().getClassLoader().getResourceAsStream(NOMBRE_LOGO);
        if (logo == null) {
            throw new FileNotFoundException("No se encontró el logo: " + NOMBRE_LOGO);
        }
        Image imagen = ImageIO.read(logo);
        parametros.put("logo", imagen);

        parametros.put("subreporte", SUB_REPORTE);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=\"historial-clinico-" + idMascota + ".pdf\"");
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
        return "Servlet para generar el reporte de historial clinico";
    }
}
