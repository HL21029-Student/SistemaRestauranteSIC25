package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf;

import jakarta.enterprise.context.Dependent;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ActionEvent;
import jakarta.faces.validator.ValidatorException;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.event.SelectEvent;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.InventarioDAOInterface;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.InventarioDefaultDataAccess;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.ProductoDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.VentaDetalleDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.Producto;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.Venta;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.VentaDetalle;

import java.math.BigDecimal;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Dependent
@Named
public class VentaDetalleFrm extends DefaultFrm<VentaDetalle> implements Serializable {

    @Inject
    FacesContext facesContext;
    @Inject
    VentaDetalleDAO ventaDetalleDAO;

    @Inject
    ProductoDAO productoDAO;

    protected UUID idVenta;

    @Override
    protected FacesContext getFacesContext() {
        return facesContext;
    }

    @Override
    protected InventarioDAOInterface<VentaDetalle, Object> getDao() {
        return ventaDetalleDAO;
    }

    @Override
    protected String getIdAsText(VentaDetalle r) {
        if(r != null && r.getId() != null){
            return r.getId().toString();
        }
        return null;
    }

    @Override
    protected VentaDetalle getIdByText(String id) {
        if(id != null && !id.isBlank() && this.modelo.getWrappedData() != null && !this.modelo.getWrappedData().isEmpty()){
            try{
                UUID buscado = UUID.fromString(id);
                return this.modelo.getWrappedData().stream().filter(r -> r.getId().equals(buscado)).findFirst().orElse(null);
            }catch(IllegalArgumentException e){
                Logger.getLogger(VentaDetalleFrm.class.getName()).log(Level.SEVERE,e.getMessage(),e);
            }
        }
        return null;
    }

    @Override
    protected VentaDetalle nuevoRegistro() {
        VentaDetalle ventaDetalle = new VentaDetalle();
        ventaDetalle.setId(UUID.randomUUID());
        ventaDetalle.setCantidad(BigDecimal.valueOf(0));
        ventaDetalle.setPrecio(BigDecimal.valueOf(0.0));
        ventaDetalle.setEstado("ACTIVA");
        ventaDetalle.setObservaciones("");
        return ventaDetalle;
    }

    @Override
    public InventarioDefaultDataAccess getDataAccess() {
        return ventaDetalleDAO;
    }

    @Override
    protected VentaDetalle buscarRegistroPorId(Object id) {
        if(id instanceof UUID buscado && this.modelo != null){
            return this.modelo.getWrappedData().stream().filter(r -> r.getId().equals(buscado)).findFirst().orElse(null);
        }
        return null;
    }

