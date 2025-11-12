package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf;


import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.model.LazyDataModel;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.CaracteristicaDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.InventarioDAOInterface;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.InventarioDefaultDataAccess;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.TipoUnidadMedidaDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.Caracteristica;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.TipoUnidadMedida;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ViewScoped
public class CaracteristicaFrm extends DefaultFrm<Caracteristica> implements Serializable {

    @Inject
    FacesContext facesContext;

    @Inject
    CaracteristicaDAO caracteristicaDAO;

    @Inject
    TipoUnidadMedidaDAO tipoUnidadMedidaDAO;

    List<TipoUnidadMedida> tipoUnidadMedidasList;


    public CaracteristicaFrm() {
    }

    @Override
    protected FacesContext getFacesContext() {
        return facesContext;
    }

    @Override
    protected InventarioDAOInterface<Caracteristica, Object> getDao() {
        return caracteristicaDAO;
    }

    @Override
    protected String getIdAsText(Caracteristica r) {
        if (r != null && r.getId() != null) {
            return r.getId().toString();
        }
        return null;
    }

    @Override
    protected Caracteristica getIdByText(String id) {
        if (id != null) {
            try {
                Integer buscado = Integer.valueOf(id);
                return this.modelo.getWrappedData().stream().filter(r -> r.getId().equals(buscado)).findFirst().orElse(null);
            } catch (IllegalArgumentException e) {
                Logger.getLogger(CaracteristicaFrm.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            }
        }
        return null;
    }


    @Override
    public void inicializarListas() {
        try {
            tipoUnidadMedidasList = tipoUnidadMedidaDAO.findRange(0, Integer.MAX_VALUE);
        } catch (Exception ex) {
            Logger.getLogger(CaracteristicaFrm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            tipoUnidadMedidasList = List.of();
        }
    }

    //probar la creacion del id
    @Override
    protected Caracteristica nuevoRegistro() {
        Caracteristica caracteristica = new Caracteristica();
        caracteristica.setNombre("");
        caracteristica.setActivo(true);
        caracteristica.setDescripcion("");
        return caracteristica;
    }

    @Override
    public InventarioDefaultDataAccess getDataAccess() {
        return caracteristicaDAO;
    }

    @Override
    protected Caracteristica buscarRegistroPorId(Object id) {
        if (id instanceof Integer buscado && this.modelo != null) {
            return this.modelo.getWrappedData().stream().filter(r -> r.getId().equals(buscado)).findFirst().orElse(null);
        }
        return null;
    }

    protected String nombreBean = "page.caracteristica";

    public String getNombreBean() {
        return this.nombreBean;
    }

    public void setNombreBean(String nombreBean) {
        this.nombreBean = nombreBean;
    }

    @Override
    public LazyDataModel<Caracteristica> getModelo() {
        return super.getModelo();
    }

    public List<TipoUnidadMedida> getTipoUnidadMedidasList() {
        return tipoUnidadMedidasList;
    }

    public Integer getIdTipoUnidadMedidaSeleccionado(){
        if(this.registro != null && this.registro.getIdTipoUnidadMedida() != null){
            return this.registro.getIdTipoUnidadMedida().getId();
        }
        return null;
    }

    public void setIdTipoUnidadMedidaSeleccionado(Integer idTipoUnidadMedida){
        if(idTipoUnidadMedida != null && this.registro != null && this.tipoUnidadMedidasList != null && !this.tipoUnidadMedidasList.isEmpty()) {
            this.registro.setIdTipoUnidadMedida(this.tipoUnidadMedidasList.stream().filter(r->r.getId().equals(idTipoUnidadMedida)).findFirst().orElse(null));
        }
    }

}