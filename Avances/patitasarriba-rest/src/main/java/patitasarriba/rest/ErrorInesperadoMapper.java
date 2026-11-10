package patitasarriba.rest;

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
        if (ex instanceof WebApplicationException web) {
            return web.getResponse();
        }
        String mensaje = ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage();
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of("error", "Error inesperado: " + mensaje))
                .build();
    }
}
