package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.InventarioDAOInterface;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.InventarioDefaultDataAccess;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.ProductoDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.Producto;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ViewScoped
public class ProductoFrm extends DefaultFrm<Producto> implements Serializable {
    @Inject
    FacesContext facesContext;
    @Inject
    ProductoDAO productoDAO;
    @Inject
    protected ProductoTipoProductoFrm productoTipoProductoFrm;
    List<Producto> productos;

    public ProductoFrm() {
    }

    @Override
    protected FacesContext getFacesContext() {
        return facesContext;
    }

    @Override
    protected InventarioDAOInterface<Producto, Object> getDao() {
        return productoDAO;
    }
    @Override
    protected String getIdAsText(Producto r) {
        if(r != null && r.getId() != null){
            return r.getId().toString();
        }
        return null;
    }
    @Override
    protected Producto getIdByText(String id) {
        if(id!= null){
            try{
                UUID buscado = UUID.fromString(id);
                return this.modelo.getWrappedData().stream().filter(r -> r.getId().equals(buscado)).findFirst().orElse(null);
            }catch(IllegalArgumentException e){
                Logger.getLogger(ProductoFrm.class.getName()).log(Level.SEVERE,e.getMessage(),e);
            }
        }
        return null;
    }

    @PostConstruct
    @Override
    public void inicializar(){
        super.inicializar();
        productos = productoDAO.findRange(0, Integer.MAX_VALUE);
    }

    @Override
    protected Producto nuevoRegistro() {
        Producto producto =new Producto();
        producto.setId(UUID.randomUUID());
        producto.setNombreProducto("");
        producto.setActivo(true);
        producto.setComentarios("");
        return producto;
    }
    @Override
    public InventarioDefaultDataAccess getDataAccess() {
        return productoDAO;
    }
    @Override
    protected Producto buscarRegistroPorId(Object id) {
        if(id instanceof  UUID buscado && this.modelo != null){
            return this.modelo.getWrappedData().stream().filter(r -> r.getId().equals(buscado)).findFirst().orElse(null);
        }
        return null;
    }

    protected String nombreBean="page.producto";
    public String getNombreBean() {return nombreBean;}
    public void setNombreBean(String nombreBean) {this.nombreBean = nombreBean;}
    public List<Producto> getListaProductos() {return productos;}
    public void setListaProductos(List<Producto> listaProductos) {this.productos = listaProductos;}

    public ProductoTipoProductoFrm getProductoTipoProductoFrm() {
        if(this.registro != null && this.registro.getId() != null){
            productoTipoProductoFrm.setIdProducto(this.registro.getId());
        }
        return productoTipoProductoFrm;
    }
}
