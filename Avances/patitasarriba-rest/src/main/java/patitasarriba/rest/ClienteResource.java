package patitasarriba.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import patitasarriba.bl.AccesoBL;
import patitasarriba.bl.BLException;
import patitasarriba.bl.ClienteBL;
import patitasarriba.bl.NoEncontradoException;
import patitasarriba.bl.ValidacionException;
import patitasarriba.bl.impl.usuario.AccesoBLImpl;
import patitasarriba.bl.impl.usuario.ClienteBLImpl;
import patitasarriba.modelo.usuario.Cliente;
import patitasarriba.rest.dto.ClienteDTO;
import patitasarriba.rest.mapper.ClienteMapper;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Path("/clientes")
@Produces("application/json")
@Consumes("application/json")
public class ClienteResource {
    private final ClienteBL clienteBL;

    private final AccesoBL accesoBL = new AccesoBLImpl();

    @Context
    private UriInfo uriInfo;

    public ClienteResource() {
        clienteBL = new ClienteBLImpl();
    }

    @GET
    public Response findAll(@QueryParam("dni") String dni) {
        try {
            if (dni != null && !dni.isEmpty()) {
                Cliente cliente = clienteBL.findByDni(dni);
                if (cliente == null) {
                    return Response.status(Response.Status.NOT_FOUND)
                            .entity(Map.of("error", "No existe un cliente con el DNI " + dni))
                            .build();
                }
                return Response.ok(List.of(ClienteMapper.toDTO(cliente))).build();
            }
            return Response.ok(ClienteMapper.toDTOs(clienteBL.findAll())).build();
        } catch (BLException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", ex.getMessage())).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") int id) {
        try {
            return Response.ok(ClienteMapper.toDTO(clienteBL.findById(id))).build();
        } catch (NoEncontradoException ex) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", ex.getMessage())).build();
        } catch (BLException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", ex.getMessage())).build();
        }
    }

    @POST
    public Response insert(ClienteDTO clienteDTO) {
        try {
            Cliente cliente = ClienteMapper.toModelo(clienteDTO);
            clienteBL.insert(cliente);
            URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(cliente.getId())).build();
            return Response.created(uri).entity(ClienteMapper.toDTO(cliente)).build();
        } catch (ValidacionException ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", ex.getMessage())).build();
        } catch (BLException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", ex.getMessage())).build();
        }
    }

    // Alta hecha por un administrador: crea la persona y su cuenta en una sola transaccion. La cuenta lleva
    // solo el correo (cuenta.correo): la persona elige su contrasena con el enlace que recibe por correo
    @POST
    @Path("/con-cuenta")
    public Response insertConCuenta(ClienteDTO dto) {
        try {
            if (dto.getCuenta() == null) {
                throw new ValidacionException("Debe indicar el correo de la cuenta");
            }
            Cliente cliente = ClienteMapper.toModeloSinCuenta(dto);
            accesoBL.registrarCliente(cliente, dto.getCuenta().getCorreo());
            URI uri = uriInfo.getBaseUriBuilder().path("clientes").path(String.valueOf(cliente.getId())).build();
            return Response.created(uri).entity(ClienteMapper.toDTO(cliente)).build();
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
    public Response update(@PathParam("id") int id, ClienteDTO clienteDTO) {
        try {
            if (id != clienteDTO.getId()) {
                throw new ValidacionException("El id y el payload no coinciden");
            }
            Cliente cliente = ClienteMapper.toModelo(clienteDTO);
            clienteBL.update(cliente);
            return Response.ok(ClienteMapper.toDTO(cliente)).build();
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
            clienteBL.delete(id);
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
