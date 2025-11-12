package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf;

import jakarta.enterprise.context.Dependent;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.InventarioDAOInterface;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.InventarioDefaultDataAccess;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.UnidadMedidaDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.TipoUnidadMedida;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.UnidadMedida;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@Dependent
public class UnidadMedidaFrm extends DefaultFrm<UnidadMedida> implements Serializable {

    protected Integer idTipoUnidadMedida;

    @Inject
    FacesContext facesContext;

    @Inject
    UnidadMedidaDAO unidadMedidaDAO;

    @Override
    public List<UnidadMedida> cargarDatos(int first, int max) {
        try {
            if (first >= 0 && max > 0 && this.idTipoUnidadMedida != null) {
                return unidadMedidaDAO.findByIdTipoUnidadMedida(this.idTipoUnidadMedida, first, max);
            }
        } catch (Exception ex) {
            Logger.getLogger(UnidadMedidaFrm.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return List.of();
    }

    @Override
    public int contarDatos() {
        try {
            if (this.idTipoUnidadMedida != null) {
                return this.unidadMedidaDAO.countByIdTipoUnidadMedida(this.idTipoUnidadMedida).intValue();
            }

        } catch (Exception ex) {
            Logger.getLogger(UnidadMedidaFrm.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return 0;
    }

    @Override
    protected FacesContext getFacesContext() {
        return facesContext;
    }

    @Override
    protected InventarioDAOInterface<UnidadMedida, Object> getDao() {
        return unidadMedidaDAO;
    }

    @Override
    protected String getIdAsText(UnidadMedida r) {
        if (r != null && r.getId() != null) {
            return r.getId().toString();
        }
        return null;
    }

    @Override
    protected UnidadMedida getIdByText(String id) {
        if (id != null && !id.isBlank() && this.modelo != null && this.modelo.getWrappedData() != null && !this.modelo.getWrappedData().isEmpty()) {
            try {
                Integer idInteger = Integer.parseInt(id);
                return this.modelo.getWrappedData().stream()
                        .filter(r -> r.getId().equals(idInteger))
                        .findFirst()
                        .orElse(null);
            } catch (NumberFormatException e) {
                Logger.getLogger(UnidadMedidaFrm.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            }
        }
        return null;
    }

    @Override
    protected UnidadMedida nuevoRegistro() {
        UnidadMedida nueva = new UnidadMedida();
        nueva.setActivo(true);
        nueva.setComentarios("");
        nueva.setEquivalencia(null);
        nueva.setExpresionRegular("");

        if (idTipoUnidadMedida != null) {
            TipoUnidadMedida tipo = new TipoUnidadMedida();
            tipo.setId(idTipoUnidadMedida);
            nueva.setIdTipoUnidadMedida(tipo);
        }

        return nueva;
    }

    @Override
    public InventarioDefaultDataAccess getDataAccess() {
        return null;
    }

    @Override
    protected UnidadMedida buscarRegistroPorId(Object id) {
        return null;
    }

    public Integer getIdTipoUnidadMedida() {
        return idTipoUnidadMedida;
    }

    public void setIdTipoUnidadMedida(Integer idTipoUnidadMedida) {
        this.idTipoUnidadMedida = idTipoUnidadMedida;
    }

    protected String nombreBean = "page.unidadMedida";

    @Override
    public String getNombreBean() {
        return nombreBean;
    }
}
