package patitasarriba.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import patitasarriba.bl.BLException;
import patitasarriba.bl.HorarioBL;
import patitasarriba.bl.NoEncontradoException;
import patitasarriba.bl.ValidacionException;
import patitasarriba.bl.impl.horario.HorarioBLImpl;
import patitasarriba.modelo.horario.Horario;
import patitasarriba.rest.dto.HorarioDTO;
import patitasarriba.rest.mapper.HorarioMapper;

import java.net.URI;
import java.util.Map;

@Path("/horarios")
@Produces("application/json")
@Consumes("application/json")
public class HorarioResource {
    private final HorarioBL horarioBL;

    @Context
    private UriInfo uriInfo;

    public HorarioResource() {
        horarioBL = new HorarioBLImpl();
    }

    @GET
    public Response findAll() {
        try {
            return Response.ok(HorarioMapper.toDTOs(horarioBL.findAll())).build();
        } catch (BLException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", ex.getMessage())).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") int id) {
        try {
            return Response.ok(HorarioMapper.toDTO(horarioBL.findById(id))).build();
        } catch (NoEncontradoException ex) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", ex.getMessage())).build();
        } catch (BLException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", ex.getMessage())).build();
        }
    }

    @POST
    public Response insert(HorarioDTO horarioDTO) {
        try {
            Horario horario = HorarioMapper.toModelo(horarioDTO);
            horarioBL.insert(horario);
            URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(horario.getId())).build();
            return Response.created(uri).entity(HorarioMapper.toDTO(horario)).build();
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
    public Response update(@PathParam("id") int id, HorarioDTO horarioDTO) {
        try {
            if (id != horarioDTO.getId()) {
                throw new ValidacionException("El id y el payload no coinciden");
            }
            Horario horario = HorarioMapper.toModelo(horarioDTO);
            horarioBL.update(horario);
            return Response.ok(HorarioMapper.toDTO(horario)).build();
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
            horarioBL.delete(id);
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
