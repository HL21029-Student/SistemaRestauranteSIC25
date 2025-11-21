package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf.conversores;

import jakarta.enterprise.context.Dependent;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.KardexDetalleDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.KardexDetalle;

import java.io.Serializable;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@FacesConverter(value="kardexDetalleConverter", managed = true)
@Dependent
public class KardexDetalleConverter implements Converter<KardexDetalle>, Serializable {
    @Inject
    KardexDetalleDAO kardexDetalleDAO;

    @Override
    public KardexDetalle getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        if(s != null && !s.isBlank()){
            int inicioId =s.lastIndexOf('(');
            int finId =s.lastIndexOf(')');
            if(inicioId != -1 && finId != -1 && finId > inicioId) {
                String idStr = s.substring(inicioId + 1, finId);
                try {
                    UUID id = UUID.fromString(idStr);
                    return kardexDetalleDAO.findbyId(id);
                } catch (Exception ex) {
                    Logger.getLogger(KardexDetalleConverter.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, KardexDetalle kardexDetalle) {
        if(kardexDetalle!=null && kardexDetalle.getId()!=null && kardexDetalle.getLote()!=null){
            return kardexDetalle.getLote()+" ("+kardexDetalle.getId().toString()+")";
        }
        return null;
    }
}
