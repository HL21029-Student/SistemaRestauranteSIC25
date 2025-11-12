package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.rest_server;


import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.ProductoTipoProductoDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.ProductoTipoProducto;

import java.util.List;
import java.util.UUID;

@Path("producto/{idTipoProducto}/tipo_producto")
public class ProductoTipoProductoResource {

    @Inject
    ProductoTipoProductoDAO productoTipoProductoDAO;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(
            @Min(0)
            @DefaultValue("0")
            @QueryParam("first")
            int first,
            @Max(100)
            @DefaultValue("50")
            @QueryParam("max")
            int max,
            @PathParam("idTipoProducto")
            UUID idTipoProducto
    ){
        List<ProductoTipoProducto> salida = productoTipoProductoDAO.findByidProducto(idTipoProducto, first, max);
        return Response.ok(salida).build();
    }

}
