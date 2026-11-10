package patitasarriba.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import patitasarriba.bl.BLException;
import patitasarriba.bl.BoletaBL;
import patitasarriba.bl.NoEncontradoException;
import patitasarriba.bl.ValidacionException;
import patitasarriba.bl.impl.boleta.BoletaBLImpl;
import patitasarriba.modelo.boleta.Boleta;
import patitasarriba.rest.dto.BoletaDTO;
import patitasarriba.rest.mapper.BoletaMapper;

import java.net.URI;
import java.util.Map;

@Path("/boletas")
@Produces("application/json")
@Consumes("application/json")
public class BoletaResource {
    private final BoletaBL boletaBL;

    @Context
    private UriInfo uriInfo;

    public BoletaResource() {
        boletaBL = new BoletaBLImpl();
    }

    @GET
    public Response findAll() {
        try {
            return Response.ok(BoletaMapper.toDTOs(boletaBL.findAll())).build();
        } catch (BLException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", ex.getMessage())).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") int id) {
        try {
            return Response.ok(BoletaMapper.toDTO(boletaBL.findById(id))).build();
        } catch (NoEncontradoException ex) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", ex.getMessage())).build();
        } catch (BLException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", ex.getMessage())).build();
        }
    }

    @POST
    public Response insert(BoletaDTO boletaDTO) {
        try {
            Boleta boleta = BoletaMapper.toModelo(boletaDTO);
            boletaBL.insert(boleta);
            URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(boleta.getId())).build();
            return Response.created(uri).entity(BoletaMapper.toDTO(boleta)).build();
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
    public Response update(@PathParam("id") int id, BoletaDTO boletaDTO) {
        try {
            if (id != boletaDTO.getId()) {
                throw new ValidacionException("El id y el payload no coinciden");
            }
            Boleta boleta = BoletaMapper.toModelo(boletaDTO);
            boletaBL.update(boleta);
            return Response.ok(BoletaMapper.toDTO(boleta)).build();
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
            boletaBL.delete(id);
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
