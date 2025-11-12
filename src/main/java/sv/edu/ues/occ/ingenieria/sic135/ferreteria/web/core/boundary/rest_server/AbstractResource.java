package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.rest_server;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.InventarioDefaultDataAccess;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractResource<T, ID> implements Serializable {

    private InventarioDefaultDataAccess<T, ID> bean;
    public abstract InventarioDefaultDataAccess<T, ID> getBean();
    public abstract UUID getIdEntity(T entity);

    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response create(T entity, @Context UriInfo uriInfo) {
        if (entity != null && getIdEntity(entity) == null) {
            try {
                bean = getBean();
                bean.create(entity);
                if (getIdEntity(entity) != null) {
                    UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
                    uriBuilder.path(String.valueOf(getIdEntity(entity)));
                    return Response.created(uriBuilder.build()).type(MediaType.APPLICATION_JSON).build();
                }
                return Response.status(400).header("Process-Error", "Record could not be created").build();
            } catch (Exception e) {
                return Response.status(500).entity(e.getMessage()).build();
            }
        }
        return Response.status(422).header("Wrong-Parameters", "Entity" + entity).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response findRange(
            @QueryParam("first")
            @DefaultValue("0")
            int firstResult,
            @QueryParam("max")
            @DefaultValue("100")
            int maxResult) {
        if (firstResult >= 0 && maxResult >= firstResult) {
            try {
                bean = getBean();
                List<T> encontrados = bean.findRange(firstResult, maxResult);
                Integer total = bean.count();
                if (encontrados != null) {
                    Response.ResponseBuilder builder = Response.ok(encontrados)
                            .header("Total-Records", total)
                            .type(MediaType.APPLICATION_JSON);
                    return builder.build();
                }
                return Response.status(404).header("Records-Not-Found", "Min " + firstResult + " Max " + maxResult).build();
            } catch (Exception e) {
                Logger.getLogger(AbstractResource.class.getName()).log(Level.SEVERE, e.getMessage(), e);
                return Response.status(500).entity(e.getMessage()).build();
            }
        } else {
            return Response.status(422).header("Wrong-Parameter", "first:" + firstResult + ",max:" + maxResult).build();
        }
    }

    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response update(T entity) {
        if (entity != null && getIdEntity(entity) != null) {
            try {
                bean.update(entity);
                T actualizado = bean.findById(getIdEntity(entity));
                if (entity.equals(actualizado)) {
                    return Response.ok(actualizado).type(MediaType.APPLICATION_JSON).build();
                }
                return Response.status(400).header("Process-Error", "Record could not be update").build();
            } catch (Exception e) {
                Logger.getLogger(AbstractResource.class.getName()).log(Level.SEVERE, e.getMessage(), e);
                return Response.status(500).entity(e.getMessage()).build();
            }
        }
        return Response.status(422).header("Wrong-Parameters", "Entity" + entity).build();
    }

    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response delete(T entity) {
        if (entity != null && getIdEntity(entity) != null) {
            try {
                T encontrado = bean.findById(getIdEntity(entity));
                if (encontrado != null) {
                    bean.delete(encontrado);
                    Response.ResponseBuilder builder = Response.ok(entity).type(MediaType.APPLICATION_JSON);
                    return builder.build();
                }
                return Response.status(404).header("Record-Not-Found", "Record could not be deleted").build();
            } catch (Exception e) {
                Logger.getLogger(AbstractResource.class.getName()).log(Level.SEVERE, e.getMessage(), e);
                return Response.status(500).entity(e.getMessage()).build();
            }
        }
        return Response.status(422).header("Wrong-Parameters", "Entity" + entity).build();
    }

    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response findById(@PathParam("id") ID id) {
        if (id != null) {
            try {
                bean = getBean();
                T encontrado = bean.findById(id);
                if (encontrado != null) {
                    Response.ResponseBuilder builder = Response.ok(encontrado).type(MediaType.APPLICATION_JSON);
                    return builder.build();
                }
                return Response.status(404).header("Record-Not-Found", "Id " + id).build();
            } catch (Exception e) {
                Logger.getLogger(AbstractResource.class.getName()).log(Level.SEVERE, e.getMessage(), e);
                return Response.status(500).entity(e.getMessage()).build();
            }
        }
        return Response.status(422).header("Wrong-Parameter", "Id" + id).build();
    }

}