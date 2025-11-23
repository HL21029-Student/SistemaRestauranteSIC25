package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.Dependent;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.CuentaContableDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.DetalleLibroDiarioDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.DetalleLibroMayorDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.InventarioDAOInterface;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.InventarioDefaultDataAccess;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.CuentaContable;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.DetalleLibroMayor;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.LibroMayor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Dependent
@Named
public class DetalleLibroMayorFrm extends DefaultFrm<DetalleLibroMayor> implements Serializable {

    private static final Logger LOG = Logger.getLogger(DetalleLibroMayorFrm.class.getName());

    @Inject
    FacesContext facesContext;

    @Inject
    DetalleLibroMayorDAO detalleLibroMayorDAO;

    @Inject
    DetalleLibroDiarioDAO detalleLibroDiarioDAO;

    @Inject
    CuentaContableDAO cuentaContableDAO;

    @Named("libroMayorFrm")
    @Inject
    private LibroMayorFrm libroMayorFrm;

    private LibroMayor libroMayor;
    private String criterioBusqueda;
    private List<CuentaContable> cuentasEncontradas;
    private CuentaContable cuentaSeleccionada;

    @PostConstruct
    @Override
    public void inicializar() {
        super.inicializar();
        this.cuentasEncontradas = Collections.emptyList();
        this.criterioBusqueda = "";
    }

    @Override
    protected FacesContext getFacesContext() {
        return facesContext;
    }

    @Override
    protected InventarioDAOInterface<DetalleLibroMayor, Object> getDao() {
        return detalleLibroMayorDAO;
    }

    @Override
    protected String getIdAsText(DetalleLibroMayor r) {
        if (r != null && r.getId() != null) {
            return r.getId().toString();
        }
        return null;
    }

    @Override
    protected DetalleLibroMayor getIdByText(String id) {
        if (id != null && !id.isBlank() && this.modelo.getWrappedData() != null && !this.modelo.getWrappedData().isEmpty()) {
            try {
                UUID buscado = UUID.fromString(id);
                return this.modelo.getWrappedData().stream()
                        .filter(r -> r.getId().equals(buscado))
                        .findFirst()
                        .orElse(null);
            } catch (IllegalArgumentException e) {
                LOG.log(Level.SEVERE, "Error al convertir ID: " + id, e);
            }
        }
        return null;
    }

    @Override
    protected DetalleLibroMayor nuevoRegistro() {
        return new DetalleLibroMayor();
    }

    @Override
    public InventarioDefaultDataAccess getDataAccess() {
        return detalleLibroMayorDAO;
    }

