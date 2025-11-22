package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.model.TreeNode;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.event.NodeSelectEvent;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.InventarioDAOInterface;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.InventarioDefaultDataAccess;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.LibroMayorDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.LibroDiarioDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.LibroMayor;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.LibroDiario;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ViewScoped
public class LibroMayorFrm extends DefaultFrm<LibroMayor> implements Serializable {

    private static final Logger LOG = Logger.getLogger(LibroMayorFrm.class.getName());

    @Inject
    FacesContext facesContext;

    @Inject
    LibroMayorDAO libroMayorDAO;

    @Inject
    LibroDiarioDAO libroDiarioDAO;

    private List<LibroDiario> librosDiariosDisponibles;

    // PROPIEDADES PARA EL treeTable
    private TreeNode root;
    private TreeNode selectedNode;

    // PROPIEDAD PARA EL SELECTOR DE LIBRO DIARIO
    private Long libroDiarioIdSeleccionado;

    @Override
    protected FacesContext getFacesContext() {
        return facesContext;
    }

    @Override
    protected InventarioDAOInterface<LibroMayor, Object> getDao() {
        return libroMayorDAO;
    }

    @Override
    protected String getIdAsText(LibroMayor r) {
        if (r != null && r.getId() != null) {
            return r.getId().toString();
        }
        return null;
    }

