package patitasarriba.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import patitasarriba.bl.BLException;
import patitasarriba.bl.CategoriaBL;
import patitasarriba.bl.NoEncontradoException;
import patitasarriba.bl.ValidacionException;
import patitasarriba.bl.impl.producto.CategoriaBLImpl;
import patitasarriba.modelo.producto.Categoria;
import patitasarriba.rest.dto.CategoriaDTO;
import patitasarriba.rest.mapper.CategoriaMapper;

import java.net.URI;
import java.util.Map;

@Path("/categorias")
@Produces("application/json")
@Consumes("application/json")
public class CategoriaResource {
    private final CategoriaBL categoriaBL;

    @Context
    private UriInfo uriInfo;

    public CategoriaResource() {
        categoriaBL = new CategoriaBLImpl();
    }

    @GET
    public Response findAll() {
        try {
            return Response.ok(CategoriaMapper.toDTOs(categoriaBL.findAll())).build();
        } catch (BLException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", ex.getMessage())).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") int id) {
        try {
            return Response.ok(CategoriaMapper.toDTO(categoriaBL.findById(id))).build();
        } catch (NoEncontradoException ex) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", ex.getMessage())).build();
        } catch (BLException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", ex.getMessage())).build();
        }
    }

    @POST
    public Response insert(CategoriaDTO categoriaDTO) {
        try {
            Categoria categoria = CategoriaMapper.toModelo(categoriaDTO);
            categoriaBL.insert(categoria);
            URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(categoria.getId())).build();
            return Response.created(uri).entity(CategoriaMapper.toDTO(categoria)).build();
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
    public Response update(@PathParam("id") int id, CategoriaDTO categoriaDTO) {
        try {
            if (id != categoriaDTO.getId()) {
                throw new ValidacionException("El id y el payload no coinciden");
            }
            Categoria categoria = CategoriaMapper.toModelo(categoriaDTO);
            categoriaBL.update(categoria);
            return Response.ok(CategoriaMapper.toDTO(categoria)).build();
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
            categoriaBL.delete(id);
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
