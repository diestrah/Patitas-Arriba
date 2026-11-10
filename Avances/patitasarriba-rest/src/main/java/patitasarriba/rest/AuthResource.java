package patitasarriba.rest;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import patitasarriba.bl.BLException;
import patitasarriba.bl.CuentaBL;
import patitasarriba.bl.ValidacionException;
import patitasarriba.bl.impl.usuario.CuentaBLImpl;
import patitasarriba.modelo.usuario.Cuenta;

import java.util.Map;

@Path("/login")
@Produces("application/json")
@Consumes("application/json")
public class AuthResource {
    private final CuentaBL cuentaBL;

    public AuthResource() {
        cuentaBL = new CuentaBLImpl();
    }

    @POST
    public Response login(Map<String, String> credenciales) {
        try {
            String nombreUsuario = credenciales.get("nombreUsuario");
            String password = credenciales.get("password");
            Cuenta cuenta = cuentaBL.login(nombreUsuario, password);
            return Response.ok(Map.of("id", cuenta.getId(), "nombreUsuario", cuenta.getNombreUsuario()))
                    .build();
        } catch (ValidacionException ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", ex.getMessage())).build();
        } catch (BLException ex) {
            // Las unicas BLException que puede lanzar login() son "Usuario o contraseña
            // incorrectos" (ver el comentario en CuentaBLImpl.login): por eso aqui, y solo aqui,
            // se mapea BLException a 401 en vez del generico 500.
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("error", ex.getMessage())).build();
        }
    }
}
