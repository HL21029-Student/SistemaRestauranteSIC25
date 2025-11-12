package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.InventarioDAOInterface;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.InventarioDefaultDataAccess;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.TipoAlmacenDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.TipoAlmacen;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ViewScoped
public class TipoAlmacenFrm extends DefaultFrm<TipoAlmacen> implements Serializable {

    @Inject
    FacesContext facesContext;

    @Inject
    TipoAlmacenDAO tipoAlmacenDAO;

    @Override
    public FacesContext getFacesContext() {
        return facesContext;
    }

    @Override
    protected InventarioDAOInterface<TipoAlmacen, Object> getDao() {
        return tipoAlmacenDAO;
    }

    @Override
    protected String getIdAsText(TipoAlmacen r) {
        if (r != null && r.getIdTipoAlmacen() != null) {
            return r.getIdTipoAlmacen().toString();
        }
        return null;
    }

    @Override
    protected TipoAlmacen getIdByText(String id) {
        if (id != null && this.modelo != null ){
            try {
                Integer buscado = Integer.valueOf(id);
                return this.modelo.getWrappedData().stream()
                        .filter(r->r.getIdTipoAlmacen().equals(buscado))
                        .findFirst().orElse(null);
            } catch (NumberFormatException e) {
                Logger.getLogger(TipoAlmacen.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        return null;
    }

    List<TipoAlmacen> listaTipoAlmacen;

    public TipoAlmacenFrm() {}

    @PostConstruct
    @Override
    public void inicializar() {
        super.inicializar();
        listaTipoAlmacen = tipoAlmacenDAO.findRange(0,Integer.MAX_VALUE);
    }

    @Override
    protected TipoAlmacen nuevoRegistro() {
        TipoAlmacen t = new TipoAlmacen();
        return t;
    }

    @Override
    public InventarioDefaultDataAccess getDataAccess() {
        return tipoAlmacenDAO;
    }

    @Override
    protected TipoAlmacen buscarRegistroPorId(Object id) {
        if (id != null && id instanceof Integer buscado && this.modelo != null ) {
            return this.modelo.getWrappedData().stream().filter(r -> r.getIdTipoAlmacen().equals(buscado)).findFirst().orElse(null);
        }
        return null;
    }

    protected String nombreBean="page.tipoAlmacen";

    public String getNombreBean() {
        return nombreBean;
    }

    public void setNombreBean(String nombreBean) {
        this.nombreBean = nombreBean;
    }

    public List<TipoAlmacen> getListaTipoAlmacen() {
        return listaTipoAlmacen;
    }
    public void setListaTipoAlmacen(List<TipoAlmacen> listaTipoAlmacen) {
        this.listaTipoAlmacen = listaTipoAlmacen;
    }

}
