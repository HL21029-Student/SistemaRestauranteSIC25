package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.FacturaDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.InventarioDefaultDataAccess;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.Factura;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named("facturaFrm")
@ViewScoped
public class FacturaFrm extends DefaultFrm<Factura> {

    private static final Logger LOGGER = Logger.getLogger(FacturaFrm.class.getName());

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
        try {
            UUID uuid = UUID.fromString(id);
            return facturaDAO.findById(uuid);
        } catch (IllegalArgumentException ex) {
            LOGGER.log(Level.WARNING, "ID de factura no válido: " + id, ex);
            return null;
        }
    }

    @Override
    protected Factura nuevoRegistro() {
        Factura f = new Factura();
        f.setFechaFactura(java.time.OffsetDateTime.now());
        f.setIva(Boolean.FALSE);
        return f;
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
        try {
            UUID uuid = UUID.fromString(id.toString());
            return facturaDAO.findById(uuid);
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "ID inválido para Factura: " + id, ex);
            return null;
        }
    }
}

