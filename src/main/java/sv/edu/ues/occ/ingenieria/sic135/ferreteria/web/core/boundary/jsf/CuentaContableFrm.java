package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf;

import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.CuentaContableCrudDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.InventarioDefaultDataAccess;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.CuentaContable;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.TipoCuentaDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.SubTipoCuentaDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.TipoCuenta;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.SubTipoCuenta;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.CuentaContableDAO;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Named("cuentaContableFrm")
@ViewScoped
public class CuentaContableFrm extends DefaultFrm<CuentaContable> implements Serializable {

    private static final Logger LOG = Logger.getLogger(CuentaContableFrm.class.getName());

    @Inject
    private CuentaContableCrudDAO cuentaContableCrudDAO;

    @Inject
    private FacesContext facesContext;

    @Inject
    private TipoCuentaDAO tipoCuentaDAO;

    @Inject
    private SubTipoCuentaDAO subTipoCuentaDAO;

    @Inject
    private CuentaContableDAO cuentaContableDAO;

    private List<TipoCuenta> tipos;

    private List<SubTipoCuenta> subtipos;

    private Long tipoCuentaIdSeleccionado;

    private Long subTipoCuentaIdSeleccionado;

    public CuentaContableFrm() {
        this.nombreBean = "cuentaContableFrm";
    }

    @jakarta.annotation.PostConstruct
    private void initTipos() {
        try {
            tipos = tipoCuentaDAO.findAll();
            subtipos = subTipoCuentaDAO.findAll(); // cargar subtipos como fallback
            if (tipos == null || tipos.isEmpty()) {
                getFacesContext().addMessage(null, new jakarta.faces.application.FacesMessage(jakarta.faces.application.FacesMessage.SEVERITY_WARN, "No hay tipos de cuenta disponibles", "No hay tipos de cuenta disponibles"));
            }
            if (subtipos == null || subtipos.isEmpty()) {
                getFacesContext().addMessage(null, new jakarta.faces.application.FacesMessage(jakarta.faces.application.FacesMessage.SEVERITY_WARN, "No hay subtipos de cuenta disponibles", "No hay subtipos de cuenta disponibles"));
            }
        } catch (Exception ex) {
            tipos = List.of();
            subtipos = List.of();
            getFacesContext().addMessage(null, new jakarta.faces.application.FacesMessage(jakarta.faces.application.FacesMessage.SEVERITY_ERROR, "Error cargando tipos/subtipos", ex.getMessage()));
        }
    }

    public List<TipoCuenta> getTipos() {
        return tipos;
    }

    public List<SubTipoCuenta> getSubtipos() {
        return subtipos;
    }

    public Long getTipoCuentaIdSeleccionado() {
        return tipoCuentaIdSeleccionado;
    }

    public void setTipoCuentaIdSeleccionado(Long tipoCuentaIdSeleccionado) {
        this.tipoCuentaIdSeleccionado = tipoCuentaIdSeleccionado;
        if (this.registro != null && tipoCuentaIdSeleccionado != null) {
            // Sincronizar entidad seleccionada con el id
            this.registro.setIdTipoCuenta(
                    tipos.stream().filter(t -> tipoCuentaIdSeleccionado.equals(t.getId())).findFirst().orElse(null)
            );
        }
    }

    public Long getSubTipoCuentaIdSeleccionado() {
        return subTipoCuentaIdSeleccionado;
    }

    public void setSubTipoCuentaIdSeleccionado(Long subTipoCuentaIdSeleccionado) {
        this.subTipoCuentaIdSeleccionado = subTipoCuentaIdSeleccionado;
        if (this.registro != null && subTipoCuentaIdSeleccionado != null) {
            this.registro.setIdSubTipoCuenta(
                    subtipos.stream().filter(s -> subTipoCuentaIdSeleccionado.equals(s.getId())).findFirst().orElse(null)
            );
        }
    }

    /**
     * Método para autocompletar cuentas padre. Devuelve cuentas cuyo nombre o código coincide con la consulta.
     */
    public List<CuentaContable> completarCuentaPadre(String query) {
        if (query == null || query.isBlank()) return List.of();
        try {
            String q = query.trim();
            if (!q.isEmpty()) {
                // 1) Buscar cuentas cuyo código contenga el texto
                List<CuentaContable> porCodigo = cuentaContableDAO.findByCodigoLike(q, 0, 20);
                // 2) Buscar adicionalmente por nombre y combinar, evitando duplicados
                List<CuentaContable> porNombre = cuentaContableDAO.findByNombreLike(q, 0, 20);

                // Combinar resultados, priorizando coincidencias por código
                java.util.Set<Long> ids = new java.util.HashSet<>();
                java.util.List<CuentaContable> resultado = new java.util.ArrayList<>();
                for (CuentaContable c : porCodigo) {
                    if (c.getId() != null && ids.add(c.getId())) {
                        resultado.add(c);
                    }
                }
                for (CuentaContable c : porNombre) {
                    if (c.getId() != null && ids.add(c.getId())) {
                        resultado.add(c);
                    }
                }
                return resultado;
            }
            return List.of();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error buscando cuentas padre", ex);
            return List.of();
        }
    }

