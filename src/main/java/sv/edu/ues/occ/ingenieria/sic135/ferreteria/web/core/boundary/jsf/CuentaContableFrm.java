package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.CuentaContableCrudDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.InventarioDefaultDataAccess;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.CuentaContable;

@Named("cuentaContableFrm")
@RequestScoped
public class CuentaContableFrm extends DefaultFrm<CuentaContable> {

    @Inject
    private CuentaContableCrudDAO cuentaContableCrudDAO;

    @Inject
    private FacesContext facesContext;

    public CuentaContableFrm() {
        this.nombreBean = "cuentaContableFrm";
    }

    @Override
    protected FacesContext getFacesContext() {
        return facesContext;
    }

    @Override
    protected InventarioDefaultDataAccess<CuentaContable, Object> getDao() {
        return cuentaContableCrudDAO;
    }

    @Override
    protected String getIdAsText(CuentaContable r) {
        return r != null && r.getId() != null ? r.getId().toString() : null;
    }

    @Override
    protected CuentaContable getIdByText(String id) {
        if (id == null) {
            return null;
        }
        return cuentaContableCrudDAO.findById(Long.valueOf(id));
    }

    @Override
    protected CuentaContable nuevoRegistro() {
        return new CuentaContable();
    }

    @Override
    public InventarioDefaultDataAccess<CuentaContable, Object> getDataAccess() {
        return cuentaContableCrudDAO;
    }

    @Override
    protected CuentaContable buscarRegistroPorId(Object id) {
        if (id == null) {
            return null;
        }
        return cuentaContableCrudDAO.findById(id);
    }
}

