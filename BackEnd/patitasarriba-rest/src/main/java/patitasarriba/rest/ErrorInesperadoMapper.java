package patitasarriba.rest;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.Map;

// Errores no previstos (NullPointerException, IllegalArgumentException del modelo, etc.): sin esto
// el servidor responde 500 con su pagina HTML. Asi la API siempre responde JSON con "error".
// Las excepciones propias de JAX-RS (404 de ruta, 415, etc.) conservan su codigo.
@Provider
public class ErrorInesperadoMapper implements ExceptionMapper<RuntimeException> {

    @Override
    public Response toResponse(RuntimeException ex) {
        if (ex instanceof ProcessingException) {
            return error(Response.Status.BAD_REQUEST.getStatusCode(),
                    "El cuerpo de la solicitud no es un JSON valido");
        }
        if (ex instanceof WebApplicationException web) {
            return error(web.getResponse().getStatus(), ex.getMessage());
        }
        String mensaje = ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage();
        return error(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Error inesperado: " + mensaje);
    }

    private Response error(int status, String mensaje) {
        return Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of("error", mensaje == null ? "Error en la solicitud" : mensaje))
                .build();
    }
}
