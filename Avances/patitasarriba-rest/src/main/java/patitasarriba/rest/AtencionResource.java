package patitasarriba.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import patitasarriba.bl.AtencionBL;
import patitasarriba.bl.BLException;
import patitasarriba.bl.NoEncontradoException;
import patitasarriba.bl.ValidacionException;
import patitasarriba.bl.impl.atencion.AtencionBLImpl;
import patitasarriba.modelo.atencion.Atencion;
import patitasarriba.rest.dto.AtencionDTO;
import patitasarriba.rest.mapper.AtencionMapper;

import java.net.URI;
import java.util.Map;

@Path("/atenciones")
@Produces("application/json")
@Consumes("application/json")
public class AtencionResource {
    private final AtencionBL atencionBL;

    @Context
    private UriInfo uriInfo;

    public AtencionResource() {
        atencionBL = new AtencionBLImpl();
    }

    // RF16: GET /atenciones?mascota={id} es el historial clinico de una mascota (orden cronologico)
    @GET
    public Response findAll(@QueryParam("mascota") Integer idMascota) {
        try {
            if (idMascota != null) {
                return Response.ok(AtencionMapper.toDTOs(atencionBL.findByMascota(idMascota))).build();
            }
            return Response.ok(AtencionMapper.toDTOs(atencionBL.findAll())).build();
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
            return Response.ok(AtencionMapper.toDTO(atencionBL.findById(id))).build();
        } catch (NoEncontradoException ex) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", ex.getMessage())).build();
        } catch (BLException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", ex.getMessage())).build();
        }
    }

    @POST
    public Response insert(AtencionDTO atencionDTO) {
        try {
            Atencion atencion = AtencionMapper.toModelo(atencionDTO);
            atencionBL.insert(atencion);
            URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(atencion.getId())).build();
            return Response.created(uri).entity(AtencionMapper.toDTO(atencion)).build();
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
    public Response update(@PathParam("id") int id, AtencionDTO atencionDTO) {
        try {
            if (id != atencionDTO.getId()) {
                throw new ValidacionException("El id y el payload no coinciden");
            }
            Atencion atencion = AtencionMapper.toModelo(atencionDTO);
            atencionBL.update(atencion);
            return Response.ok(AtencionMapper.toDTO(atencion)).build();
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
            atencionBL.delete(id);
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
