package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.ManualCuentaDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.ManualCuenta;

import java.io.Serializable;
import java.util.List;

@Named("manualCuentaCrudFrm")
@RequestScoped
public class ManualCuentaCrudFrm implements Serializable {

    @Inject
    private ManualCuentaDAO manualCuentaDAO;

    @Inject
    private FacesContext facesContext;

    private List<ManualCuenta> manuales;

    private ManualCuenta manualSeleccionado;

    @PostConstruct
    public void init() {
        manuales = manualCuentaDAO.findRange(0, Integer.MAX_VALUE);
    }

    public List<ManualCuenta> getManuales() {
        return manuales;
    }

    public ManualCuenta getManualSeleccionado() {
        return manualSeleccionado;
    }

    public void editarManual(ManualCuenta manual) {
        this.manualSeleccionado = manual;
    }

    public void guardarManual() {
        if (manualSeleccionado == null) {
            addMessage("No hay registro seleccionado para editar el manual", FacesMessage.SEVERITY_WARN);
            return;
        }
        try {
            if (manualSeleccionado.getId() == null) {
                manualCuentaDAO.create(manualSeleccionado);
                addMessage("Manual creado", FacesMessage.SEVERITY_INFO);
            } else {
                manualCuentaDAO.update(manualSeleccionado);
                addMessage("Manual actualizado", FacesMessage.SEVERITY_INFO);
            }
            // recargar lista para reflejar cambios
            manuales = manualCuentaDAO.findRange(0, Integer.MAX_VALUE);
        } catch (Exception e) {
            addMessage("Error al guardar el manual: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }

    private void addMessage(String msg, FacesMessage.Severity sev) {
        facesContext.addMessage(null, new FacesMessage(sev, msg, msg));
    }
}
