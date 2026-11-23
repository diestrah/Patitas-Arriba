package patitasarriba.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import patitasarriba.bl.usuario.AccesoBL;
import patitasarriba.bl.excepciones.BLException;
import patitasarriba.bl.excepciones.NoEncontradoException;
import patitasarriba.bl.excepciones.ValidacionException;
import patitasarriba.blimpl.usuario.AccesoBLImpl;
import patitasarriba.bl.usuario.VeterinarioBL;
import patitasarriba.blimpl.usuario.VeterinarioBLImpl;
import patitasarriba.modelo.usuario.Veterinario;
import patitasarriba.rest.dto.VeterinarioDTO;
import patitasarriba.rest.mapper.VeterinarioMapper;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Path("/veterinarios")
@Produces("application/json")
@Consumes("application/json")
public class VeterinarioResource {
    private final VeterinarioBL veterinarioBL;

    private final AccesoBL accesoBL = new AccesoBLImpl();

    @Context
    private UriInfo uriInfo;

    public VeterinarioResource() {
        veterinarioBL = new VeterinarioBLImpl();
    }

    @GET
    public Response findAll(@QueryParam("dni") String dni) {
        try {
            if (dni != null && !dni.isEmpty()) {
                Veterinario veterinario = veterinarioBL.findByDni(dni);
                if (veterinario == null) {
                    return Response.status(Response.Status.NOT_FOUND)
                            .entity(Map.of("error", "No existe un veterinario con el DNI " + dni))
                            .build();
                }
                return Response.ok(List.of(VeterinarioMapper.toDTO(veterinario))).build();
            }
            return Response.ok(VeterinarioMapper.toDTOs(veterinarioBL.findAll())).build();
        } catch (BLException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", ex.getMessage())).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") int id) {
        try {
            return Response.ok(VeterinarioMapper.toDTO(veterinarioBL.findById(id))).build();
        } catch (NoEncontradoException ex) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", ex.getMessage())).build();
        } catch (BLException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", ex.getMessage())).build();
        }
    }

    @POST
    public Response insert(VeterinarioDTO veterinarioDTO) {
        try {
            Veterinario veterinario = VeterinarioMapper.toModelo(veterinarioDTO);
            veterinarioBL.insert(veterinario);
            URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(veterinario.getId())).build();
            return Response.created(uri).entity(VeterinarioMapper.toDTO(veterinario)).build();
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
    public Response insertConCuenta(VeterinarioDTO dto) {
        try {
            if (dto.getCuenta() == null) {
                throw new ValidacionException("Debe indicar el correo de la cuenta");
            }
            Veterinario veterinario = VeterinarioMapper.toModeloSinCuenta(dto);
            accesoBL.registrarVeterinario(veterinario, dto.getCuenta().getCorreo());
            URI uri = uriInfo.getBaseUriBuilder().path("veterinarios").path(String.valueOf(veterinario.getId())).build();
            return Response.created(uri).entity(VeterinarioMapper.toDTO(veterinario)).build();
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
    public Response update(@PathParam("id") int id, VeterinarioDTO veterinarioDTO) {
        try {
            if (id != veterinarioDTO.getId()) {
                throw new ValidacionException("El id y el payload no coinciden");
            }
            Veterinario veterinario = VeterinarioMapper.toModelo(veterinarioDTO);
            veterinarioBL.update(veterinario);
            return Response.ok(VeterinarioMapper.toDTO(veterinario)).build();
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
            veterinarioBL.delete(id);
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
