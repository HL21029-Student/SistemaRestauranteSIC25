package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf.conversores;

import jakarta.enterprise.context.Dependent;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.CuentaContableDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.CuentaContable;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

@FacesConverter(value="cuentaContableConverter", managed = true)
@Dependent
public class CuentaContableConverter implements Converter<CuentaContable>, Serializable {
    @Inject
    CuentaContableDAO cuentaContableDAO;
    @Override
    public CuentaContable getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        if(s != null && !s.isBlank()){
            int inicioId =s.lastIndexOf('(');
            int finId =s.lastIndexOf(')');
            if(inicioId != -1 && finId != -1 && finId > inicioId){
                String idStr = s.substring(inicioId+1, finId);
                try{
                    Long id = Long.valueOf(idStr);
                    //refactorizar el metdo findbyId a findOfId para estandarizar nombres
                    return cuentaContableDAO.findbyId(id);
                }catch(Exception ex){
                    Logger.getLogger(CuentaContableConverter.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }
            }

        }
        return null;
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, CuentaContable cuentaContable) {
        if(cuentaContable != null && cuentaContable.getId()!=null && cuentaContable.getNombre()!=null){
            return cuentaContable.getNombre()+" ("+cuentaContable.getId().toString()+")";
        }
        return null;
    }

}
