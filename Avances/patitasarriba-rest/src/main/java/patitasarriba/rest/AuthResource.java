package patitasarriba.rest;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import patitasarriba.bl.excepciones.BLException;
import patitasarriba.bl.excepciones.CredencialesInvalidasException;
import patitasarriba.bl.usuario.CuentaBL;
import patitasarriba.bl.excepciones.ValidacionException;
import patitasarriba.blimpl.usuario.CuentaBLImpl;
import patitasarriba.modelo.usuario.Administrador;
import patitasarriba.modelo.usuario.Cliente;
import patitasarriba.modelo.usuario.Persona;

import java.util.LinkedHashMap;
import java.util.Map;

@Path("/login")
@Produces("application/json")
@Consumes("application/json")
public class AuthResource {
    private final CuentaBL cuentaBL;

    public AuthResource() {
        cuentaBL = new CuentaBLImpl();
    }

    // RF01: devuelve la cuenta y la persona duena de ella, con su rol (CLIENTE, VETERINARIO o
    // ADMINISTRADOR), para que el frontend restrinja los modulos segun el rol
    @POST
    public Response login(Map<String, String> credenciales) {
        try {
            String correo = credenciales.get("correo");
            String password = credenciales.get("password");
            Persona persona = cuentaBL.login(correo, password);

            Map<String, Object> sesion = new LinkedHashMap<>();
            sesion.put("id", persona.getCuenta().getId());
            sesion.put("correo", persona.getCuenta().getCorreo());
            sesion.put("nombreUsuario", persona.getCuenta().getNombreUsuario());
            sesion.put("rol", rol(persona));
            sesion.put("idPersona", persona.getId());
            sesion.put("nombres", persona.getNombres());
            sesion.put("apellidoPaterno", persona.getApellidoPaterno());
            return Response.ok(sesion).build();
        } catch (ValidacionException ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", ex.getMessage())).build();
        } catch (CredencialesInvalidasException ex) {
            // Siempre el mismo mensaje: no revela si fallo el correo o la contrasena
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("error", ex.getMessage())).build();
        } catch (BLException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", ex.getMessage())).build();
        }
    }

    private static String rol(Persona persona) {
        if (persona instanceof Cliente) return "CLIENTE";
        if (persona instanceof Administrador) return "ADMINISTRADOR";
        return "VETERINARIO";
    }
}
