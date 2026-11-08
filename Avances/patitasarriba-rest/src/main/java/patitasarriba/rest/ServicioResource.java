package patitasarriba.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import patitasarriba.bl.BLException;
import patitasarriba.bl.ServicioBL;
import patitasarriba.bl.ValidacionException;
import patitasarriba.bl.impl.producto.ServicioBLImpl;
import patitasarriba.modelo.producto.Servicio;
import patitasarriba.rest.dto.ServicioDTO;
import patitasarriba.rest.mapper.ServicioMapper;

import java.net.URI;
import java.util.Map;

@Path("/servicios")
@Produces("application/json")
@Consumes("application/json")
public class ServicioResource {
    private final ServicioBL servicioBL;

    @Context
    private UriInfo uriInfo;

    public ServicioResource() {
        servicioBL = new ServicioBLImpl();
    }

    @GET
    public Response findAll(@QueryParam("nombre") String nombre) {
        try {
            if (nombre != null && !nombre.isEmpty()) {
                Servicio servicio = servicioBL.findByName(nombre);
                if (servicio == null) {
                    return Response.status(Response.Status.NOT_FOUND)
                            .entity(Map.of("error", "No existe un servicio con el nombre " + nombre))
                            .build();
                }
                return Response.ok(ServicioMapper.toDTO(servicio)).build();
            }
            return Response.ok(ServicioMapper.toDTOs(servicioBL.findAll())).build();
        } catch (ValidacionException ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", ex.getMessage())).build();
        } catch (BLException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", ex.getMessage())).build();
        }
    }

    // ServicioBLImpl.findById todavia no sigue el patron null -> NoEncontradoException que usan
    // las demas entidades (ver detalle en el resumen de la implementacion): el chequeo de null
    // se hace aqui mismo, a mano, mientras eso no se homologue en la capa de negocio.
    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") int id) {
        try {
            Servicio servicio = servicioBL.findById(id);
            if (servicio == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("error", "No existe un servicio con id " + id)).build();
            }
            return Response.ok(ServicioMapper.toDTO(servicio)).build();
        } catch (BLException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", ex.getMessage())).build();
        }
    }

    @POST
    public Response insert(ServicioDTO servicioDTO) {
        try {
            Servicio servicio = ServicioMapper.toModelo(servicioDTO);
            servicioBL.insert(servicio);
            URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(servicio.getId())).build();
            return Response.created(uri).entity(ServicioMapper.toDTO(servicio)).build();
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
    public Response update(@PathParam("id") int id, ServicioDTO servicioDTO) {
        try {
            if (id != servicioDTO.getId()) {
                throw new ValidacionException("El id y el payload no coinciden");
            }
            Servicio servicio = ServicioMapper.toModelo(servicioDTO);
            servicioBL.update(servicio);
            return Response.ok(ServicioMapper.toDTO(servicio)).build();
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
            servicioBL.delete(id);
            return Response.noContent().build();
        } catch (BLException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", ex.getMessage())).build();
        }
    }
}
