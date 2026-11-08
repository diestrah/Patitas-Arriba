package patitasarriba.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import patitasarriba.bl.BLException;
import patitasarriba.bl.DiagnosticoBL;
import patitasarriba.bl.NoEncontradoException;
import patitasarriba.bl.ValidacionException;
import patitasarriba.bl.impl.atencion.DiagnosticoBLImpl;
import patitasarriba.modelo.atencion.Diagnostico;
import patitasarriba.rest.dto.DiagnosticoDTO;
import patitasarriba.rest.mapper.DiagnosticoMapper;

import java.net.URI;
import java.util.Map;

@Path("/diagnosticos")
@Produces("application/json")
@Consumes("application/json")
public class DiagnosticoResource {
    private final DiagnosticoBL diagnosticoBL;

    @Context
    private UriInfo uriInfo;

    public DiagnosticoResource() {
        diagnosticoBL = new DiagnosticoBLImpl();
    }

    @GET
    public Response findAll(@QueryParam("nombreEnfermedad") String nombreEnfermedad) {
        try {
            if (nombreEnfermedad != null && !nombreEnfermedad.isEmpty()) {
                Diagnostico diagnostico = diagnosticoBL.findByNombreEnfermedad(nombreEnfermedad);
                if (diagnostico == null) {
                    return Response.status(Response.Status.NOT_FOUND)
                            .entity(Map.of("error", "No existe un diagnostico con el nombre de enfermedad " + nombreEnfermedad))
                            .build();
                }
                return Response.ok(DiagnosticoMapper.toDTO(diagnostico)).build();
            }
            return Response.ok(DiagnosticoMapper.toDTOs(diagnosticoBL.findAll())).build();
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
            return Response.ok(DiagnosticoMapper.toDTO(diagnosticoBL.findById(id))).build();
        } catch (NoEncontradoException ex) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", ex.getMessage())).build();
        } catch (BLException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", ex.getMessage())).build();
        }
    }

    @POST
    public Response insert(DiagnosticoDTO diagnosticoDTO) {
        try {
            Diagnostico diagnostico = DiagnosticoMapper.toModelo(diagnosticoDTO);
            diagnosticoBL.insert(diagnostico);
            URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(diagnostico.getId())).build();
            return Response.created(uri).entity(DiagnosticoMapper.toDTO(diagnostico)).build();
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
    public Response update(@PathParam("id") int id, DiagnosticoDTO diagnosticoDTO) {
        try {
            if (id != diagnosticoDTO.getId()) {
                throw new ValidacionException("El id y el payload no coinciden");
            }
            Diagnostico diagnostico = DiagnosticoMapper.toModelo(diagnosticoDTO);
            diagnosticoBL.update(diagnostico);
            return Response.ok(DiagnosticoMapper.toDTO(diagnostico)).build();
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
            diagnosticoBL.delete(id);
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
