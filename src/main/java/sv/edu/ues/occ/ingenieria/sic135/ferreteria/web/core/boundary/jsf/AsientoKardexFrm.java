package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf;

import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.AsientoKardexDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.InventarioDAOInterface;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.InventarioDefaultDataAccess;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.AsientoKardex;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.KardexDetalle;

import java.io.Serializable;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ViewScoped
public class AsientoKardexFrm extends DefaultFrm<AsientoKardex> implements Serializable {
    @Inject
    FacesContext facesContext;
    @Inject
    AsientoKardexDAO asientoKardexDAO;
    
    
    @Override
    protected FacesContext getFacesContext() {
        return facesContext;
    }

    @Override
    protected InventarioDAOInterface<AsientoKardex, Object> getDao() {
        return asientoKardexDAO;
    }



    @Override
    public void inicializarListas() {
        // No hay listas adicionales para inicializar en este formulario
    }

    @Override
    protected String getIdAsText(AsientoKardex r) {
        if(r != null && r.getId() != null){
            return r.getId().toString();
        }
        return null;
    }

    @Override
    protected AsientoKardex getIdByText(String id) {
        if(id!=null){
            try{
                UUID buscado = UUID.fromString(id);
                return this.modelo.getWrappedData().stream().filter(kardexDetalle -> kardexDetalle.getId().equals(buscado)).findFirst().orElse(null);
            }catch (Exception ex){
                Logger.getLogger(AsientoKardexFrm.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        return null;
    }

    @Override
    protected AsientoKardex nuevoRegistro() {
        AsientoKardex ak = new AsientoKardex();

        return ak;
    }

    @Override
    public InventarioDefaultDataAccess getDataAccess() {
        return asientoKardexDAO;
    }

    @Override
    protected AsientoKardex buscarRegistroPorId(Object id) {
        if(id!=null && id instanceof UUID buscado && this.modelo != null && this.modelo.getWrappedData()!= null && !this.modelo.getWrappedData().isEmpty()){
            try{
                return this.modelo.getWrappedData().stream().filter(r->r.getId().equals(buscado)).findFirst().orElse(null);
            }catch(Exception e){
                Logger.getLogger(AsientoKardexFrm.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            }
        }
        return null;
    }


}
