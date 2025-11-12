package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf.conversores;

import jakarta.enterprise.context.Dependent;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.ProductoDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.Producto;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@FacesConverter(value="productoConverter", managed = true)
@Dependent
public class ProductoConverter implements Converter<Producto> {

    @Inject
    ProductoDAO productoDAO;

    @Override
    public Producto getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            UUID id = UUID.fromString(value);
            return productoDAO.findById(id);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ProductoConverter.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return null;
        } catch (Exception ex) {
            Logger.getLogger(ProductoConverter.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Producto producto) {
        if (producto == null) return "";
        try {
            return producto.getId() != null ? producto.getId().toString() : "";
        } catch (Exception ex) {
            Logger.getLogger(ProductoConverter.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return "";
        }
    }
}