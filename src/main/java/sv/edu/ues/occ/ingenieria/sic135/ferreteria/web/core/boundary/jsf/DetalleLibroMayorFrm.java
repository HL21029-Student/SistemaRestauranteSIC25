package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.Dependent;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.DetalleLibroDiarioDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.DetalleLibroMayorDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.InventarioDAOInterface;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.InventarioDefaultDataAccess;
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

    @Inject
    FacesContext facesContext;

    @Inject
    DetalleLibroMayorDAO detalleLibroMayorDAO;

    // Para posibles usos del libro diario
    @Inject
    DetalleLibroDiarioDAO detalleLibroDiarioDAO;

    @Named("libroMayorFrm")
    @Inject
    private LibroMayorFrm libroMayorFrm;

    private LibroMayor libroMayor;

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
                Logger.getLogger(DetalleLibroMayorFrm.class.getName())
                        .log(Level.SEVERE, "Error al convertir ID: " + id, e);
            }
        }
        return null;
    }

    @PostConstruct
    @Override
    public void inicializar() {
        super.inicializar();
    }

    @Override
    protected DetalleLibroMayor nuevoRegistro() {
        DetalleLibroMayor dlm = new DetalleLibroMayor();
        return dlm;
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

    /**
     * Método para ejecutar la mayorización desde el formulario
     *
     * @param libroDiarioId Id del libro diario a procesar
     * @param nombreCuenta  Nombre de la cuenta contable a mayorizar
     * @param idLibroMayor  ID del libro mayor destino
     * @return DetalleLibroMayor creado o null si hay error
     */
    public DetalleLibroMayor ejecutarMayorizacion(Long libroDiarioId, String nombreCuenta, Long idLibroMayor) {
        try {
            DetalleLibroMayor resultado = detalleLibroMayorDAO.mayorizarYCrearDetalle(libroDiarioId, nombreCuenta, idLibroMayor);

            if (resultado != null) {
                super.inicializar();
                addMessage("Mayorización completada",
                        "Cuenta: " + nombreCuenta + " procesada exitosamente. Saldo: " + resultado.getSaldo());
            } else {
                addMessage("Error en mayorización",
                        "No se pudo procesar la cuenta: " + nombreCuenta, true);
            }
            return resultado;
        } catch (Exception ex) {
            Logger.getLogger(DetalleLibroMayorFrm.class.getName())
                    .log(Level.SEVERE, "Error al ejecutar mayorización", ex);
            addMessage("Error",
                    "Ocurrió un error durante la mayorización: " + ex.getMessage(), true);
            return null;
        }
    }

    /**
     * Método para obtener los detalles de un libro mayor en específico
     *
     * @param libroMayorId Id del libro mayor
     * @param first        primer registro
     * @param max          máximo de registros
     * @return Lista de detalles del libro mayor
     */
    public List<DetalleLibroMayor> obtenerDetallesPorLibroMayor(Long libroMayorId, int first, int max) {
        try {
            return detalleLibroMayorDAO.findByLibroMayorId(libroMayorId, first, max);
        } catch (Exception ex) {
            Logger.getLogger(DetalleLibroMayorFrm.class.getName())
                    .log(Level.SEVERE, "Error al obtener detalles del libro mayor", ex);
            return Collections.emptyList();
        }
    }

    /**
     * Método para obtener todos los detalles de un libro mayor (sin paginación)
     *
     * @param libroMayorId Id del libro mayor
     * @return Lista completa de detalles del libro mayor
     */
    public List<DetalleLibroMayor> obtenerTodosDetallesPorLibroMayor(Long libroMayorId) {
        try {
            // Usar un número grande para obtener todos los registros
            return detalleLibroMayorDAO.findByLibroMayorId(libroMayorId, 0, 1000);
        } catch (Exception ex) {
            Logger.getLogger(DetalleLibroMayorFrm.class.getName())
                    .log(Level.SEVERE, "Error al obtener detalles del libro mayor", ex);
            return Collections.emptyList();
        }
    }

    /**
     * Método para cargar detalles cuando se selecciona un libro mayor
     */
    public void cargarDetallesDeLibroMayorSeleccionado() {
        if (libroMayor != null && libroMayor.getId() != null) {
            try {
                // Cargar los detalles del libro mayor seleccionado
                List<DetalleLibroMayor> detalles = obtenerTodosDetallesPorLibroMayor(libroMayor.getId());
                // Aquí puedes actualizar tu modelo con los detalles cargados
            } catch (Exception ex) {
                Logger.getLogger(DetalleLibroMayorFrm.class.getName())
                        .log(Level.SEVERE, "Error al cargar detalles del libro mayor", ex);
            }
        }
    }

    /**
     * Método para mayorizar todas las cuentas de un libro diario
     */
    public void mayorizarTodasLasCuentas(Long libroDiarioId, Long idLibroMayor) {
        try {
            // Aquí puedes implementar la lógica para mayorizar múltiples cuentas
            // Por ejemplo, obtener todas las cuentas del libro diario y mayorizar cada una
            addMessage("Proceso iniciado", "Mayorización de cuentas en proceso...");
        } catch (Exception ex) {
            Logger.getLogger(DetalleLibroMayorFrm.class.getName())
                    .log(Level.SEVERE, "Error en mayorización masiva", ex);
            addMessage("Error", "Error en mayorización masiva: " + ex.getMessage(), true);
        }
    }

    // 🔧 MÉTODOS PARA MENSAJES

    /**
     * Método para agregar mensaje de información
     */
    public void addMessage(String summary, String detail) {
        addMessage(summary, detail, false);
    }

    /**
     * Método para agregar mensaje (info o error)
     */
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
            Logger.getLogger(DetalleLibroMayorFrm.class.getName())
                    .log(level, summary + " - " + detail);
        }
    }

    // 🔧 GETTERS Y SETTERS

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
        // Cuando se establece el libro mayor, cargar sus detalles automáticamente
        if (libroMayor != null) {
            cargarDetallesDeLibroMayorSeleccionado();
        }
    }

    public DetalleLibroDiarioDAO getDetalleLibroDiarioDAO() {
        return detalleLibroDiarioDAO;
    }
}