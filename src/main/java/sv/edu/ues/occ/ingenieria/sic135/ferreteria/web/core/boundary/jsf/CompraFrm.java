package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ActionEvent;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.CompraDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.ProveedorDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.InventarioDAOInterface;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.InventarioDefaultDataAccess;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.Compra;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.Proveedor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ViewScoped
public class CompraFrm extends DefaultFrm<Compra> implements Serializable {

    @Inject
    FacesContext facesContext;

    @Inject
    CompraDAO compraDAO;

    @Inject
    ProveedorDAO proveedorDAO;

    @Inject
    protected CompraDetalleFrm compraDetalleFrm;

    private List<Compra> listaCompras;
    private List<Proveedor> listaProveedores;

    private String nombreBean = "page.compra";

    public CompraFrm() {}

    @Override
    protected FacesContext getFacesContext() {
        return facesContext;
    }

    @Override
    protected InventarioDAOInterface<Compra, Object> getDao() {
        return compraDAO;
    }

    @Override
    protected String getIdAsText(Compra r) {
        if (r != null && r.getId() != null) {
            return r.getId().toString();
        }
        return null;
    }

    @Override
    protected Compra getIdByText(String id) {
        if (id != null) {
            try {
                Long buscado = Long.parseLong(id);
                return this.modelo.getWrappedData().stream()
                        .filter(r -> r.getId().equals(buscado))
                        .findFirst().orElse(null);
            } catch (IllegalArgumentException e) {
                Logger.getLogger(CompraFrm.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        return null;
    }

    @PostConstruct
    @Override
    public void inicializar() {
        super.inicializar();
        try {
            listaCompras = compraDAO.findRange(0, Integer.MAX_VALUE);
            int size = listaCompras == null ? 0 : listaCompras.size();
            Logger.getLogger(CompraFrm.class.getName()).log(Level.INFO, "Compras cargadas desde DAO: {0}", size);

            if (listaCompras == null) listaCompras = List.of();

            // Forzar inicialización del modelo si DefaultFrm maneja lazy model
            try {
                // inicializarRegistros() existe en DefaultFrm según uso en otras clases
                this.inicializarRegistros();
                Logger.getLogger(CompraFrm.class.getName()).log(Level.INFO, "Modelo inicializado/actualizado");
            } catch (Exception e) {
                Logger.getLogger(CompraFrm.class.getName()).log(Level.WARNING, "No se pudo inicializarRegistros(): {0}", e.getMessage());
            }

        } catch (Exception ex) {
            Logger.getLogger(CompraFrm.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al cargar compras", ex.getMessage()));
            listaCompras = List.of();
        }
    }

    @Override
    protected Compra nuevoRegistro() {
        Compra c = new Compra();
        c.setEstado("ACTIVA");
        c.setIdProveedor(new Proveedor());
        c.setObservaciones("");
        c.setFecha(new Date());
        return c;
    }

    @Override
    public InventarioDefaultDataAccess getDataAccess() {
        return compraDAO;
    }

    @Override
    protected Compra buscarRegistroPorId(Object id) {
        if (id instanceof Long buscado && this.modelo != null) {
            return this.modelo.getWrappedData().stream()
                    .filter(r -> r.getId().equals(buscado))
                    .findFirst().orElse(null);
        }
        return null;
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

    private boolean validarCampos() {
        if (registro.getIdProveedor() == null) {
            enviarMensaje("Debe seleccionar un proveedor.", FacesMessage.SEVERITY_WARN);
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


    public List<Compra> getListaCompras() {
        return listaCompras;
    }

    public void setListaCompras(List<Compra> listaCompras) {
        this.listaCompras = listaCompras;
    }

    public List<Proveedor> getListaProveedores() {
        return listaProveedores;
    }

    public void setListaProveedores(List<Proveedor> listaProveedores) {
        this.listaProveedores = listaProveedores;
    }

    public String getNombreBean() {
        return nombreBean;
    }

    public void setNombreBean(String nombreBean) {
        this.nombreBean = nombreBean;
    }

//    public CompraDetalleFrm getCompraDetalleFrm() {
//        if (this.registro != null && this.registro.getId() != null) {
//            compraDetalleFrm.setIdCompra(this.registro.getId());
//        }
//        return compraDetalleFrm;
//    }

}
