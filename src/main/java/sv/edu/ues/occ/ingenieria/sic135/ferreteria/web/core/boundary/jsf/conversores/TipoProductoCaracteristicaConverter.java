package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf.conversores;

import jakarta.enterprise.inject.spi.CDI;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.FacesConverter;
import jakarta.faces.convert.Converter;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.TipoProductoCaracteristicaDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.TipoProductoCaracteristica;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

@FacesConverter(value = "tipoProductoCaracteristicaConverter", forClass = TipoProductoCaracteristica.class)
public class TipoProductoCaracteristicaConverter implements Converter<TipoProductoCaracteristica>, Serializable {
    @Override
    public TipoProductoCaracteristica getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        if(s!=null && !s.isBlank()){
            int inicioId = s.lastIndexOf('(');
            int finId = s.lastIndexOf(')');
            if(inicioId != -1 && finId != -1 && finId > inicioId){
                String idStr = s.substring(inicioId+1, finId);
                try{
                    Long id = Long.parseLong(idStr);
                    TipoProductoCaracteristicaDAO dao = CDI.current().select(TipoProductoCaracteristicaDAO.class).get();
                    return dao.findById(id);
                }catch(Exception ex){
                    Logger.getLogger(TipoProductoCaracteristicaConverter.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
        }
        return null;
    }
    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, TipoProductoCaracteristica tipoProductoCaracteristica) {
        if(tipoProductoCaracteristica!=null && tipoProductoCaracteristica.getId()!=null && tipoProductoCaracteristica.getIdCaracteristica().getNombre()!=null){
            return tipoProductoCaracteristica.getIdCaracteristica().getNombre()+" ("+tipoProductoCaracteristica.getId().toString()+")";
        }
        return null;
    }
}
