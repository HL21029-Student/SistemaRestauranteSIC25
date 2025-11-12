package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.InventarioDAOInterface;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.InventarioDefaultDataAccess;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.TipoUnidadMedidaDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.TipoUnidadMedida;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ViewScoped
public class TipoUnidadMedidaFrm extends DefaultFrm<TipoUnidadMedida> implements Serializable {

    @Inject
    private FacesContext facesContext;

    @Inject
    private TipoUnidadMedidaDAO tipoUnidadMedidaDAO;

    @Inject
    private UnidadMedidaFrm unidadMedidaFrm;


    private List<TipoUnidadMedida> tipos;

    public TipoUnidadMedidaFrm() {
    }


    @Override
    protected FacesContext getFacesContext() {
        return facesContext;
    }

    @Override
    protected InventarioDAOInterface<TipoUnidadMedida, Object> getDao() {
        return tipoUnidadMedidaDAO;
    }

    @Override
    protected String getIdAsText(TipoUnidadMedida r) {
        if (r != null && r.getId() != null) {
            return r.getId().toString();
        }
        return null;
    }

    @Override
    protected TipoUnidadMedida getIdByText(String id) {
        if (id != null) {
            try {
                Integer buscado = Integer.parseInt(id);
                return this.modelo.getWrappedData().stream()
                        .filter(r -> r.getId().equals(buscado))
                        .findFirst()
                        .orElse(null);
            } catch (NumberFormatException e) {
                Logger.getLogger(TipoUnidadMedidaFrm.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        return null;
    }

    @PostConstruct
    @Override
    public void inicializar() {
        super.inicializar();
        tipos = tipoUnidadMedidaDAO.findRange(0, Integer.MAX_VALUE);
    }

    @Override
    protected TipoUnidadMedida nuevoRegistro() {
        TipoUnidadMedida t = new TipoUnidadMedida();
        t.setNombre("");
        t.setUnidadBase("");
        t.setActivo(true);
        t.setComentarios("");
        return t;
    }

    @Override
    public InventarioDefaultDataAccess getDataAccess() {
        return tipoUnidadMedidaDAO;
    }

    @Override
    protected TipoUnidadMedida buscarRegistroPorId(Object id) {
        if (id instanceof Integer buscado && this.modelo != null) {
            return this.modelo.getWrappedData().stream()
                    .filter(r -> r.getId().equals(buscado))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    private String nombreBean = "page.tipounidadmedida";

    public String getNombreBean() {
        return nombreBean;
    }

    public UnidadMedidaFrm getUnidadMedidaFrm() {
        if (this.registro != null && this.registro.getId() != null) {
            unidadMedidaFrm.setIdTipoUnidadMedida(this.registro.getId());
        }
        return unidadMedidaFrm;
    }

    public void setNombreBean(String nombreBean) {
        this.nombreBean = nombreBean;
    }

    public List<TipoUnidadMedida> getListaTipos() {
        return tipos;
    }

    public void setListaTipos(List<TipoUnidadMedida> listaTipos) {
        this.tipos = listaTipos;
    }
}
