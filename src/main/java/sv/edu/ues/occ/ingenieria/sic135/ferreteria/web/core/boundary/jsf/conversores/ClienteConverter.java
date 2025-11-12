package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf.conversores;


import jakarta.enterprise.context.Dependent;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.ClienteDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.Cliente;

@FacesConverter(value = "clienteConverter", managed = true)
@Dependent
public class ClienteConverter implements Converter<Cliente>{

    @Inject
    private ClienteDAO clienteDAO;

    @Override
    public Cliente getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            // Intentar convertir el id y delegar al DAO genérico
            // Primero UUID, luego Long, luego Integer; si falla, pasar el string tal cual.
            Object key = null;
            try {
                key = java.util.UUID.fromString(value);
            } catch (IllegalArgumentException ex1) {
                try {
                    key = Long.valueOf(value);
                } catch (NumberFormatException ex2) {
                    try {
                        key = Integer.valueOf(value);
                    } catch (NumberFormatException ex3) {
                        key = value;
                    }
                }
            }
            if (clienteDAO != null) {
                return clienteDAO.findById(key);
            } else {
                return null;
            }
        } catch (Exception ex) {
            // en caso de error devolver null para evitar fallos en renderizado
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Cliente value) {
        if (value == null) return "";
        try {
            Object id = value.getId();
            return (id == null) ? "" : id.toString();
        } catch (Exception ex) {
            return "";
        }
    }
}