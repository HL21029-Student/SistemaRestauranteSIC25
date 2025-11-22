package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf.conversores;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf.LibroMayorFrm;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.LibroDiario;

import java.util.logging.Level;
import java.util.logging.Logger;

@FacesConverter(value = "libroDiarioConverter")
public class LibroDiarioConverter implements Converter<LibroDiario> {

    @Override
    public LibroDiario getAsObject(FacesContext context, UIComponent component, String value) {
        Logger.getLogger(LibroDiarioConverter.class.getName())
                .log(Level.INFO, "CONVERSOR - Convirtiendo string a objeto: {0}", value);

        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            // Obtener el managed bean desde el contexto
            LibroMayorFrm libroMayorFrm = context.getApplication()
                    .evaluateExpressionGet(context, "#{libroMayorFrm}", LibroMayorFrm.class);

            if (libroMayorFrm == null) {
                Logger.getLogger(LibroDiarioConverter.class.getName())
                        .log(Level.SEVERE, "CONVERSOR - No se pudo obtener el managed bean libroMayorFrm");
                return null;
            }

            // Buscar en la lista disponible
            return libroMayorFrm.getLibrosDiariosDisponibles().stream()
                    .filter(libro -> value.equals(libro.getId().toString()) ||
                            value.equals(libro.getNombre() + " (" + libro.getId().toString() + ")"))
                    .findFirst()
                    .orElse(null);

        } catch (Exception ex) {
            Logger.getLogger(LibroDiarioConverter.class.getName())
                    .log(Level.SEVERE, "CONVERSOR - Error al convertir: " + value, ex);
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, LibroDiario value) {
        if (value == null) {
            return "";
        }

        if (value.getId() != null && value.getNombre() != null) {
            return value.getNombre() + " (" + value.getId() + ")";
        }

        return value.getId() != null ? value.getId().toString() : "";
    }
}