package patitasarriba.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import patitasarriba.bl.excepciones.BLException;
import patitasarriba.bl.cita.CitaBL;
import patitasarriba.bl.excepciones.NoEncontradoException;
import patitasarriba.bl.excepciones.ValidacionException;
import patitasarriba.blimpl.cita.CitaBLImpl;
import patitasarriba.modelo.cita.Cita;
import patitasarriba.rest.dto.CitaDTO;
import patitasarriba.rest.mapper.CitaMapper;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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

    // RF10: GET /citas?cliente={id} lista las citas de las mascotas de un cliente.
    // RF11, RF13: GET /citas?fecha=AAAA-MM-DD[&veterinario={id}] es la agenda de un dia.
    @GET
    public Response findAll(@QueryParam("cliente") Integer idCliente,
                            @QueryParam("fecha") String fecha,
                            @QueryParam("veterinario") Integer idVeterinario) {
        try {
            if (idCliente != null) {
                return Response.ok(CitaMapper.toDTOs(citaBL.findByCliente(idCliente))).build();
            }
            if (fecha != null && !fecha.isEmpty()) {
                LocalDate dia;
                try {
                    dia = LocalDate.parse(fecha);
                } catch (DateTimeParseException e) {
                    throw new ValidacionException("La fecha debe tener el formato AAAA-MM-DD");
                }
                return Response.ok(CitaMapper.toDTOs(citaBL.findByFecha(dia, idVeterinario))).build();
            }
            return Response.ok(CitaMapper.toDTOs(citaBL.findAll())).build();
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
    // - no hay sub-rutas especiales para cada accion.
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
        } catch (ValidacionException ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", ex.getMessage())).build();
        } catch (BLException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", ex.getMessage())).build();
        }
    }
}
