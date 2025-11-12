package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf;

import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.model.LazyDataModel;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.AlmacenDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.InventarioDAOInterface;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.InventarioDefaultDataAccess;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.TipoAlmacenDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.Almacen;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.TipoAlmacen;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ViewScoped
public class AlmacenFrm extends DefaultFrm<Almacen> implements Serializable {
    @Inject
    FacesContext facesContext;

    @Inject
    AlmacenDAO almacenDAO;

    @Inject
    TipoAlmacenDAO tipoAlmacenDAO;

    List<TipoAlmacen> tipoAlmacenList;

    @Override
    public void inicializarListas(){
        try{
            tipoAlmacenList = tipoAlmacenDAO.findRange(0, Integer.MAX_VALUE);
        }catch(Exception e){
            Logger.getLogger(AlmacenFrm.class.getName()).log(java.util.logging.Level.SEVERE, null, e);
            tipoAlmacenList=List.of();
        }
    }

    @Override
    protected FacesContext getFacesContext() {
        return facesContext;
    }

    @Override
    protected InventarioDAOInterface<Almacen, Object> getDao() {
        return almacenDAO;
    }

    @Override
    protected String getIdAsText(Almacen r) {
        if(r != null && r.getId() != null){
            return r.getId().toString();
        }
        return null;
    }

    @Override
    protected Almacen getIdByText(String id) {
        if(id!= null &&  this.modelo!= null && this.modelo.getWrappedData()!= null && !this.modelo.getWrappedData().isEmpty()){
            try{
                Integer buscado = Integer.valueOf(id);
                return this.modelo.getWrappedData().stream().filter(r->r.getId().equals(buscado)).findFirst().orElse(null);
            }catch(Exception e){
                Logger.getLogger(AlmacenFrm.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        return null;
    }

    @Override
    protected Almacen nuevoRegistro() {
        Almacen almacen = new Almacen();
        almacen.setActivo(true);
        if(this.tipoAlmacenList!=null && !this.tipoAlmacenList.isEmpty()){
            almacen.setIdTipoAlmacen(this.tipoAlmacenList.getFirst());
        }
        return almacen;
    }



    @Override
    public InventarioDefaultDataAccess getDataAccess() {
        return almacenDAO;
    }

    @Override
    protected Almacen buscarRegistroPorId(Object id) {
        if(id!= null && id instanceof Integer buscado && this.modelo!= null && this.modelo.getWrappedData()!= null && !this.modelo.getWrappedData().isEmpty()){
            return this.modelo.getWrappedData().stream().filter(r->r.getId().equals(buscado)).findFirst().orElse(null);
        }
        return null;
    }

    @Override
    public LazyDataModel<Almacen> getModelo() {
        return super.getModelo();
    }

    public List<TipoAlmacen> getTipoAlmacenList() {
        return tipoAlmacenList;
    }

    public Integer getIdTipoAlmacenSeleccionado(){
        if(this.registro != null && this.registro.getIdTipoAlmacen() != null){
            return this.registro.getIdTipoAlmacen().getId();
        }
        return null;
    }

    public void setIdTipoAlmacenSeleccionado(final Integer idTipoAlmacen){
        if(this.registro !=null && this.tipoAlmacenList!=null && !this.tipoAlmacenList.isEmpty()){
            this.registro.setIdTipoAlmacen(this.tipoAlmacenList.stream()
                    .filter(r->r.getId().equals(idTipoAlmacen))
                    .findFirst().orElse(null));
        }
    }

    protected String nombreBean="page.almacen";

    @Override
    public String getNombreBean(){
        return nombreBean;
    }


}
