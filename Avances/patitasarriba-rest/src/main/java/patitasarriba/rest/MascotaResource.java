package patitasarriba.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import patitasarriba.bl.excepciones.BLException;
import patitasarriba.bl.mascota.MascotaBL;
import patitasarriba.bl.excepciones.NoEncontradoException;
import patitasarriba.bl.excepciones.ValidacionException;
import patitasarriba.blimpl.mascota.MascotaBLImpl;
import patitasarriba.modelo.mascota.Mascota;
import patitasarriba.rest.dto.MascotaDTO;
import patitasarriba.rest.mapper.MascotaMapper;

import java.net.URI;
import java.util.Map;

@Path("/mascotas")
@Produces("application/json")
@Consumes("application/json")
public class MascotaResource {
    private final MascotaBL mascotaBL;

    @Context
    private UriInfo uriInfo;

    public MascotaResource() {
        mascotaBL = new MascotaBLImpl();
    }

    // RF03: GET /mascotas?cliente={id} lista las mascotas activas de un cliente
    @GET
    public Response findAll(@QueryParam("cliente") Integer idCliente) {
        try {
            if (idCliente != null) {
                return Response.ok(MascotaMapper.toDTOs(mascotaBL.findByCliente(idCliente))).build();
            }
            return Response.ok(MascotaMapper.toDTOs(mascotaBL.findAll())).build();
        } catch (ValidacionException ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", ex.getMessage())).build();
        } catch (BLException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", ex.getMessage())).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") int id) {
        try {
            return Response.ok(MascotaMapper.toDTO(mascotaBL.findById(id))).build();
        } catch (NoEncontradoException ex) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", ex.getMessage())).build();
        } catch (BLException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", ex.getMessage())).build();
        }
    }

    @POST
    public Response insert(MascotaDTO mascotaDTO) {
        try {
            Mascota mascota = MascotaMapper.toModelo(mascotaDTO);
            mascotaBL.insert(mascota);
            URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(mascota.getId())).build();
            return Response.created(uri).entity(MascotaMapper.toDTO(mascota)).build();
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
    public Response update(@PathParam("id") int id, MascotaDTO mascotaDTO) {
        try {
            if (id != mascotaDTO.getId()) {
                throw new ValidacionException("El id y el payload no coinciden");
            }
            Mascota mascota = MascotaMapper.toModelo(mascotaDTO);
            mascotaBL.update(mascota);
            return Response.ok(MascotaMapper.toDTO(mascota)).build();
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
            mascotaBL.delete(id);
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
