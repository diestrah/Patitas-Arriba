package patitasarriba.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import patitasarriba.bl.producto.ArticuloBL;
import patitasarriba.bl.excepciones.BLException;
import patitasarriba.bl.excepciones.NoEncontradoException;
import patitasarriba.bl.excepciones.ValidacionException;
import patitasarriba.blimpl.producto.ArticuloBLImpl;
import patitasarriba.modelo.producto.Articulo;
import patitasarriba.rest.dto.ArticuloDTO;
import patitasarriba.rest.mapper.ArticuloMapper;

import java.net.URI;
import java.util.Map;

@Path("/articulos")
@Produces("application/json")
@Consumes("application/json")
public class ArticuloResource {
    private final ArticuloBL articuloBL;

    @Context
    private UriInfo uriInfo;

    public ArticuloResource() {
        articuloBL = new ArticuloBLImpl();
    }

    @GET
    public Response findAll() {
        try {
            return Response.ok(ArticuloMapper.toDTOs(articuloBL.findAll())).build();
        } catch (BLException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", ex.getMessage())).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") int id) {
        try {
            return Response.ok(ArticuloMapper.toDTO(articuloBL.findById(id))).build();
        } catch (NoEncontradoException ex) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", ex.getMessage())).build();
        } catch (BLException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", ex.getMessage())).build();
        }
    }

    @POST
    public Response insert(ArticuloDTO articuloDTO) {
        try {
            Articulo articulo = ArticuloMapper.toModelo(articuloDTO);
            articuloBL.insert(articulo);
            URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(articulo.getId())).build();
            return Response.created(uri).entity(ArticuloMapper.toDTO(articulo)).build();
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
    public Response update(@PathParam("id") int id, ArticuloDTO articuloDTO) {
        try {
            if (id != articuloDTO.getId()) {
                throw new ValidacionException("El id y el payload no coinciden");
            }
            Articulo articulo = ArticuloMapper.toModelo(articuloDTO);
            articuloBL.update(articulo);
            return Response.ok(ArticuloMapper.toDTO(articulo)).build();
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
            articuloBL.delete(id);
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
