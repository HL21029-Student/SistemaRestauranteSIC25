package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf.conversores;

import jakarta.enterprise.context.Dependent;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.ProveedorDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.Proveedor;

@FacesConverter(value = "proveedorConverter", managed = true)
@Dependent
public class ProveedorConverter implements Converter<Proveedor> {

    @Inject
    private ProveedorDAO proveedorDAO;

    @Override
    public Proveedor getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            Object key = null;
            String v = value.trim();
            try {
                key = Integer.valueOf(v);
            } catch (NumberFormatException exInt) {
                try {
                    key = Long.valueOf(v);
                } catch (NumberFormatException exLong) {
                    try {
                        key = java.util.UUID.fromString(v);
                    } catch (IllegalArgumentException exUuid) {
                        key = v;
                    }
                }
            }
            if (proveedorDAO != null) {
                return proveedorDAO.findById(key);
            } else {
                return null;
            }
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Proveedor value) {
        if (value == null) return "";
        try {
            Object id = value.getId();
            return (id == null) ? "" : id.toString();
        } catch (Exception ex) {
            return "";
        }
    }
}