    public void cargarSubtiposPorTipo() {
        if (this.tipoCuentaIdSeleccionado != null) {
            // Nota: la entidad SubTipoCuenta no referencia a TipoCuenta en el modelo actual,
            // por lo que no es posible filtrar por tipo sin modificar el esquema.
            // Como comportamiento seguro, cargamos todos los subtipos y el usuario podrá seleccionar.
            subtipos = subTipoCuentaDAO.findAll();
            return;
        }
        subtipos = List.of();
    }

    @Override
    protected FacesContext getFacesContext() {
        return facesContext;
    }

    @Override
    protected InventarioDefaultDataAccess<CuentaContable, Object> getDao() {
        return cuentaContableCrudDAO;
    }

    @Override
    protected String getIdAsText(CuentaContable r) {
        return r != null && r.getId() != null ? r.getId().toString() : null;
    }

    @Override
    protected CuentaContable getIdByText(String id) {
        if (id == null) {
            return null;
        }
        return cuentaContableCrudDAO.findById(Long.valueOf(id));
    }

    @Override
    protected CuentaContable nuevoRegistro() {
        return new CuentaContable();
    }

    @Override
    public InventarioDefaultDataAccess<CuentaContable, Object> getDataAccess() {
        return cuentaContableCrudDAO;
    }

    @Override
    protected CuentaContable buscarRegistroPorId(Object id) {
        if (id == null) {
            return null;
        }
        return cuentaContableCrudDAO.findById(id);
    }

    @Override
    public void btnEliminarHandler(jakarta.faces.event.ActionEvent actionEvent) {
        try {
            super.btnEliminarHandler(actionEvent);
        } catch (IllegalStateException ex) {
            // Mensaje localizado desde resource bundle o mensaje amigable por defecto
            String mensaje;
            try {
                java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("Traducciones", getFacesContext().getViewRoot().getLocale());
                mensaje = bundle.getString("error.cuenta.eliminar.dependencias");
            } catch (Exception ignored) {
                mensaje = "La cuenta no puede eliminarse porque tiene subcuentas, movimientos o un manual asociado.";
            }
            getFacesContext().addMessage(null, new jakarta.faces.application.FacesMessage(jakarta.faces.application.FacesMessage.SEVERITY_WARN, mensaje, mensaje));
        } catch (Exception ex) {
            // otros errores se manejarán en DefaultFrm, pero mostramos un mensaje más amigable
            String detalle = ex.getMessage() != null ? ex.getMessage() : "Ocurrió un error inesperado al intentar eliminar la cuenta.";
            getFacesContext().addMessage(null, new jakarta.faces.application.FacesMessage(jakarta.faces.application.FacesMessage.SEVERITY_ERROR, "Error al eliminar la cuenta", detalle));
            throw ex;
        }
    }

    @Override
    public void btnNuevoHandler(jakarta.faces.event.ActionEvent actionEvent) {
        try {
            LOG.info("btnNuevoHandler invoked");
            super.btnNuevoHandler(actionEvent);
            LOG.info("registro inicializado: " + this.registro);
            // resetear ids seleccionados
            this.tipoCuentaIdSeleccionado = null;
            this.subTipoCuentaIdSeleccionado = null;
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error en btnNuevoHandler", ex);
            getFacesContext().addMessage(null, new jakarta.faces.application.FacesMessage(jakarta.faces.application.FacesMessage.SEVERITY_ERROR, "Error al iniciar nuevo registro", ex.getMessage()));
        }
    }

    @Override
    public void btnSeleccionarHandler(CuentaContable registro) {
        try {
            LOG.info("btnSeleccionarHandler invoked for registro: " + registro);
            super.btnSeleccionarHandler(registro);
            LOG.info("registro cargado en bean: " + this.registro);
            // Sincronizar ids seleccionados para los dropdowns
            if (this.registro != null && this.registro.getIdTipoCuenta() != null) {
                this.tipoCuentaIdSeleccionado = this.registro.getIdTipoCuenta().getId();
            } else {
                this.tipoCuentaIdSeleccionado = null;
            }
            if (this.registro != null && this.registro.getIdSubTipoCuenta() != null) {
                this.subTipoCuentaIdSeleccionado = this.registro.getIdSubTipoCuenta().getId();
            } else {
                this.subTipoCuentaIdSeleccionado = null;
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error en btnSeleccionarHandler", ex);
            getFacesContext().addMessage(null, new jakarta.faces.application.FacesMessage(jakarta.faces.application.FacesMessage.SEVERITY_ERROR, "Error al seleccionar registro", ex.getMessage()));
        }
    }

    @Override
    public void btnGuardarHandler(jakarta.faces.event.ActionEvent actionEvent) {
        try {
            LOG.info("btnGuardarHandler invoked, registro=" + this.registro);
            super.btnGuardarHandler(actionEvent);
            LOG.info("btnGuardarHandler completed");
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error en btnGuardarHandler", ex);
            getFacesContext().addMessage(null, new jakarta.faces.application.FacesMessage(jakarta.faces.application.FacesMessage.SEVERITY_ERROR, "Error al guardar registro", ex.getMessage()));
        }
    }
}
