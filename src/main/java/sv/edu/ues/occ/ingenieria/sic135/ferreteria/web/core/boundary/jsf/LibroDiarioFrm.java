package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf;

import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.InventarioDAOInterface;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.InventarioDefaultDataAccess;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.LibroDiario;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.LibroDiarioDAO;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ViewScoped
public class LibroDiarioFrm extends DefaultFrm<LibroDiario> implements Serializable {
    @Inject
    FacesContext facesContext;
    @Inject
    LibroDiarioDAO libroDiarioDAO;


    @Override
    protected FacesContext getFacesContext() {
        return facesContext;
    }

    @Override
    protected InventarioDAOInterface<LibroDiario, Object> getDao() {
        return libroDiarioDAO;
    }

    @Override
    protected String getIdAsText(LibroDiario r) {
        if(r != null && r.getId() != null){
            return r.getId().toString();
        }
        return null;
    }

    @Override
    protected LibroDiario getIdByText(String id) {
        if(id!=null){
            try{
                Long buscado = Long.parseLong(id);
                return this.modelo.getWrappedData().stream().filter(r -> r.getId().equals(buscado)).findFirst().orElse(null);
            }catch(IllegalArgumentException e){
                Logger.getLogger(LibroDiarioFrm.class.getName()).log(Level.SEVERE,e.getMessage(),e);
            }
        }
        return null;
    }

    @Override
    protected LibroDiario nuevoRegistro() {
        return null;
    }

    @Override
    public InventarioDefaultDataAccess getDataAccess() {
        return null;
    }

    @Override
    protected LibroDiario buscarRegistroPorId(Object id) {
        return null;
    }
}
