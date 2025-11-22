package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf.conversores;

import jakarta.enterprise.context.Dependent;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.LibroMayorDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.LibroMayor;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

@FacesConverter(value = "libroMayorConverter", managed = true)
@Dependent
public class LibroMayorConverter implements Converter<LibroMayor>, Serializable {

    @Inject
    LibroMayorDAO libroMayorDAO;

    @Override
    public LibroMayor getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        if(s != null && !s.isBlank()){
            int inicioId = s.lastIndexOf('(');
            int finId = s.lastIndexOf(')');
            if(inicioId != -1 && finId != -1 && finId > inicioId){
                String idStr = s.substring(inicioId+1, finId);
                try{
                    Long id = Long.valueOf(idStr);
                    return libroMayorDAO.findById(id);
                }catch(Exception ex){
                    Logger.getLogger(LibroMayorConverter.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
        }
        return null;
    }

    /**
     * Extrae el ID de diferentes formatos
     */
    private Long extractIdFromValue(String value) {
        try {
            if (value.matches("^\\d+$")) {
                return Long.valueOf(value);
            }
            int inicioId = value.lastIndexOf('(');
            int finId = value.lastIndexOf(')');
            if (inicioId != -1 && finId != -1 && finId > inicioId) {
                String idStr = value.substring(inicioId + 1, finId).trim();
                if (idStr.matches("\\d+")) {
                    return Long.valueOf(idStr);
                }
            }
            String numericPart = value.replaceAll("[^0-9]", "");
            if (!numericPart.isEmpty()) {
                return Long.valueOf(numericPart);
            }

        } catch (NumberFormatException e) {
            Logger.getLogger(LibroDiarioConverter.class.getName())
                    .log(Level.WARNING, "CONVERSOR - Error de formato numerico en: {0}", value);
        }

        return null;
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, LibroMayor libroMayor) {
        if(libroMayor != null && libroMayor.getId() != null){
            // Usar la observación como texto principal, si está disponible
            String textoPrincipal = libroMayor.getObservacion() != null ?
                    libroMayor.getObservacion() : "Libro Mayor";

            // Si la observación es muy larga, truncarla
            if(textoPrincipal.length() > 30){
                textoPrincipal = textoPrincipal.substring(0, 30) + "...";
            }

            return textoPrincipal + " (" + libroMayor.getId().toString() + ")";
        }
        return null;
    }
}