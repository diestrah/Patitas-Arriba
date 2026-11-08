package patitasarriba.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import patitasarriba.bl.BLException;
import patitasarriba.bl.CitaBL;
import patitasarriba.bl.NoEncontradoException;
import patitasarriba.bl.ValidacionException;
import patitasarriba.bl.impl.cita.CitaBLImpl;
import patitasarriba.modelo.cita.Cita;
import patitasarriba.rest.dto.CitaDTO;
import patitasarriba.rest.mapper.CitaMapper;

import java.net.URI;
import java.util.Map;

@Path("/citas")
@Produces("application/json")
@Consumes("application/json")
public class CitaResource {
    private final CitaBL citaBL;

    @Context
    private UriInfo uriInfo;

    public CitaResource() {
        citaBL = new CitaBLImpl();
    }

    @GET
    public Response findAll() {
        try {
            return Response.ok(CitaMapper.toDTOs(citaBL.findAll())).build();
        } catch (BLException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", ex.getMessage())).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") int id) {
        try {
            return Response.ok(CitaMapper.toDTO(citaBL.findById(id))).build();
        } catch (NoEncontradoException ex) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", ex.getMessage())).build();
        } catch (BLException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", ex.getMessage())).build();
        }
    }

    @POST
    public Response insert(CitaDTO citaDTO) {
        try {
            Cita cita = CitaMapper.toModelo(citaDTO);
            citaBL.insert(cita);
            URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(cita.getId())).build();
            return Response.created(uri).entity(CitaMapper.toDTO(cita)).build();
        } catch (ValidacionException ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", ex.getMessage())).build();
        } catch (BLException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", ex.getMessage())).build();
        }
    }

    // Un mismo PUT cubre reprogramar, aprobar/rechazar y completar: se manda el CitaDTO completo
    // con el estado deseado y CitaBLImpl.validarCambioDeEstado decide si la transicion es valida
    // (ver REST_SERVICIOS.md §5.12) - no hay sub-rutas especiales para cada accion.
    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") int id, CitaDTO citaDTO) {
        try {
            if (id != citaDTO.getId()) {
                throw new ValidacionException("El id y el payload no coinciden");
            }
            Cita cita = CitaMapper.toModelo(citaDTO);
            citaBL.update(cita);
            return Response.ok(CitaMapper.toDTO(cita)).build();
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
            citaBL.delete(id);
            return Response.noContent().build();
        } catch (NoEncontradoException ex) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", ex.getMessage())).build();
        } catch (BLException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", ex.getMessage())).build();
        }
    }
}
