package patitasarriba.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import patitasarriba.bl.BLException;
import patitasarriba.bl.CuentaBL;
import patitasarriba.bl.NoEncontradoException;
import patitasarriba.bl.ValidacionException;
import patitasarriba.bl.impl.usuario.CuentaBLImpl;
import patitasarriba.modelo.usuario.Cuenta;
import patitasarriba.rest.dto.CuentaDTO;
import patitasarriba.rest.mapper.CuentaMapper;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Path("/cuentas")
@Produces("application/json")
@Consumes("application/json")
public class CuentaResource {
    private final CuentaBL cuentaBL;

    @Context
    private UriInfo uriInfo;

    public CuentaResource() {
        cuentaBL = new CuentaBLImpl();
    }

    @GET
    public Response findAll(@QueryParam("nombreUsuario") String nombreUsuario) {
        try {
            if (nombreUsuario != null && !nombreUsuario.isEmpty()) {
                Cuenta cuenta = cuentaBL.findByNombreUsuario(nombreUsuario);
                if (cuenta == null) {
                    return Response.status(Response.Status.NOT_FOUND)
                            .entity(Map.of("error", "No existe una cuenta con el nombre de usuario " + nombreUsuario))
                            .build();
                }
                return Response.ok(List.of(CuentaMapper.toDTO(cuenta))).build();
            }
            return Response.ok(CuentaMapper.toDTOs(cuentaBL.findAll())).build();
        } catch (BLException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", ex.getMessage())).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") int id) {
        try {
            return Response.ok(CuentaMapper.toDTO(cuentaBL.findById(id))).build();
        } catch (NoEncontradoException ex) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", ex.getMessage())).build();
        } catch (BLException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", ex.getMessage())).build();
        }
    }

    @POST
    public Response insert(CuentaDTO cuentaDTO) {
        try {
            Cuenta cuenta = CuentaMapper.toModelo(cuentaDTO);
            cuentaBL.insert(cuenta);
            URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(cuenta.getId())).build();
            return Response.created(uri).entity(CuentaMapper.toDTO(cuenta)).build();
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
    public Response update(@PathParam("id") int id, CuentaDTO cuentaDTO) {
        try {
            if (id != cuentaDTO.getId()) {
                throw new ValidacionException("El id y el payload no coinciden");
            }
            Cuenta cuenta = CuentaMapper.toModelo(cuentaDTO);
            cuentaBL.update(cuenta);
            return Response.ok(CuentaMapper.toDTO(cuenta)).build();
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
            cuentaBL.delete(id);
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
