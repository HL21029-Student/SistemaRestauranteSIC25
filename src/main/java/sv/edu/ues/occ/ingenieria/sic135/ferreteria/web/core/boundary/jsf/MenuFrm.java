package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;

import java.io.IOException;
import java.io.Serializable;
import java.util.Locale;
import java.util.ResourceBundle;

@Named("menuFrm")
@SessionScoped
public class MenuFrm implements Serializable {

    @Inject
    FacesContext facesContext;

    private DefaultMenuModel model;

    @PostConstruct
    public void init() {
        construirMenu();
    }

    public void construirMenu() {
        try {
            if (facesContext == null) {
                facesContext = FacesContext.getCurrentInstance();
            }

            Locale locale = (facesContext != null && facesContext.getViewRoot() != null)
                    ? facesContext.getViewRoot().getLocale()
                    : Locale.getDefault();

            ResourceBundle bundle;
            try {
                bundle = ResourceBundle.getBundle("Traducciones", locale);
            } catch (Exception e) {
                bundle = null;
            }

            model = new DefaultMenuModel();

            String labelTipos = bundle != null ? bundle.getString("menu.tipos") : "Tipos";
            String labelEspecificos = bundle != null ? bundle.getString("menu.especificos") : "Específicos";

            DefaultSubMenu tipos = DefaultSubMenu.builder().label(labelTipos).expanded(true).build();
            tipos.getElements().add(crearItem(bundle != null ? bundle.getString("menu.tipoAlmacen") : "Tipo Almacén", "pi pi-building", "TipoAlmacen.jsf"));
            tipos.getElements().add(crearItem(bundle != null ? bundle.getString("menu.tipoProducto") : "Tipo Producto", "pi pi-th-large", "TipoProducto.jsf"));
            tipos.getElements().add(crearItem(bundle != null ? bundle.getString("menu.tipoUnidadMedida") : "Tipo de Unidad de Medida", "pi pi-calculator", "TipoUnidadMedida.jsf"));

            DefaultSubMenu especificos = DefaultSubMenu.builder().label(labelEspecificos).expanded(true).build();
            especificos.getElements().add(crearItem(bundle != null ? bundle.getString("menu.almacen") : "Almacén", "pi pi-warehouse", "Almacen.jsf"));
            especificos.getElements().add(crearItem(bundle != null ? bundle.getString("menu.producto") : "Producto", "pi pi-box", "Producto.jsf"));
            especificos.getElements().add(crearItem(bundle != null ? bundle.getString("menu.cliente") : "Cliente", "pi pi-user", "Cliente.jsf"));
            especificos.getElements().add(crearItem(bundle != null ? bundle.getString("menu.proveedor") : "Proveedor", "pi pi-briefcase", "Proveedor.jsf"));
            especificos.getElements().add(crearItem(bundle != null ? bundle.getString("menu.caracteristicas") : "Características", "pi pi-tags", "Caracteristica.jsf"));
            especificos.getElements().add(crearItem(bundle != null ? bundle.getString("menu.compras") : "Compras", "pi pi-shopping-cart", "Compras.jsf"));
            especificos.getElements().add(crearItem(bundle != null ? bundle.getString("menu.ventas") : "Ventas", "pi pi-dollar", "Ventas.jsf"));

            model.getElements().add(tipos);
            model.getElements().add(especificos);

        } catch (Exception e) {
            e.printStackTrace();
            model = new DefaultMenuModel();
        }
    }


    private DefaultMenuItem crearItem(String label, String icon, String pagina) {
        return DefaultMenuItem.builder()
                .value(label)
                .icon(icon)
                .ajax(false)
                .command("#{menuFrm.navegar('" + pagina + "')}")
                .build();
    }

    public void navegar(String pagina) throws IOException {
        if (facesContext == null) {
            facesContext = FacesContext.getCurrentInstance();
        }
        facesContext.getExternalContext().redirect(pagina);
    }

    public DefaultMenuModel getModel() {
        if (model == null || model.getElements().isEmpty()) {
            construirMenu();
        }
        return model;
    }
}
