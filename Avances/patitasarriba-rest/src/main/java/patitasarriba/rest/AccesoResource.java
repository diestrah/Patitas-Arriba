package patitasarriba.rest;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import patitasarriba.bl.AccesoBL;
import patitasarriba.bl.BLException;
import patitasarriba.bl.ValidacionException;
import patitasarriba.bl.impl.usuario.AccesoBLImpl;
import patitasarriba.modelo.usuario.Cliente;

import java.util.Map;

// Rutas publicas (sin sesion): registro de clientes con verificacion de correo y recuperacion de contrasena
@Path("/acceso")
@Produces("application/json")
@Consumes("application/json")
public class AccesoResource {
    private final AccesoBL accesoBL;

    public AccesoResource() {
        accesoBL = new AccesoBLImpl();
    }

    // Body: { dni, nombres, apellidoPaterno, apellidoMaterno, telefono, correo, password }
    // Todavia no crea la cuenta: envia un correo con un enlace (202)
    @POST
    @Path("/registro")
    public Response solicitarRegistro(Map<String, String> datos) {
        try {
            if (datos == null) {
                throw new ValidacionException("Debe enviar los datos del registro");
            }
            Cliente cliente = new Cliente();
            try {
                cliente.setDni(datos.get("dni"));
                cliente.setNombres(datos.get("nombres"));
                cliente.setApellidoPaterno(datos.get("apellidoPaterno"));
                cliente.setApellidoMaterno(datos.get("apellidoMaterno"));
                cliente.setTelefono(datos.get("telefono"));
            } catch (IllegalArgumentException e) {
                throw new ValidacionException("Datos del registro invalidos: " + e.getMessage());
            }
            accesoBL.solicitarRegistro(cliente, datos.get("correo"), datos.get("password"));
            return Response.accepted(Map.of("mensaje", "Te enviamos un correo para confirmar tu registro")).build();
        } catch (ValidacionException ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", ex.getMessage())).build();
        } catch (BLException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", ex.getMessage())).build();
        }
    }

    // Body: { token } (el del enlace del correo). Crea la cuenta y el cliente (201)
    @POST
    @Path("/registro/confirmar")
    public Response confirmarRegistro(Map<String, String> datos) {
        try {
            accesoBL.confirmarRegistro(datos == null ? null : datos.get("token"));
            return Response.status(Response.Status.CREATED)
                    .entity(Map.of("mensaje", "Tu cuenta quedó activa. Ya puedes iniciar sesión")).build();
        } catch (ValidacionException ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", ex.getMessage())).build();
        } catch (BLException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", ex.getMessage())).build();
        }
    }

    // Body: { correo }. Siempre responde 202 igual (exista o no el correo) para no revelar que cuentas hay
    @POST
    @Path("/recuperacion")
    public Response solicitarRecuperacion(Map<String, String> datos) {
        try {
            accesoBL.solicitarRecuperacion(datos == null ? null : datos.get("correo"));
            return Response.accepted(Map.of("mensaje",
                    "Si el correo está registrado, te enviamos un enlace para restablecer tu contraseña")).build();
        } catch (ValidacionException ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", ex.getMessage())).build();
        } catch (BLException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", ex.getMessage())).build();
        }
    }

    // Body: { token, password }. El token es de un solo uso y vence
    @POST
    @Path("/recuperacion/restablecer")
    public Response restablecerContrasena(Map<String, String> datos) {
        try {
            if (datos == null) {
                throw new ValidacionException("Debe enviar el token y la contraseña nueva");
            }
            accesoBL.restablecerContrasena(datos.get("token"), datos.get("password"));
            return Response.ok(Map.of("mensaje", "Tu contraseña se actualizó. Ya puedes iniciar sesión")).build();
        } catch (ValidacionException ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", ex.getMessage())).build();
        } catch (BLException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", ex.getMessage())).build();
        }
    }
}
