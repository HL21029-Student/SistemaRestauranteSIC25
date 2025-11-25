package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ActionEvent;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.*;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.CuentaContable;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.DetalleLibroDiario;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.LibroDiario;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ViewScoped
public class DetalleLibroDiarioFrm extends DefaultFrm<DetalleLibroDiario> implements Serializable {

    private static final Logger LOG = Logger.getLogger(DetalleLibroDiarioFrm.class.getName());

    @Inject
    FacesContext facesContext;

    @Inject
    DetalleLibroDiarioDAO detalleLibroDiarioDAO;

    @Inject
    CuentaContableDAO cuentaContableDAO;

    @Named("libroDiarioFrm")
    @Inject
    private LibroDiarioFrm libroDiarioFrm;

    // Propiedades
    private List<DetalleLibroDiario> detalleLibroDiarios;
    private List<DetalleLibroDiario> listaDetalleLibroDiarios;
    private List<CuentaContable> cuentaContables;
    private Long idCuentaContable;
    private LibroDiario idLibroDiario;
    private CuentaContable cuentaContableTemporal; // Propiedad temporal para el diálogo

    @Override
    protected FacesContext getFacesContext() {
        return facesContext;
    }

    @Override
    protected InventarioDAOInterface<DetalleLibroDiario, Object> getDao() {
        return detalleLibroDiarioDAO;
    }

    @Override
    protected String getIdAsText(DetalleLibroDiario r) {
        if (r != null && r.getId() != null) {
            return r.getId().toString();
        }
        return null;
    }

    @Override
    protected DetalleLibroDiario getIdByText(String id) {
        if (id != null && !id.isBlank() && this.modelo != null && this.modelo.getWrappedData() != null && !this.modelo.getWrappedData().isEmpty()) {
            try {
                Long buscado = Long.parseLong(id);
                return this.modelo.getWrappedData().stream().filter(r -> r.getId().equals(buscado)).findFirst().orElse(null);
            } catch (IllegalArgumentException e) {
                LOG.log(Level.SEVERE, "Error al convertir ID: " + e.getMessage(), e);
            }
        }
        return null;
    }

    @PostConstruct
    @Override
    public void inicializar() {
        super.inicializar();

        if (this.idCuentaContable != null) {
            listaDetalleLibroDiarios = detalleLibroDiarioDAO.findByCuentaContableId(this.idCuentaContable, 0, Integer.MAX_VALUE);
        } else {
            listaDetalleLibroDiarios = List.of();
        }
    }

    @Override
    protected DetalleLibroDiario nuevoRegistro() {
        System.out.println("para agregar cambios");
        DetalleLibroDiario detalleLibroDiario = new DetalleLibroDiario();
        detalleLibroDiario.setId(UUID.randomUUID());
        detalleLibroDiario.setFecha(new Date());
        detalleLibroDiario.setNumeroPartida(0L);
        detalleLibroDiario.setConcepto("");
        detalleLibroDiario.setParcial(BigDecimal.ZERO);
        detalleLibroDiario.setDebe(false);
        detalleLibroDiario.setMonto(BigDecimal.ZERO);
        return detalleLibroDiario;
    }

    @Override
    public InventarioDefaultDataAccess getDataAccess() {
        return detalleLibroDiarioDAO;
    }

    @Override
    protected DetalleLibroDiario buscarRegistroPorId(Object id) {
        if (id instanceof UUID buscado && this.modelo != null) {
            return this.modelo.getWrappedData().stream().filter(r -> r.getId().equals(buscado)).findFirst().orElse(null);
        }
        return null;
    }