    @Override
    protected LibroMayor getIdByText(String id) {
        if (id != null && !id.isBlank() && this.modelo.getWrappedData() != null) {
            try {
                Long buscado = Long.parseLong(id);
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

    @PostConstruct
    @Override
    public void inicializar() {
        super.inicializar();
        cargarLibrosDiariosDisponibles();
        inicializarArbol();
    }

    /**
     * Cargar libros diarios disponibles para el selector
     */
    private void cargarLibrosDiariosDisponibles() {
        try {
            librosDiariosDisponibles = libroDiarioDAO.findRange(0, 100);
            LOG.log(Level.INFO, "Cargados {0} libros diarios disponibles",
                    librosDiariosDisponibles != null ? librosDiariosDisponibles.size() : 0);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al cargar libros diarios disponibles", ex);
            librosDiariosDisponibles = List.of();
        }
    }

    /**
     * INICIALIZAR EL ÁRBOL PARA EL treeTable
     */
    private void inicializarArbol() {
        try {
            // Crear nodo raíz
            root = new DefaultTreeNode("Root", null);

            // Obtener todos los libros mayores
            List<LibroMayor> librosMayores = libroMayorDAO.findRange(0, 100);

            // Crear nodos para cada libro mayor
            for (LibroMayor libroMayor : librosMayores) {
                TreeNode nodoLibro = new DefaultTreeNode(libroMayor, root);
                // Si necesitas agregar hijos (detalles), los agregas aquí
            }

            LOG.log(Level.INFO, "Árbol inicializado con {0} nodos",
                    root.getChildCount());

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al inicializar árbol", ex);
            root = new DefaultTreeNode();
        }
    }

    /**
     * MÉTODO PARA MANEJAR LA SELECCIÓN DE NODOS
     */
    public void onNodeSelect(NodeSelectEvent event) {
        try {
            this.selectedNode = event.getTreeNode();

            // Obtener el libro mayor seleccionado
            if (selectedNode != null && selectedNode.getData() instanceof LibroMayor) {
                LibroMayor libroSeleccionado = (LibroMayor) selectedNode.getData();
                this.registro = libroSeleccionado;
                this.estado = ESTADO_CRUD.MODIFICAR;

                // Actualizar el ID seleccionado para el libro diario
                if (libroSeleccionado.getIdLibroDiario() != null) {
                    this.libroDiarioIdSeleccionado = libroSeleccionado.getIdLibroDiario().getId();
                } else {
                    this.libroDiarioIdSeleccionado = null;
                }

                LOG.log(Level.INFO, "Seleccionado libro mayor ID: {0}", libroSeleccionado.getId());
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al seleccionar nodo", ex);
        }
    }

    @Override
    protected LibroMayor nuevoRegistro() {
        LibroMayor libroMayor = new LibroMayor();
        return libroMayor;
    }

    @Override
    public InventarioDefaultDataAccess getDataAccess() {
        return libroMayorDAO;
    }

    @Override
    protected LibroMayor buscarRegistroPorId(Object id) {
        if (id instanceof Long) {
            Long buscado = (Long) id;
            if (this.modelo != null && this.modelo.getWrappedData() != null) {
                return this.modelo.getWrappedData().stream()
                        .filter(r -> r.getId().equals(buscado))
                        .findFirst()
                        .orElse(null);
            }
        }
        return null;
    }

    // MÉTODOS ADICIONALES PARA EL FORMULARIO

    /**
     * Método para crear un nuevo libro mayor
     */
    public String crearLibroMayor() {
        try {
            // Validación usando observación en lugar de nombre
            if (registro.getObservacion() == null || registro.getObservacion().isBlank()) {
                addMessage("Error", "La observación del libro mayor es obligatoria", true);
                return null;
            }

            libroMayorDAO.create(registro);

            this.estado = ESTADO_CRUD.NADA;

            addMessage("Éxito", "Libro mayor creado correctamente");

            // Recargar el árbol después de crear
            inicializarArbol();
            inicializar();

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al crear libro mayor", ex);
            addMessage("Error", "No se pudo crear el libro mayor: " + ex.getMessage(), true);
        }
        return null;
    }

    /**
     * Método para cancelar la creación/edición
     */
    public String cancelarCreacion() {
        try {
            this.estado = ESTADO_CRUD.NADA;
            this.registro = null;
            this.selectedNode = null;
            this.libroDiarioIdSeleccionado = null;
            addMessage("Operación cancelada", "La creación/edición del libro mayor ha sido cancelada");
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al cancelar operación", ex);
        }
        return null;
    }

    /**
     * Método para limpiar el formulario (solo en modo CREAR)
     */
    public String limpiarFormulario() {
        try {
            if (ESTADO_CRUD.CREAR.equals(this.estado)) {
                this.registro = nuevoRegistro();
                this.libroDiarioIdSeleccionado = null;
                addMessage("Formulario limpiado", "Todos los campos han sido restablecidos");
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al limpiar formulario", ex);
        }
        return null;
    }

    /**
     * Método para guardar cambios (edición)
     */
    public String guardarCambios() {
        try {
            // Validación usando observación en lugar de nombre
            if (registro.getObservacion() == null || registro.getObservacion().isBlank()) {
                addMessage("Error", "La observación del libro mayor es obligatoria", true);
                return null;
            }

            libroMayorDAO.edit(registro);

            this.estado = ESTADO_CRUD.NADA;
            this.selectedNode = null;

            addMessage("Éxito", "Libro mayor actualizado correctamente");

            // Recargar el árbol después de editar
            inicializarArbol();
            inicializar();

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al guardar cambios", ex);
            addMessage("Error", "No se pudieron guardar los cambios: " + ex.getMessage(), true);
        }
        return null;
    }

    /**
     * Centraliza la lógica de creación o edición
     */
    public String guardarOSalvarRegistro() {
        try {
            // Actualizar el LibroDiario antes de guardar
            actualizarLibroDiarioDesdeId();

            if (ESTADO_CRUD.CREAR.equals(this.estado)) {
                return crearLibroMayor();
            } else if (ESTADO_CRUD.MODIFICAR.equals(this.estado)) {
                return guardarCambios();
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error en guardarOSalvarRegistro", ex);
            addMessage("Error", "No se pudo guardar el registro: " + ex.getMessage(), true);
        }
        return null;
    }

    /**
     * Método para seleccionar registro desde dataTable (si lo necesitas)
     */
    public void seleccionarRegistro() {
        try {
            if (this.registro != null) {
                this.estado = ESTADO_CRUD.MODIFICAR;
                LOG.log(Level.INFO, "Seleccionado libro mayor con ID: {0}", registro.getId());
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al seleccionar registro", ex);
        }
    }

    // MÉTODOS PARA MENSAJES

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

    // GETTERS Y SETTERS

    public List<LibroDiario> getLibrosDiariosDisponibles() {
        return librosDiariosDisponibles;
    }

    public void setLibrosDiariosDisponibles(List<LibroDiario> librosDiariosDisponibles) {
        this.librosDiariosDisponibles = librosDiariosDisponibles;
    }

    // GETTERS Y SETTERS PARA EL treeTable

    public TreeNode getRoot() {
        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    // GETTER Y SETTER PARA libroDiarioIdSeleccionado

    public Long getLibroDiarioIdSeleccionado() {
        return libroDiarioIdSeleccionado;
    }

    public void setLibroDiarioIdSeleccionado(Long libroDiarioIdSeleccionado) {
        this.libroDiarioIdSeleccionado = libroDiarioIdSeleccionado;
    }

    /**
     * Método para actualizar el LibroDiario en el registro basado en el ID seleccionado
     */
    public void actualizarLibroDiarioDesdeId() {
        try {
            if (libroDiarioIdSeleccionado != null) {
                // Buscar el LibroDiario por ID
                LibroDiario libroDiario = libroDiarioDAO.findById(libroDiarioIdSeleccionado);
                if (libroDiario != null) {
                    registro.setIdLibroDiario(libroDiario);
                    LOG.log(Level.INFO, "LibroDiario actualizado: {0}", libroDiario.getNombre());
                } else {
                    registro.setIdLibroDiario(null);
                    LOG.log(Level.WARNING, "No se encontró LibroDiario con ID: {0}", libroDiarioIdSeleccionado);
                }
            } else {
                registro.setIdLibroDiario(null);
                LOG.log(Level.INFO, "LibroDiario establecido como null (Tipo Raíz)");
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al actualizar LibroDiario", ex);
        }
    }

    /**
     * Verifica si el libro mayor tiene detalles contables asociados
     */
    private boolean tieneDetallesAsociados(Long idLibroMayor) {
        try {
            // Aquí debes implementar la lógica para verificar si existen detalles
            // Por ejemplo, usando tu DetalleLibroMayorDAO
            // Esto es un ejemplo - adapta según tu implementación
            // return detalleLibroMayorDAO.existenDetallesPorLibroMayor(idLibroMayor);
            return false; // Temporalmente false para pruebas
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al verificar detalles asociados", ex);
            return true; // Por seguridad, asumir que sí tiene detalles si hay error
        }
    }

    /**
     * Método para eliminar un libro mayor con validaciones
     */
    public String eliminarLibroMayor() {
        try {
            if (this.registro == null || this.registro.getId() == null) {
                addMessage("Error", "No hay libro mayor seleccionado para eliminar", true);
                return null;
            }

            Long idAEliminar = this.registro.getId();
            String observacion = this.registro.getObservacion();

            // Verificar si hay detalles asociados antes de eliminar
            if (tieneDetallesAsociados(idAEliminar)) {
                addMessage("Error",
                        "No se puede eliminar el libro mayor '" + observacion +
                                "' porque tiene detalles contables asociados", true);
                return null;
            }

            // Realizar la eliminación
            libroMayorDAO.remove(registro);

            // Limpiar estado
            this.estado = ESTADO_CRUD.NADA;
            this.registro = null;
            this.selectedNode = null;
            this.libroDiarioIdSeleccionado = null;

            addMessage("Éxito", "Libro mayor '" + observacion + "' eliminado correctamente");

            // Recargar datos
            inicializarArbol();
            inicializar();

            LOG.log(Level.INFO, "Libro mayor eliminado - ID: {0}, Observación: {1}",
                    new Object[]{idAEliminar, observacion});

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al eliminar libro mayor con ID: " +
                    (this.registro != null ? this.registro.getId() : "null"), ex);
            addMessage("Error", "No se pudo eliminar el libro mayor: " + ex.getMessage(), true);
        }
        return null;
    }
}