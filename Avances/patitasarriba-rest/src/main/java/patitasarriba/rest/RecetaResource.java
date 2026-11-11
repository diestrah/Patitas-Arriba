package patitasarriba.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import patitasarriba.bl.excepciones.BLException;
import patitasarriba.bl.excepciones.NoEncontradoException;
import patitasarriba.bl.receta.RecetaBL;
import patitasarriba.bl.excepciones.ValidacionException;
import patitasarriba.blimpl.receta.RecetaBLImpl;
import patitasarriba.modelo.receta.Receta;
import patitasarriba.rest.dto.RecetaDTO;
import patitasarriba.rest.mapper.RecetaMapper;

import java.net.URI;
import java.util.Map;

@Path("/recetas")
@Produces("application/json")
@Consumes("application/json")
public class RecetaResource {
    private final RecetaBL recetaBL;

    @Context
    private UriInfo uriInfo;

    public RecetaResource() {
        recetaBL = new RecetaBLImpl();
    }

    // RF16, RF17: GET /recetas?atencion={id} lista las recetas emitidas en una atencion
    @GET
    public Response findAll(@QueryParam("atencion") Integer idAtencion) {
        try {
            if (idAtencion != null) {
                return Response.ok(RecetaMapper.toDTOs(recetaBL.findByAtencion(idAtencion))).build();
            }
            return Response.ok(RecetaMapper.toDTOs(recetaBL.findAll())).build();
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
            return Response.ok(RecetaMapper.toDTO(recetaBL.findById(id))).build();
        } catch (NoEncontradoException ex) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", ex.getMessage())).build();
        } catch (BLException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", ex.getMessage())).build();
        }
    }

    @POST
    public Response insert(RecetaDTO recetaDTO) {
        try {
            Receta receta = RecetaMapper.toModelo(recetaDTO);
            recetaBL.insert(receta);
            URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(receta.getId())).build();
            return Response.created(uri).entity(RecetaMapper.toDTO(receta)).build();
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
    public Response update(@PathParam("id") int id, RecetaDTO recetaDTO) {
        try {
            if (id != recetaDTO.getId()) {
                throw new ValidacionException("El id y el payload no coinciden");
            }
            Receta receta = RecetaMapper.toModelo(recetaDTO);
            recetaBL.update(receta);
            return Response.ok(RecetaMapper.toDTO(receta)).build();
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
            recetaBL.delete(id);
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
