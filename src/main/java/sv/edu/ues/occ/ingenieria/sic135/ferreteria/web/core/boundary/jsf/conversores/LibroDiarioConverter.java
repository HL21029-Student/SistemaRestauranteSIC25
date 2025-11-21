package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf.conversores;

import jakarta.enterprise.context.Dependent;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.LibroDiarioDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.LibroDiario;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

@FacesConverter(value = "libroDiarioConverter", managed = true)
@Dependent
public class LibroDiarioConverter implements Converter<LibroDiario>, Serializable {
    @Inject
    LibroDiarioDAO libroDiarioDAO;

    @Override
    public LibroDiario getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        if(s != null && !s.isBlank()){
            int inicioId =s.lastIndexOf('(');
            int finId =s.lastIndexOf(')');
            if(inicioId != -1 && finId != -1 && finId > inicioId){
                String idStr = s.substring(inicioId+1, finId);
                try{
                    Long id = Long.valueOf(idStr);
                    return libroDiarioDAO.findById(id);
                }catch(Exception ex){
                    Logger.getLogger(LibroDiarioConverter.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, LibroDiario libroDiario) {
        if(libroDiario != null && libroDiario.getId()!=null && libroDiario.getNombre()!=null){
            return libroDiario.getNombre()+" ("+libroDiario.getId().toString()+")";
        }
        return null;
    }
}
