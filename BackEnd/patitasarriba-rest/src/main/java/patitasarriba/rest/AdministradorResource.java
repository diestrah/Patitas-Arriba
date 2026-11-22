package patitasarriba.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import patitasarriba.bl.usuario.AdministradorBL;
import patitasarriba.bl.usuario.AccesoBL;
import patitasarriba.bl.excepciones.BLException;
import patitasarriba.bl.excepciones.NoEncontradoException;
import patitasarriba.bl.excepciones.ValidacionException;
import patitasarriba.blimpl.usuario.AccesoBLImpl;
import patitasarriba.blimpl.usuario.AdministradorBLImpl;
import patitasarriba.modelo.usuario.Administrador;
import patitasarriba.rest.dto.AdministradorDTO;
import patitasarriba.rest.mapper.AdministradorMapper;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Path("/administradores")
@Produces("application/json")
@Consumes("application/json")
public class AdministradorResource {
    private final AdministradorBL administradorBL;

    private final AccesoBL accesoBL = new AccesoBLImpl();

    @Context
    private UriInfo uriInfo;

    public AdministradorResource() {
        administradorBL = new AdministradorBLImpl();
    }

    @GET
    public Response findAll(@QueryParam("dni") String dni) {
        try {
            if (dni != null && !dni.isEmpty()) {
                Administrador administrador = administradorBL.findByDni(dni);
                if (administrador == null) {
                    return Response.status(Response.Status.NOT_FOUND)
                            .entity(Map.of("error", "No existe un administrador con el DNI " + dni))
                            .build();
                }
                return Response.ok(List.of(AdministradorMapper.toDTO(administrador))).build();
            }
            return Response.ok(AdministradorMapper.toDTOs(administradorBL.findAll())).build();
        } catch (BLException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", ex.getMessage())).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") int id) {
        try {
            return Response.ok(AdministradorMapper.toDTO(administradorBL.findById(id))).build();
        } catch (NoEncontradoException ex) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", ex.getMessage())).build();
        } catch (BLException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", ex.getMessage())).build();
        }
    }

    @POST
    public Response insert(AdministradorDTO administradorDTO) {
        try {
            Administrador administrador = AdministradorMapper.toModelo(administradorDTO);
            administradorBL.insert(administrador);
            URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(administrador.getId())).build();
            return Response.created(uri).entity(AdministradorMapper.toDTO(administrador)).build();
        } catch (ValidacionException ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", ex.getMessage())).build();
        } catch (BLException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", ex.getMessage())).build();
        }
    }

    // Alta hecha por un administrador: crea la persona y su cuenta en una sola transaccion. La cuenta lleva
    // solo el correo (cuenta.correo): la persona elige su contrasena con el enlace que recibe por correo
    @POST
    @Path("/con-cuenta")
    public Response insertConCuenta(AdministradorDTO dto) {
        try {
            if (dto.getCuenta() == null) {
                throw new ValidacionException("Debe indicar el correo de la cuenta");
            }
            Administrador administrador = AdministradorMapper.toModeloSinCuenta(dto);
            accesoBL.registrarAdministrador(administrador, dto.getCuenta().getCorreo());
            URI uri = uriInfo.getBaseUriBuilder().path("administradores").path(String.valueOf(administrador.getId())).build();
            return Response.created(uri).entity(AdministradorMapper.toDTO(administrador)).build();
        } catch (ValidacionException ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", ex.getMessage())).build();
        } catch (BLException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", ex.getMessage())).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") int id, AdministradorDTO administradorDTO) {
        try {
            if (id != administradorDTO.getId()) {
                throw new ValidacionException("El id y el payload no coinciden");
            }
            Administrador administrador = AdministradorMapper.toModelo(administradorDTO);
            administradorBL.update(administrador);
            return Response.ok(AdministradorMapper.toDTO(administrador)).build();
        } catch (NoEncontradoException ex) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", ex.getMessage())).build();
        } catch (ValidacionException ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", ex.getMessage())).build();
        } catch (BLException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", ex.getMessage())).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") int id) {
        try {
            administradorBL.delete(id);
            return Response.noContent().build();
        } catch (NoEncontradoException ex) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", ex.getMessage())).build();
        } catch (ValidacionException ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", ex.getMessage())).build();
        } catch (BLException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", ex.getMessage())).build();
        }
    }
}
