package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ActionEvent;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.event.SelectEvent;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.*;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.Cliente;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.Venta;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.VentaDetalle;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ViewScoped
public class VentaFrm extends DefaultFrm<Venta> implements Serializable {

    @Inject
    FacesContext facesContext;

    @Inject
    VentaDAO ventaDAO;

    @Inject
    private ClienteDAO clienteDAO;

    @Inject
    private VentaDetalleDAO ventaDetalleDAO;

    @Inject
    protected VentaDetalleFrm ventaDetalleFrm;

    private List<Venta> listaVentas;
    private List<Cliente> listaClientes;

    private String nombreBean = "page.venta";

    public VentaFrm() {}

    @Override
    protected FacesContext getFacesContext() {
        return facesContext;
    }

    @Override
    protected InventarioDAOInterface<Venta, Object> getDao() {
        return ventaDAO;
    }

    @Override
    protected String getIdAsText(Venta r) {
        if (r != null && r.getId() != null) {
            return r.getId().toString();
        }
        return null;
    }

    @Override
    protected Venta getIdByText(String id) {
        if (id != null) {
            try {
                UUID buscado = UUID.fromString(id);
                return this.modelo.getWrappedData().stream()
                        .filter(r -> r.getId().equals(buscado))
                        .findFirst()
                        .orElse(null);
            } catch (IllegalArgumentException e) {
                Logger.getLogger(VentaFrm.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        return null;
    }
    @PostConstruct
    @Override
    public void inicializar() {
        super.inicializar();
        try {
            listaVentas = ventaDAO.findRange(0, Integer.MAX_VALUE);
            listaClientes = clienteDAO.findRange(0, Integer.MAX_VALUE);
        } catch (Exception ex) {
            Logger.getLogger(VentaFrm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected Venta nuevoRegistro() {
        Venta v = new Venta();
        v.setId(UUID.randomUUID());
        v.setEstado("ACTIVA");
        v.setObservaciones("");
        v.setFecha(new Date()); // inicializa con la fecha actual
        return v;
    }

    @Override
    public InventarioDefaultDataAccess getDataAccess() {
        return ventaDAO;
    }

    @Override
    protected Venta buscarRegistroPorId(Object id) {
        if (id instanceof UUID buscado && this.modelo != null) {
            return this.modelo.getWrappedData().stream()
                    .filter(r -> r.getId().equals(buscado))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }


    public java.math.BigDecimal calcularMontoTotal(Venta venta) {
        if (venta == null || venta.getId() == null) {
            return BigDecimal.ZERO;
        }
        try {
            List<VentaDetalle> detalles = ventaDetalleDAO.findByIdVenta(venta.getId(), 0, Integer.MAX_VALUE);
            return ventaDetalleDAO.calcularMontoTotal(detalles);
        } catch (Exception ex) {
            Logger.getLogger(VentaFrm.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return BigDecimal.ZERO;
        }
    }


    @Override
    public void btnGuardarHandler(ActionEvent actionEvent) {
        if (!validarCampos()) return;
        super.btnGuardarHandler(actionEvent);
    }

    @Override
    public void btnModificarHandler(ActionEvent actionEvent) {
        if (!validarCampos()) return;
        super.btnModificarHandler(actionEvent);
    }

    @Override
    public void seleccionarRegistro(SelectEvent<Venta> event) {
        super.seleccionarRegistro(event);
        try {
            if (this.registro != null && this.registro.getId() != null) {
                ventaDetalleFrm.setIdVenta(this.registro.getId());
            } else {
                ventaDetalleFrm.setIdVenta(null);
            }
            ventaDetalleFrm.inicializarRegistros();
        } catch (Exception ex) {
            Logger.getLogger(VentaFrm.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public void btnSeleccionarClienteHandler(ActionEvent event){
        try{
            if (this.registro == null) {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "No hay venta activa", null));
                return;
            }

            Cliente sel = this.registro.getIdCliente();
            if (sel == null) {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Seleccione un cliente", null));
                return;
            }
            if (sel.getId() != null) {
                Cliente completo = clienteDAO.findById(sel.getId());
                if (completo != null) {
                    this.registro.setIdCliente(completo);
                    facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Cliente asignado", null));
                    return;
                }
            }
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Cliente asignado (sin carga adicional)", null));
        }catch (Exception ex){
            Logger.getLogger(VentaFrm.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public void seleccionarCliente(final SelectEvent<Cliente> event) {
        try {
            Cliente seleccionado = event != null ? event.getObject() : null;
            if (seleccionado == null) {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Cliente no seleccionado", null));
                return;
            }
            if (seleccionado.getId() != null) {
                Cliente completo = clienteDAO.findById(seleccionado.getId());
                this.registro.setIdCliente(completo != null ? completo : seleccionado);
            } else {
                this.registro.setIdCliente(seleccionado);
            }
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Cliente seleccionado", null));
        } catch (Exception ex) {
            Logger.getLogger(VentaFrm.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al seleccionar cliente: " + ex.getMessage(), null));
        }
    }

    private boolean validarCampos() {
        if (registro.getIdCliente() == null) {
            enviarMensaje("Debe seleccionar un cliente.", FacesMessage.SEVERITY_WARN);
            return false;
        }
        if (registro.getEstado() == null || registro.getEstado().trim().isEmpty()) {
            enviarMensaje("Debe seleccionar un estado válido.", FacesMessage.SEVERITY_WARN);
            return false;
        }
        if (registro.getFecha() == null) {
            enviarMensaje("Debe seleccionar una fecha válida.", FacesMessage.SEVERITY_WARN);
            return false;
        }
        return true;
    }

    public List<Cliente> buscarClientesPorNombre(final String nombres) {
        try {
            if (nombres != null && !nombres.isBlank()) {
                return clienteDAO.buscarClientePorNombre(nombres, 0, 50);
            }
        } catch (Exception ex) {
            Logger.getLogger(ClienteDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return List.of();
    }


    public List<Venta> getListaVentas() {
        return listaVentas;
    }

    public void setListaVentas(List<Venta> listaVentas) {
        this.listaVentas = listaVentas;
    }

    public List<Cliente> getListaClientes() {
        return listaClientes;
    }

    public void setListaClientes(List<Cliente> listaClientes) {
        this.listaClientes = listaClientes;
    }

    public String getNombreBean() {
        return nombreBean;
    }

    public void setNombreBean(String nombreBean) {
        this.nombreBean = nombreBean;
    }

    public VentaDetalleFrm getVentaDetalleFrm() {
        if (this.registro != null && this.registro.getId() != null) {
            ventaDetalleFrm.setIdVenta(this.registro.getId());
        }
        return ventaDetalleFrm;
    }


}