    @Override
    public List<DetalleLibroDiario> cargarDatos(int first, int max) {
        try {
            LOG.info("=== CARGAR DATOS DETALLE LIBRO DIARIO ===");

            if (libroDiarioFrm != null && libroDiarioFrm.getRegistro() != null) {
                LOG.info("LibroDiario ID: " + libroDiarioFrm.getRegistro().getId());
            }

            if (first >= 0 && max > 0 && libroDiarioFrm != null && libroDiarioFrm.getRegistro() != null) {
                // USAR EL NUEVO MÉTODO: cargar por libro diario
                List<DetalleLibroDiario> resultados = detalleLibroDiarioDAO.findByLibroDiarioId(
                        libroDiarioFrm.getRegistro().getId(), first, max);

                // Debug: verificar qué datos se están cargando
                LOG.info("Registros encontrados: " + resultados.size());
                for (DetalleLibroDiario detalle : resultados) {
                    if (detalle.getIdCuentaContable() != null) {
                        LOG.info("Registro ID: " + detalle.getId() +
                                " - Cuenta: " + detalle.getIdCuentaContable().getNombre() +
                                " (ID: " + detalle.getIdCuentaContable().getId() + ")");
                    } else {
                        LOG.info("Registro ID: " + detalle.getId() + " - SIN CUENTA");
                    }
                }

                return resultados;
            } else {
                LOG.info("No se puede cargar datos - condiciones no cumplidas");
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error al cargar datos: " + e.getMessage(), e);
        }
        return List.of();
    }

    @Override
    public int contarDatos() {
        try {
            LOG.info("=== CONTAR DATOS DETALLE LIBRO DIARIO ===");

            if (libroDiarioFrm != null && libroDiarioFrm.getRegistro() != null) {
                // USAR EL NUEVO MÉTODO: contar por libro diario
                Long count = detalleLibroDiarioDAO.countByLibroDiarioId(libroDiarioFrm.getRegistro().getId());
                LOG.info("Total registros para libro diario " + libroDiarioFrm.getRegistro().getId() + ": " + count);
                return count != null ? count.intValue() : 0;
            } else {
                LOG.info("No se puede contar - libroDiarioFrm o registro es nulo");
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error al contar datos: " + e.getMessage(), e);
        }
        return 0;
    }


    public List<CuentaContable> buscarCuentaContablePorNombre(final String nombre) {
        try {
            if (nombre != null && !nombre.isBlank()) {
                return cuentaContableDAO.findByNombreLike(nombre, 0, Integer.MAX_VALUE);
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error al buscar cuenta contable: " + e.getMessage(), e);
        }
        return List.of();

    }

    /**
     * Metodo para seleccionar cuenta contable desde el diálogo
     */
    public void seleccionarCuentaContable() {
        if (this.cuentaContableTemporal != null) {
            this.registro.setIdCuentaContable(this.cuentaContableTemporal);

            // Debug
            LOG.info("CUENTA ASIGNADA - Nombre: " + this.registro.getIdCuentaContable().getNombre() +
                    ", ID: " + this.registro.getIdCuentaContable().getId() +
                    ", Código: " + this.registro.getIdCuentaContable().getCodigo());

            addMessage("Cuenta contable seleccionada: " + this.registro.getIdCuentaContable().getNombre());
            this.cuentaContableTemporal = null; // Limpiar temporal
        } else {
            addMessage("Error: No se seleccionó ninguna cuenta contable", FacesMessage.SEVERITY_WARN);
        }
    }

    @Override
    public void btnGuardarHandler(ActionEvent actionEvent) {
        if (this.registro != null && libroDiarioFrm != null && libroDiarioFrm.getRegistro() != null) {
            try {
                // Vincular el libro diario al detalle
                this.registro.setLibroDiario(libroDiarioFrm.getRegistro());
                this.idLibroDiario = libroDiarioFrm.getRegistro();
                if (this.registro.getIdCuentaContable() == null) {
                    addMessage("Error: Debe seleccionar una cuenta contable", FacesMessage.SEVERITY_ERROR);
                    return;
                }
                LOG.info("GUARDANDO - Cuenta: " + this.registro.getIdCuentaContable().getNombre() +
                        " (ID: " + this.registro.getIdCuentaContable().getId() +
                        ", Código: " + this.registro.getIdCuentaContable().getCodigo() + ")");

                // Validar campos obligatorios
                if (this.registro.getFecha() == null) {
                    this.registro.setFecha(new Date());
                }
                if (this.registro.getNumeroPartida() == null) {
                    this.registro.setNumeroPartida(0L);
                }
                if (this.registro.getConcepto() == null || this.registro.getConcepto().isBlank()) {
                    addMessage("Error: El concepto es obligatorio", FacesMessage.SEVERITY_ERROR);
                    return;
                }
                if (this.registro.getMonto() == null || this.registro.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
                    addMessage("Error: El monto debe ser mayor a cero", FacesMessage.SEVERITY_ERROR);
                    return;
                }
                if (this.registro.getParcial() == null) {
                    this.registro.setParcial(BigDecimal.ZERO);
                }

                super.btnGuardarHandler(actionEvent);
                if (this.modelo != null) {
                    this.modelo.setWrappedData(null);
                }

                addMessage("Detalle de libro diario guardado exitosamente");

            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Error al guardar detalle: " + e.getMessage(), e);
                addMessage("Error al guardar: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
            }
        } else {
            addMessage("Error: No hay registro activo o libro diario seleccionado", FacesMessage.SEVERITY_ERROR);
        }
    }

    @Override
    public void btnSeleccionarHandler(DetalleLibroDiario registro) {
        super.btnSeleccionarHandler(registro);
        // Inicializar la propiedad temporal con la cuenta actual
        if (registro != null && registro.getIdCuentaContable() != null) {
            this.cuentaContableTemporal = registro.getIdCuentaContable();
        }
    }

    @Override
    public void btnNuevoHandler(ActionEvent actionEvent) {
        super.btnNuevoHandler(actionEvent);
        // Limpiar la propiedad temporal al crear nuevo registro
        this.cuentaContableTemporal = null;
    }

    @Override
    public void btnCancelarHandler(ActionEvent actionEvent) {
        super.btnCancelarHandler(actionEvent);
        // Limpiar la propiedad temporal al cancelar
        this.cuentaContableTemporal = null;
    }

    // Método auxiliar para agregar mensajes
    private void addMessage(String message) {
        addMessage(message, FacesMessage.SEVERITY_INFO);
    }

    private void addMessage(String message, FacesMessage.Severity severity) {
        FacesMessage facesMsg = new FacesMessage(severity, message, null);
        facesContext.addMessage(null, facesMsg);
    }

    // Getters y Setters
    public Long getIdCuentaContable() {
        return idCuentaContable;
    }

    public void setIdCuentaContable(Long idCuentaContable) {
        this.idCuentaContable = idCuentaContable;
    }

    public LibroDiario getIdLibroDiario() {
        return idLibroDiario;
    }

    public void setIdLibroDiario(LibroDiario idLibroDiario) {
        this.idLibroDiario = idLibroDiario;
    }

    public LibroDiarioFrm getLibroDiarioFrm() {
        return libroDiarioFrm;
    }

    public void setLibroDiarioFrm(LibroDiarioFrm libroDiarioFrm) {
        this.libroDiarioFrm = libroDiarioFrm;
    }

    public CuentaContable getCuentaContableTemporal() {
        return cuentaContableTemporal;
    }

    public void setCuentaContableTemporal(CuentaContable cuentaContableTemporal) {
        this.cuentaContableTemporal = cuentaContableTemporal;
    }
}
