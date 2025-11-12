package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ActionEvent;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.InventarioDAOInterface;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.InventarioDefaultDataAccess;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.ProveedorDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.Proveedor;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ViewScoped
public class ProveedorFrm extends DefaultFrm<Proveedor> implements Serializable {

    @Inject
    FacesContext facesContext;

    @Inject
    ProveedorDAO proveedorDAO;

    private List<Proveedor> proveedores;

    public ProveedorFrm() {
    }


    @Override
    protected FacesContext getFacesContext() {
        return facesContext;
    }

    @Override
    protected InventarioDAOInterface<Proveedor, Object> getDao() {
        return proveedorDAO;
    }

    @Override
    protected String getIdAsText(Proveedor r) {
        if (r != null && r.getId() != null) {
            return String.valueOf(r.getId());
        }
        return null;
    }

    @Override
    protected Proveedor getIdByText(String id) {
        if (id != null) {
            try {
                Integer buscado = Integer.valueOf(id);
                return this.modelo.getWrappedData()
                        .stream()
                        .filter(r -> r.getId().equals(buscado))
                        .findFirst()
                        .orElse(null);
            } catch (NumberFormatException e) {
                Logger.getLogger(ProveedorFrm.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        return null;
    }

    @PostConstruct
    @Override
    public void inicializar() {
        super.inicializar();
        proveedores = proveedorDAO.findRange(0, Integer.MAX_VALUE);
    }

    @Override
    protected Proveedor nuevoRegistro() {
        Proveedor p = new Proveedor();
        p.setActivo(true);
        p.setNombre("");
        p.setRazonSocial("");
        p.setNit("");
        p.setObservaciones("");
        return p;
    }

    @Override
    public InventarioDefaultDataAccess getDataAccess() {
        return proveedorDAO;
    }

    @Override
    protected Proveedor buscarRegistroPorId(Object id) {
        if (id instanceof Integer buscado && this.modelo != null) {
            return this.modelo.getWrappedData()
                    .stream()
                    .filter(r -> r.getId().equals(buscado))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    @Override
    public void btnGuardarHandler(ActionEvent actionEvent) {
        try {
            if (registro != null) {// Validar antes de guardar
                if (!validarCampos()) {
                    return;// Si no pasa la validación, no continúa
                }

                getDao().create(registro);
                this.enviarMensaje("Proveedor registrado correctamente", FacesMessage.SEVERITY_INFO);
                this.estado = ESTADO_CRUD.NADA;
                this.registro = null;
                inicializarRegistros();
                return;
            }
        } catch (Exception ex) {
            enviarMensaje("Error al crear el proveedor: " + ex.getMessage(), FacesMessage.SEVERITY_ERROR);
            return;
        }
        enviarMensaje("El registro a almacenar no puede ser nulo", FacesMessage.SEVERITY_WARN);
        this.estado = ESTADO_CRUD.NADA;
    }

    @Override
    public void btnModificarHandler(ActionEvent actionEvent) {
        if (this.registro == null) {
            this.enviarMensaje("No hay proveedor seleccionado", FacesMessage.SEVERITY_ERROR);
            return;
        }

        try {// Validar antes de modificar
            if (!validarCampos()) {
                return;// Si no pasa la validación, no continúa
            }

            this.getDao().update(this.registro);
            enviarMensaje("Proveedor modificado correctamente", FacesMessage.SEVERITY_INFO);
            this.inicializarRegistros();
            this.estado = ESTADO_CRUD.NADA;
            this.registro = null;
        } catch (Exception ex) {
            enviarMensaje("Error al modificar el proveedor: " + ex.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }

    /**
     *  Validar que el NIT tenga exactamente 14 dígitos
     */
    private boolean validarCampos() {
        if (registro.getNit() == null || registro.getNit().trim().isEmpty()) {
            enviarMensaje("El NIT es obligatorio.", FacesMessage.SEVERITY_WARN);
            return false;
        }

        if (registro.getNit().length() != 14) {
            enviarMensaje("El NIT debe tener exactamente 14 dígitos.", FacesMessage.SEVERITY_WARN);
            return false;
        }

        return true;
    }


    protected String nombreBean = "page.proveedor";

    public String getNombreBean() {
        return nombreBean;
    }

    public void setNombreBean(String nombreBean) {
        this.nombreBean = nombreBean;
    }

    public List<Proveedor> getListaProveedores() {
        return proveedores;
    }

    public void setListaProveedores(List<Proveedor> listaProveedores) {
        this.proveedores = listaProveedores;
    }
}