package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ActionEvent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.InventarioDefaultDataAccess;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.UsuarioCrudDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.Usuario;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named("usuarioFrm")
@RequestScoped
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

    // ------------------------------
    //  Métodos CRUD
    // ------------------------------

    @Override
    public void btnGuardarHandler(ActionEvent actionEvent) {
        try {
            Usuario r = this.getRegistro();
            if (r != null) {

                // Generar UUID si no tiene
                if (r.getId() == null) {
                    r.setId(UUID.randomUUID());
                }

                getDao().create(r);
                enviarMensaje("Usuario creado correctamente", FacesMessage.SEVERITY_INFO);

                this.estado = ESTADO_CRUD.NADA;
                this.setRegistro(null);
                this.inicializarRegistros();
                return;
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error al crear usuario", ex);
            enviarMensaje("Error al crear el usuario: " + ex.getMessage(), FacesMessage.SEVERITY_ERROR);
            return;
        }

        enviarMensaje("El usuario a almacenar no puede ser nulo", FacesMessage.SEVERITY_WARN);
        this.estado = ESTADO_CRUD.NADA;
    }

    @Override
    public void btnModificarHandler(ActionEvent actionEvent) {
        if (this.getRegistro() == null) {
            enviarMensaje("No hay usuario seleccionado", FacesMessage.SEVERITY_ERROR);
            return;
        }
        try {
            getDao().update(this.getRegistro());
            enviarMensaje("Usuario modificado", FacesMessage.SEVERITY_INFO);

            this.inicializarRegistros();
            this.estado = ESTADO_CRUD.NADA;
            this.setRegistro(null);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error al modificar usuario", ex);
            enviarMensaje("Error al modificar el usuario: " + ex.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }

    @Override
    public void btnEliminarHandler(ActionEvent actionEvent) {
        if (this.getRegistro() == null) {
            enviarMensaje("No hay usuario seleccionado", FacesMessage.SEVERITY_ERROR);
            return;
        }
        try {
            getDao().delete(this.getRegistro());
            enviarMensaje("Usuario eliminado", FacesMessage.SEVERITY_INFO);

            this.inicializarRegistros();
            this.estado = ESTADO_CRUD.NADA;
            this.setRegistro(null);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error al eliminar usuario", ex);
            enviarMensaje("Error al eliminar el usuario: " + ex.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }
}
