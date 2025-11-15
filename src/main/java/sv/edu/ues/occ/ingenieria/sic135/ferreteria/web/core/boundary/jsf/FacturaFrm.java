package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.FacturaDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.InventarioDefaultDataAccess;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.Factura;

import java.util.UUID;

@Named("facturaFrm")
@RequestScoped
public class FacturaFrm extends DefaultFrm<Factura> {

    @Inject
    private FacturaDAO facturaDAO;

    @Inject
    private FacesContext facesContext;

    public FacturaFrm() {
        this.nombreBean = "facturaFrm";
    }

    @Override
    protected FacesContext getFacesContext() {
        return facesContext;
    }

    @Override
    protected InventarioDefaultDataAccess<Factura, Object> getDao() {
        return facturaDAO;
    }

    @Override
    protected String getIdAsText(Factura r) {
        return r != null && r.getId() != null ? r.getId().toString() : null;
    }

    @Override
    protected Factura getIdByText(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }
        return facturaDAO.findById(UUID.fromString(id));
    }

    @Override
    protected Factura nuevoRegistro() {
        return new Factura();
    }

    @Override
    public InventarioDefaultDataAccess<Factura, Object> getDataAccess() {
        return facturaDAO;
    }

    @Override
    protected Factura buscarRegistroPorId(Object id) {
        if (id == null) {
            return null;
        }
        return facturaDAO.findById(id);
    }
}

