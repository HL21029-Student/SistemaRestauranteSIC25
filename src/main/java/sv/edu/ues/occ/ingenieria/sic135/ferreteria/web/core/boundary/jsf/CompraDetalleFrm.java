package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf;

import jakarta.enterprise.context.Dependent;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.InventarioDAOInterface;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.InventarioDefaultDataAccess;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.CompraDetalleDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.CompraDetalle;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Dependent
@Named
public class CompraDetalleFrm extends DefaultFrm<CompraDetalle> implements Serializable {

    @Inject
    FacesContext facesContext;

    @Inject
    CompraDetalleDAO compraDetalleDAO;

    protected UUID idCompra;

    @Override
    protected FacesContext getFacesContext() {
        return facesContext;
    }

    @Override
    protected InventarioDAOInterface<CompraDetalle, Object> getDao() {
        return compraDetalleDAO;
    }

    @Override
    protected String getIdAsText(CompraDetalle r) {
        if (r != null && r.getId() != null) {
            return r.getId().toString();
        }
        return null;
    }

    @Override
    protected CompraDetalle getIdByText(String id) {
        if (id != null && !id.isBlank() && this.modelo.getWrappedData() != null && !this.modelo.getWrappedData().isEmpty()) {
            try {
                UUID buscado = UUID.fromString(id);
                return this.modelo.getWrappedData()
                        .stream()
                        .filter(r -> r.getId().equals(buscado))
                        .findFirst()
                        .orElse(null);
            } catch (IllegalArgumentException e) {
                Logger.getLogger(CompraDetalleFrm.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            }
        }
        return null;
    }

    @Override
    protected CompraDetalle nuevoRegistro() {
        CompraDetalle det = new CompraDetalle();
        det.setId(UUID.randomUUID());
        det.setCantidad(BigDecimal.valueOf(0));
        det.setPrecio(BigDecimal.valueOf(0));
        det.setEstado("ACTIVA");
        det.setObservaciones("");
        return det;
    }

    @Override
    public InventarioDefaultDataAccess getDataAccess() {
        return compraDetalleDAO;
    }

    @Override
    protected CompraDetalle buscarRegistroPorId(Object id) {
        if (id instanceof UUID buscado && this.modelo != null) {
            return this.modelo.getWrappedData()
                    .stream()
                    .filter(r -> r.getId().equals(buscado))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    public UUID getIdCompra() {
        return idCompra;
    }

    protected void setIdCompra(UUID idCompra) {
        this.idCompra = idCompra;
    }

    protected String nombreBean = "page.compraDetalle";

    @Override
    public String getNombreBean() {
        return this.nombreBean;
    }
}
