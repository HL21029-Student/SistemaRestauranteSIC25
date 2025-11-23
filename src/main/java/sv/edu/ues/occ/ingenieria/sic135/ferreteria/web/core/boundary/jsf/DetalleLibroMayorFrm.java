package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.Dependent;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.DetalleLibroDiarioDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.DetalleLibroMayorDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.InventarioDAOInterface;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.InventarioDefaultDataAccess;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.DetalleLibroMayor;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.LibroMayor;

import java.io.Serializable;
import java.util.logging.Logger;

@Dependent
@Named
public class DetalleLibroMayorFrm extends DefaultFrm<DetalleLibroMayor> implements Serializable {
    @Inject
    FacesContext facesContext;
    @Inject
    DetalleLibroMayorDAO detalleLibroMayorDAO;

    //para posibles usos del libro diario
    @Inject
    DetalleLibroDiarioDAO detalleLibroDiarioDAO;

    @Named("libroMayorFrm")
    @Inject
    private LibroMayorFrm libroMayorFrm;
    private LibroMayor libroMayor;


    @Override
    protected FacesContext getFacesContext() {
        return facesContext;
    }

    @Override
    protected InventarioDAOInterface<DetalleLibroMayor, Object> getDao() {
        return detalleLibroMayorDAO;
    }

    @Override
    protected String getIdAsText(DetalleLibroMayor r) {
        if (r != null && r.getId() != null) {
            return r.getId().toString();
        }
        return null;
    }

    @Override
    protected DetalleLibroMayor getIdByText(String id) {
        if(id!=null && !id.isBlank() && this.modelo.getWrappedData() != null && !this.modelo.getWrappedData().isEmpty()){
            try{
                Long buscado = Long.parseLong(id);
                return this.modelo.getWrappedData().stream().filter(r -> r.getId().equals(buscado)).findFirst().orElse(null);
            }catch(IllegalArgumentException e){
                Logger.getLogger(DetalleLibroMayorFrm.class.getName()).log(java.util.logging.Level.SEVERE,e.getMessage(),e);
            }
        }
        return null;
    }

    @PostConstruct
    @Override
    public void inicializar() {
        super.inicializar();


    }

    @Override
    protected DetalleLibroMayor nuevoRegistro() {
        DetalleLibroMayor dlm = new DetalleLibroMayor();

        return dlm;
    }

    @Override
    public InventarioDefaultDataAccess getDataAccess() {
        return detalleLibroMayorDAO;
    }

    @Override
    protected DetalleLibroMayor buscarRegistroPorId(Object id) {
        if(id instanceof Long buscado && this.modelo != null){
            return this.modelo.getWrappedData().stream().filter(r -> r.getId().equals(buscado)).findFirst().orElse(null);
        }
        return null;
    }






}
