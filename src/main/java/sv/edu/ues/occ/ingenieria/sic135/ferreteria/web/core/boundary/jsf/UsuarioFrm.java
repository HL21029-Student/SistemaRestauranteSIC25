package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.InventarioDefaultDataAccess;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.UsuarioCrudDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.Usuario;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named("usuarioFrm")
@ViewScoped
public class UsuarioFrm extends DefaultFrm<Usuario> {

    private static final Logger LOGGER = Logger.getLogger(UsuarioFrm.class.getName());

    @Inject
    private UsuarioCrudDAO usuarioCrudDAO;

    @Inject
    private FacesContext facesContext;

    public UsuarioFrm() {
        this.nombreBean = "usuarioFrm";
    }

    @Override
    protected FacesContext getFacesContext() {
        return facesContext;
    }

    @Override
    protected InventarioDefaultDataAccess<Usuario, Object> getDao() {
        return usuarioCrudDAO;
    }

    @Override
    protected String getIdAsText(Usuario r) {
        return (r != null && r.getId() != null) ? r.getId().toString() : null;
    }

    @Override
    protected Usuario getIdByText(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }
        try {
            UUID uuid = UUID.fromString(id);
            return usuarioCrudDAO.findById(uuid);
        } catch (IllegalArgumentException ex) {
            LOGGER.log(Level.WARNING, "ID de usuario no válido: " + id, ex);
            return null;
        }
    }

    /**
     * 🔧 Este método es el más importante porque PrimeFaces envía el ID como String.
     * Aquí lo convertimos a UUID correctamente.
     */
    @Override
    protected Usuario buscarRegistroPorId(Object id) {
        if (id == null) {
            return null;
        }
        try {
            UUID uuid = UUID.fromString(id.toString());
            return usuarioCrudDAO.findById(uuid);
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "ID inválido para Usuario: " + id, ex);
            return null;
        }
    }

    @Override
    protected Usuario nuevoRegistro() {
        Usuario u = new Usuario();
        u.setActivo(true);
        return u;
    }

    @Override
    public InventarioDefaultDataAccess<Usuario, Object> getDataAccess() {
        return usuarioCrudDAO;
    }

    // Se usan directamente los métodos CRUD heredados de DefaultFrm:
    // btnNuevoHandler, btnCancelarHandler, btnGuardarHandler, btnModificarHandler, btnEliminarHandler
}
