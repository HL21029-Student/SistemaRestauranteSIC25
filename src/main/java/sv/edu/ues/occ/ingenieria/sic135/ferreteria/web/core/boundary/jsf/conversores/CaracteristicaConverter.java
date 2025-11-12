package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf.conversores;

import jakarta.enterprise.context.Dependent;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.CaracteristicaDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.Caracteristica;


import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

@FacesConverter(value="caracteristicaConverter", managed = true)
@Dependent
public class CaracteristicaConverter implements  Converter<Caracteristica>, Serializable {
    @Inject
    CaracteristicaDAO caracteristicaDAO;
    @Override
    public Caracteristica getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        if(s != null && !s.isBlank()){
            int inicioId =s.lastIndexOf('(');
            int finId =s.lastIndexOf(')');
            if(inicioId != -1 && finId != -1 && finId > inicioId){
                String idStr = s.substring(inicioId+1, finId);
                try{
                    Integer id = Math.toIntExact(Long.valueOf(idStr));
                    return caracteristicaDAO.findById(id);
                }catch(Exception ex){
                    Logger.getLogger(CaracteristicaConverter.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
        }
        return null;
    }
    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Caracteristica caracteristica) {
        if(caracteristica != null && caracteristica.getId()!=null && caracteristica.getNombre()!=null){
            return caracteristica.getNombre()+" ("+caracteristica.getId().toString()+")";
        }
        return null;
    }
}