    public List<Producto> buscarProductosPorNombre(final String nombreProducto){
        try{
            if(nombreProducto!=null && !nombreProducto.isBlank()){
                return productoDAO.buscarProductosPorNombre(nombreProducto, 0, Integer.MAX_VALUE);
            }
        } catch (Exception ex) {
            Logger.getLogger(VentaDetalleFrm.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return List.of();
    }

    public void btnSeleccionarProductoHandler(ActionEvent event){
        try {
            Object sel = event.getComponent().getAttributes().get("selectedProducto");
            if (!(sel instanceof Producto)) {
                return;
            }
            Producto seleccionado =
                    (Producto) sel;

            if (this.registro == null) {
                this.registro = nuevoRegistro();
            }

            this.registro.setIdProducto(seleccionado);

            if (this.registro.getCantidad() == null) {
                this.registro.setCantidad(java.math.BigDecimal.ONE);
            }

            facesContext.addMessage(null,
                    new jakarta.faces.application.FacesMessage(jakarta.faces.application.FacesMessage.SEVERITY_INFO,
                            "Producto seleccionado",
                            seleccionado.getNombreProducto()));
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(VentaDetalleFrm.class.getName()).log(java.util.logging.Level.SEVERE, ex.getMessage(), ex);
            facesContext.addMessage(null,
                    new jakarta.faces.application.FacesMessage(jakarta.faces.application.FacesMessage.SEVERITY_ERROR,
                            "Error al seleccionar producto",
                            ex.getMessage()));
        }

    }
    public void seleccionarProducto(SelectEvent<Producto> event) {
        try {
            Producto seleccionado = (event != null) ? event.getObject() : null;
            if (seleccionado == null) {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "No se seleccionó producto", null));
                return;
            }
            if (this.registro == null) {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "No hay venta activa", null));
                return;
            }
            this.registro.setIdProducto(seleccionado);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Producto seleccionado", seleccionado.getNombreProducto()));
        } catch (Exception ex) {
            Logger.getLogger(VentaDetalleFrm.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al seleccionar producto", ex.getMessage()));
        }
    }


    public void validarCantidad(jakarta.faces.context.FacesContext context, jakarta.faces.component.UIComponent component, Object value) {
        if (value == null) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cantidad inválida", "La cantidad es obligatoria"));
        }
        if (!(value instanceof BigDecimal)) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cantidad inválida", "Tipo de dato no válido"));
        }
        BigDecimal bd = (BigDecimal) value;
        if (bd.compareTo(BigDecimal.ONE) < 0) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cantidad inválida", "La cantidad debe ser un entero positivo"));
        }
        if (bd.stripTrailingZeros().scale() > 0) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cantidad inválida", "La cantidad debe ser un número entero"));
        }
    }

    public void validarPrecio(jakarta.faces.context.FacesContext context, jakarta.faces.component.UIComponent component, Object value) {
        if (value == null) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Precio inválido", "El precio es obligatorio"));
        }
        if (!(value instanceof BigDecimal)) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Precio inválido", "Tipo de dato no válido"));
        }
        BigDecimal bd = (BigDecimal) value;
        if (bd.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Precio inválido", "El precio debe ser positivo"));
        }
        // Limitar a 2 decimales
        if (bd.scale() > 2) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Precio inválido", "El precio debe tener como máximo 2 decimales"));
        }
    }

    @Override
    public List<VentaDetalle> cargarDatos(int first, int max) {
        try {
            if (first >= 0 && max > 0 && this.idVenta != null) {
                // llamar al DAO que devuelve solo los detalles de la venta
                return ventaDetalleDAO.findByIdVenta(this.idVenta, first, max);
            }
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(java.util.logging.Level.SEVERE, ex.getMessage(), ex);
        }
        return java.util.List.of();
    }

    @Override
    public int contarDatos() {
        try {
            if (this.idVenta != null) {
                // usar el método de conteo del DAO
                return this.ventaDetalleDAO.countByIdVenta(this.idVenta);
            }
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(java.util.logging.Level.SEVERE, ex.getMessage(), ex);
        }
        return 0;
    }

    @Override
    public void btnGuardarHandler(ActionEvent actionEvent) {
        try {
            if (this.idVenta != null) {
                if (this.registro == null) {
                    this.registro = nuevoRegistro();
                }
                if (this.registro.getIdVenta() == null) {
                    Venta v = new Venta();
                    v.setId(this.idVenta);
                    this.registro.setIdVenta(v);
                } else if (this.registro.getIdVenta().getId() == null) {
                    this.registro.getIdVenta().setId(this.idVenta);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(VentaDetalleFrm.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        super.btnGuardarHandler(actionEvent);
    }

    @Override
    public void btnModificarHandler(ActionEvent actionEvent) {
        try {
            if (this.idVenta != null && this.registro != null) {
                if (this.registro.getIdVenta() == null) {
                    Venta v = new Venta();
                    v.setId(this.idVenta);
                    this.registro.setIdVenta(v);
                } else if (this.registro.getIdVenta().getId() == null) {
                    this.registro.getIdVenta().setId(this.idVenta);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(VentaDetalleFrm.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        super.btnModificarHandler(actionEvent);
    }

    public UUID getIdVenta() {return idVenta;}

    protected void setIdVenta(UUID idVenta) {
        this.idVenta = idVenta;
    }

    protected String nombreBean="page.ventaDetalle";
    @Override
    public String getNombreBean() {return this.nombreBean;}

}