    @Override
    protected DetalleLibroMayor buscarRegistroPorId(Object id) {
        if (id instanceof UUID buscado && this.modelo != null) {
            return this.modelo.getWrappedData().stream()
                    .filter(r -> r.getId().equals(buscado))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    // 🔍 MÉTODOS PARA BÚSQUEDA Y MAYORIZACIÓN

    /**
     * Metodo para buscar cuentas contables por nombre
     */
    public void buscarCuentas() {
        try {
            if (criterioBusqueda != null && !criterioBusqueda.trim().isEmpty()) {
                cuentasEncontradas = cuentaContableDAO.findByNombreContaining(criterioBusqueda.trim());
                LOG.log(Level.INFO, "Cuentas encontradas: {0}", cuentasEncontradas.size());
            } else {
                cuentasEncontradas = Collections.emptyList();
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al buscar cuentas", ex);
            cuentasEncontradas = Collections.emptyList();
            addMessage("Error", "Error al buscar cuentas: " + ex.getMessage(), true);
        }
    }

    /**
     * Metodo para seleccionar cuenta y ejecutar mayorizacion
     */
    public void seleccionarCuenta(CuentaContable cuenta) {
        try {
            this.cuentaSeleccionada = cuenta;

            if (libroMayor != null && libroMayor.getId() != null && cuenta != null) {
                Long libroDiarioId = obtenerLibroDiarioAsociado();

                if (libroDiarioId != null) {
                    DetalleLibroMayor detalleCreado = ejecutarMayorizacion(
                            libroDiarioId,
                            cuenta.getNombre(),
                            libroMayor.getId()
                    );

                    if (detalleCreado != null) {
                        cargarDetallesDeLibroMayorSeleccionado();
                        addMessage("Éxito",
                                "Cuenta '" + cuenta.getNombre() + "' mayorizada correctamente. Saldo: " + detalleCreado.getSaldo());
                    }
                } else {
                    addMessage("Error", "No se encontró libro diario asociado para mayorizar", true);
                }
            } else {
                addMessage("Error", "Debe seleccionar un libro mayor primero", true);
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al seleccionar cuenta", ex);
            addMessage("Error", "Error al procesar cuenta: " + ex.getMessage(), true);
        }
    }

    /**
     *  Metodo para abrir el deialogo de busqueda
     */
    public void abrirBusquedaCuenta() {
        this.cuentasEncontradas = Collections.emptyList();
        this.criterioBusqueda = "";
        this.cuentaSeleccionada = null;
    }

    /**
     * Metodo auxiliar para obtener el libro diairo sleccionado
     * VERSIÓN SIMPLIFICADA - AJUSTA SEGÚN TU MODELO
     */
    private Long obtenerLibroDiarioAsociado() {
        try {

            LOG.log(Level.WARNING, "Usando ID temporal para libro diario - REEMPLAZA CON LÓGICA REAL");
            return 1L;
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al obtener libro diario asociado", ex);
            return null;
        }
    }

    /**
     * Metodo para ejecutar la mayorizacion desde ek formulario
     */
    public DetalleLibroMayor ejecutarMayorizacion(Long libroDiarioId, String nombreCuenta, Long idLibroMayor) {
        try {
            DetalleLibroMayor resultado = detalleLibroMayorDAO.mayorizarYCrearDetalle(libroDiarioId, nombreCuenta, idLibroMayor);

            if (resultado != null) {
                super.inicializar(); // Recargar datos
                LOG.log(Level.INFO, "Mayorización completada para cuenta: {0}", nombreCuenta);
                return resultado;
            } else {
                LOG.log(Level.WARNING, "No se pudo procesar la cuenta: {0}", nombreCuenta);
                return null;
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al ejecutar mayorización", ex);
            return null;
        }
    }

    /**
     * Metodo para obtener los detalles de un libro mayor en especifico
     */
    public List<DetalleLibroMayor> obtenerDetallesPorLibroMayor(Long libroMayorId, int first, int max) {
        try {
            return detalleLibroMayorDAO.findByLibroMayorId(libroMayorId, first, max);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al obtener detalles del libro mayor", ex);
            return Collections.emptyList();
        }
    }

    /**
     * Metodo para obtener todos los detalles de un libro mayor
     */
    public List<DetalleLibroMayor> obtenerTodosDetallesPorLibroMayor(Long libroMayorId) {
        try {
            return detalleLibroMayorDAO.findByLibroMayorId(libroMayorId, 0, 1000);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al obtener detalles del libro mayor", ex);
            return Collections.emptyList();
        }
    }

    /**
     * Metodo para cargar detalles cuando se selecciona un libro mayor
     */
    public void cargarDetallesDeLibroMayorSeleccionado() {
        if (libroMayor != null && libroMayor.getId() != null) {
            try {
                List<DetalleLibroMayor> detalles = obtenerTodosDetallesPorLibroMayor(libroMayor.getId());
                LOG.log(Level.INFO, "Detalles cargados: {0} para libro mayor ID: {1}",
                        new Object[]{detalles.size(), libroMayor.getId()});
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Error al cargar detalles del libro mayor", ex);
            }
        }
    }

    /**
     * Metodo simplificado para mayorizar cuentas comunes
     */
    public void mayorizarCuentasComunes() {
        try {
            if (libroMayor != null && libroMayor.getId() != null) {
                Long libroDiarioId = obtenerLibroDiarioAsociado();
                if (libroDiarioId != null) {
                    // Lista de cuentas comunes para mayorizar
                    String[] cuentasComunes = {
                            "ACTIVOS CORRIENTES",
                            "PASIVOS CORRIENTES",
                            "PATRIMONIO",
                            "INGRESOS",
                            "GASTOS"
                    };

                    int procesadas = 0;
                    for (String cuenta : cuentasComunes) {
                        DetalleLibroMayor resultado = ejecutarMayorizacion(libroDiarioId, cuenta, libroMayor.getId());
                        if (resultado != null) {
                            procesadas++;
                        }
                    }

                    addMessage("Proceso completado",
                            "Se mayorizaron " + procesadas + " cuentas comunes");
                    cargarDetallesDeLibroMayorSeleccionado();
                } else {
                    addMessage("Error", "No se encontró libro diario asociado", true);
                }
            } else {
                addMessage("Error", "Debe seleccionar un libro mayor primero", true);
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error en mayorización", ex);
            addMessage("Error", "Error en mayorización: " + ex.getMessage(), true);
        }
    }

    //MÉTODOS PARA MENSAJES

    public void addMessage(String summary, String detail) {
        addMessage(summary, detail, false);
    }
    public void addMessage(String summary, String detail, boolean isError) {
        try {
            jakarta.faces.application.FacesMessage.Severity severity =
                    isError ? jakarta.faces.application.FacesMessage.SEVERITY_ERROR
                            : jakarta.faces.application.FacesMessage.SEVERITY_INFO;
            jakarta.faces.application.FacesMessage message =
                    new jakarta.faces.application.FacesMessage(severity, summary, detail);
            facesContext.addMessage(null, message);
        } catch (Exception e) {
            Level level = isError ? Level.SEVERE : Level.INFO;
            LOG.log(level, summary + " - " + detail);
        }
    }

    // ETTERS Y SETTERS

    public LibroMayorFrm getLibroMayorFrm() {
        return libroMayorFrm;
    }

    public void setLibroMayorFrm(LibroMayorFrm libroMayorFrm) {
        this.libroMayorFrm = libroMayorFrm;
    }

    public LibroMayor getLibroMayor() {
        return libroMayor;
    }

    public void setLibroMayor(LibroMayor libroMayor) {
        this.libroMayor = libroMayor;
        if (libroMayor != null) {
            cargarDetallesDeLibroMayorSeleccionado();
        }
    }

    public DetalleLibroDiarioDAO getDetalleLibroDiarioDAO() {
        return detalleLibroDiarioDAO;
    }

    public String getCriterioBusqueda() {
        return criterioBusqueda;
    }

    public void setCriterioBusqueda(String criterioBusqueda) {
        this.criterioBusqueda = criterioBusqueda;
    }

    public List<CuentaContable> getCuentasEncontradas() {
        return cuentasEncontradas;
    }

    public void setCuentasEncontradas(List<CuentaContable> cuentasEncontradas) {
        this.cuentasEncontradas = cuentasEncontradas;
    }

    public CuentaContable getCuentaSeleccionada() {
        return cuentaSeleccionada;
    }

    public void setCuentaSeleccionada(CuentaContable cuentaSeleccionada) {
        this.cuentaSeleccionada = cuentaSeleccionada;
    }
}