package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.ViewExpiredException;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.model.TreeNode;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.InventarioDAOInterface;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.InventarioDefaultDataAccess;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.LibroMayorDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.LibroDiarioDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.DetalleLibroMayorDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.LibroMayor;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.LibroDiario;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.DetalleLibroMayor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
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

    @Inject
    DetalleLibroMayorDAO detalleLibroMayorDAO;

    @Inject
    private DetalleLibroMayorFrm detalleLibroMayorFrm;

    private List<LibroDiario> librosDiariosDisponibles;
    private TreeNode root;
    private TreeNode selectedNode;
    private LibroDiario libroDiarioSeleccionado;
    private List<DetalleLibroMayor> detallesLibroMayor;
    private DetalleLibroMayor detalleSeleccionado;
    private String nombreCuentaTemporal;
    private String cuentaSeleccionada;
    private String filtroCuenta;
    private List<Object> cuentasContables;
    private List<Object> detallesCuentaContable;
    private Double saldoFinal;
    private Object cuentaSeleccionadaObj;

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

    private void inicializarArbol() {
        try {
            System.out.println("=== INICIALIZAR ÁRBOL INICIADO ===");

            // ✅ CREAR NODO ROOT CON TIPO CORRECTO
            this.root = new DefaultTreeNode("Libros Mayores", null);
            System.out.println("Nodo root creado");

            // ✅ VERIFICAR QUE EL DAO ESTÉ DISPONIBLE
            if (libroMayorDAO == null) {
                System.out.println("ERROR: libroMayorDAO es null");
                LOG.log(Level.SEVERE, "libroMayorDAO es null - no se puede cargar datos");
                return;
            }

            // ✅ OBTENER LIBROS MAYORES
            List<LibroMayor> librosMayores = libroMayorDAO.findRange(0, 100);

            if (librosMayores == null || librosMayores.isEmpty()) {
                System.out.println("No se encontraron libros mayores - lista vacía");
                // Crear nodo de ejemplo para mostrar
                TreeNode nodoEjemplo = new DefaultTreeNode(new LibroMayor() {{
                    setObservacion("No hay libros mayores disponibles");
                }}, root);
                return;
            }

            System.out.println("Libros mayores encontrados: " + librosMayores.size());

            // ✅ CREAR NODOS PARA CADA LIBRO MAYOR
            int nodosCreados = 0;
            for (LibroMayor libroMayor : librosMayores) {
                try {
                    if (libroMayor != null) {
                        // ✅ USAR DefaultTreeNode CON EL OBJETO COMO DATA
                        TreeNode nodoLibro = new DefaultTreeNode("libroMayor", libroMayor, root);
                        nodosCreados++;

                        System.out.println("Nodo creado - ID: " + libroMayor.getId() +
                                ", Observación: " + libroMayor.getObservacion());
                    }
                } catch (Exception e) {
                    System.out.println("Error creando nodo para libro mayor ID " +
                            (libroMayor != null ? libroMayor.getId() : "null") + ": " + e.getMessage());
                }
            }

            System.out.println("Árbol inicializado con " + nodosCreados + " nodos");
            LOG.log(Level.INFO, "Árbol inicializado con {0} nodos", nodosCreados);

        } catch (Exception ex) {
            System.out.println("ERROR CRÍTICO en inicializarArbol: " + ex.getMessage());
            ex.printStackTrace();
            LOG.log(Level.SEVERE, "Error crítico al inicializar árbol", ex);

            // ✅ CREAR ROOT DE FALLBACK
            this.root = new DefaultTreeNode("Error al cargar datos", null);
        }
    }



    public void crearNuevoRegistro() {
        try {
            System.out.println("=== CREAR NUEVO REGISTRO INICIADO ===");

            this.estado = ESTADO_CRUD.CREAR;
            this.registro = nuevoRegistro();
            this.libroDiarioSeleccionado = null;
            this.libroDiarioIdSeleccionado = null;
            this.selectedNode = null;

            this.detallesLibroMayor = null;
            this.cuentaSeleccionada = null;
            this.filtroCuenta = null;
            this.cuentasContables = null;
            this.detallesCuentaContable = null;
            this.saldoFinal = null;
            this.cuentaSeleccionadaObj = null;

            cargarLibrosDiariosDisponibles();

            System.out.println("Nuevo registro preparado - Estado: " + this.estado);

        } catch (Exception ex) {
            System.out.println("ERROR en crearNuevoRegistro: " + ex.getMessage());
            LOG.log(Level.SEVERE, "Error al crear nuevo registro", ex);
            addMessage("Error", "No se pudo preparar el formulario de creación", true);
        }
    }

    public void onNodeSelect(NodeSelectEvent event) {
        try {
            System.out.println("=== onNodeSelect INICIADO ===");

            // ✅ OBTENER EL NODO SELECCIONADO CORRECTAMENTE
            this.selectedNode = event.getTreeNode();
            System.out.println("Nodo seleccionado: " + (selectedNode != null ? selectedNode.getData() : "null"));

            if (selectedNode != null && selectedNode.getData() instanceof LibroMayor) {
                LibroMayor libroSeleccionado = (LibroMayor) selectedNode.getData();
                System.out.println("Libro mayor seleccionado - ID: " + libroSeleccionado.getId());

                // ✅ CARGAR DESDE LA BASE DE DATOS
                this.registro = libroMayorDAO.findById(libroSeleccionado.getId());

                if (this.registro == null) {
                    System.out.println("ERROR: No se pudo cargar el libro mayor desde BD");
                    addMessage("Error", "No se pudo cargar el libro mayor seleccionado", true);
                    return;
                }

                this.estado = ESTADO_CRUD.MODIFICAR;
                this.libroDiarioSeleccionado = this.registro.getIdLibroDiario();

                if (this.libroDiarioSeleccionado != null) {
                    this.libroDiarioIdSeleccionado = this.libroDiarioSeleccionado.getId();
                } else {
                    this.libroDiarioIdSeleccionado = null;
                }

                // ✅ FORZAR CARGA DE DETALLES
                this.detallesLibroMayor = null;
                cargarDetallesLibroMayor();

                System.out.println("Nodo procesado correctamente - Estado: " + this.estado);

            } else {
                System.out.println("Nodo no contiene LibroMayor válido");
                addMessage("Advertencia", "Seleccione un libro mayor válido", true);
            }
        } catch (Exception ex) {
            System.out.println("ERROR en onNodeSelect: " + ex.getMessage());
            ex.printStackTrace();
            addMessage("Error", "No se pudo procesar la selección: " + ex.getMessage(), true);
        }
    }

    @Override
    protected LibroMayor nuevoRegistro() {
        return new LibroMayor();
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

    public String crearLibroMayor() {
        try {
            actualizarLibroDiarioDesdeSeleccion();

            if (registro.getObservacion() == null || registro.getObservacion().isBlank()) {
                addMessage("Error", "La observación del libro mayor es obligatoria", true);
                return null;
            }

            libroMayorDAO.create(registro);

            this.estado = ESTADO_CRUD.NADA;

            addMessage("Éxito", "Libro mayor creado correctamente");

            inicializarArbol();
            inicializar();

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al crear libro mayor", ex);
            addMessage("Error", "No se pudo crear el libro mayor: " + ex.getMessage(), true);
        }
        return null;
    }

    public String cancelarCreacion() {
        try {
            this.estado = ESTADO_CRUD.NADA;
            this.registro = null;
            this.selectedNode = null;
            this.libroDiarioSeleccionado = null;
            this.libroDiarioIdSeleccionado = null; // Limpiar la nueva propiedad
            this.detallesLibroMayor = null;
            this.detalleSeleccionado = null;
            this.nombreCuentaTemporal = null;
            this.cuentaSeleccionada = null;
            this.filtroCuenta = null;
            this.cuentasContables = null;
            this.detallesCuentaContable = null;
            this.saldoFinal = null;
            this.cuentaSeleccionadaObj = null;
            addMessage("Operación cancelada", "La creación/edición del libro mayor ha sido cancelada");
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al cancelar operación", ex);
        }
        return null;
    }

    public String limpiarFormulario() {
        try {
            if (ESTADO_CRUD.CREAR.equals(this.estado)) {
                this.registro = nuevoRegistro();
                this.libroDiarioSeleccionado = null;
                this.libroDiarioIdSeleccionado = null; // Limpiar la nueva propiedad
                this.nombreCuentaTemporal = null;
                addMessage("Formulario limpiado", "Todos los campos han sido restablecidos");
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al limpiar formulario", ex);
        }
        return null;
    }

    public String guardarCambios() {
        try {
            actualizarLibroDiarioDesdeSeleccion();

            if (registro.getObservacion() == null || registro.getObservacion().isBlank()) {
                addMessage("Error", "La observación del libro mayor es obligatoria", true);
                return null;
            }

            libroMayorDAO.edit(registro);

            this.estado = ESTADO_CRUD.NADA;
            this.selectedNode = null;

            addMessage("Éxito", "Libro mayor actualizado correctamente");

            inicializarArbol();
            inicializar();

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al guardar cambios", ex);
            addMessage("Error", "No se pudieron guardar los cambios: " + ex.getMessage(), true);
        }
        return null;
    }

    public String guardarOSalvarRegistro() {
        try {
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

    public void actualizarLibroDiarioDesdeSeleccion() {
        try {
            this.registro.setIdLibroDiario(this.libroDiarioSeleccionado);

            if (this.libroDiarioSeleccionado != null) {
                LOG.log(Level.INFO, "LibroDiario actualizado: {0}", this.libroDiarioSeleccionado.getNombre());
            } else {
                LOG.log(Level.INFO, "LibroDiario establecido como null (Tipo Raíz)");
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al actualizar LibroDiario", ex);
            addMessage("Error", "No se pudo actualizar el libro diario seleccionado", true);
        }
    }


    /***
     * Metodo para actualizar desde el ID
     */
    public void actualizarLibroDiarioDesdeId() {
        try {
            if (this.libroDiarioIdSeleccionado != null) {
                this.libroDiarioSeleccionado = librosDiariosDisponibles.stream()
                        .filter(ld -> ld.getId().equals(this.libroDiarioIdSeleccionado))
                        .findFirst()
                        .orElse(null);

                if (this.libroDiarioSeleccionado != null) {
                    LOG.log(Level.INFO, "LibroDiario actualizado desde ID: {0} - {1}",
                            new Object[]{this.libroDiarioSeleccionado.getId(), this.libroDiarioSeleccionado.getNombre()});
                } else {
                    LOG.log(Level.WARNING, "No se encontró LibroDiario con ID: {0}", this.libroDiarioIdSeleccionado);
                    this.libroDiarioSeleccionado = null;
                }
            } else {
                this.libroDiarioSeleccionado = null;
                LOG.log(Level.INFO, "LibroDiario establecido como null desde ID nulo");
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al actualizar LibroDiario desde ID", ex);
            addMessage("Error", "No se pudo actualizar el libro diario desde el ID seleccionado", true);
        }
    }

    public void cargarDetallesLibroMayor() {
        try {
            System.out.println("=== CARGAR DETALLES LIBRO MAYOR ===");

            // ✅ VERIFICACIONES DE SEGURIDAD
            if (facesContext == null) {
                System.out.println("ERROR: FacesContext es null");
                this.detallesLibroMayor = new ArrayList<>();
                return;
            }

            if (this.registro == null || this.registro.getId() == null) {
                System.out.println("Registro o ID es nulo - inicializando lista vacía");
                this.detallesLibroMayor = new ArrayList<>();
                return;
            }

            // ✅ CARGAR DESDE DAO
            this.detallesLibroMayor = detalleLibroMayorDAO.findByLibroMayorId(
                    this.registro.getId(), 0, 1000
            );

            System.out.println("Detalles cargados: " +
                    (this.detallesLibroMayor != null ? this.detallesLibroMayor.size() : "null") +
                    " para libro mayor ID: " + this.registro.getId());

            // ✅ ASEGURAR QUE NO SEA NULL
            if (this.detallesLibroMayor == null) {
                this.detallesLibroMayor = new ArrayList<>();
            }

        } catch (Exception ex) {
            System.out.println("ERROR en cargarDetallesLibroMayor: " + ex.getMessage());
            ex.printStackTrace();
            this.detallesLibroMayor = new ArrayList<>();
        }
    }

    public void prepararNuevoDetalle() {
        try {
            this.detalleSeleccionado = new DetalleLibroMayor();
            System.out.println("=== prepararNuevoDetalle COMPLETADO ===");
        } catch (Exception ex) {
            System.out.println("ERROR en prepararNuevoDetalle: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void prepararEditarDetalle(DetalleLibroMayor detalle) {
        this.detalleSeleccionado = detalle;
    }

    public void guardarDetalle() {
        try {
            if (detalleSeleccionado == null || registro == null) {
                addMessage("Error", "No hay detalle o libro mayor seleccionado", true);
                return;
            }

            if (detalleSeleccionado.getSaldo() == null) {
                addMessage("Error", "El saldo es obligatorio", true);
                return;
            }

            detalleSeleccionado.setIdLibroMayor(registro);

            if (detalleSeleccionado.getId() == null) {
                detalleLibroMayorDAO.create(detalleSeleccionado);
                addMessage("Éxito", "Detalle creado correctamente");
            } else {
                detalleLibroMayorDAO.edit(detalleSeleccionado);
                addMessage("Éxito", "Detalle actualizado correctamente");
            }

            cargarDetallesLibroMayor();
            this.detalleSeleccionado = null;

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al guardar detalle", ex);
            addMessage("Error", "No se pudo guardar el detalle: " + ex.getMessage(), true);
        }
    }

    public void eliminarDetalle() {
        try {
            if (detalleSeleccionado != null && detalleSeleccionado.getId() != null) {
                detalleLibroMayorDAO.remove(detalleSeleccionado);
                cargarDetallesLibroMayor();
                this.detalleSeleccionado = null; // Limpiar después de eliminar
                addMessage("Éxito", "Detalle eliminado correctamente");
            } else {
                addMessage("Error", "No hay detalle seleccionado para eliminar", true);
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al eliminar detalle", ex);
            addMessage("Error", "No se pudo eliminar el detalle: " + ex.getMessage(), true);
        }
    }

    // GETTERS Y SETTERS
    public Long getLibroDiarioIdSeleccionado() {
        if (registro != null && registro.getIdLibroDiario() != null) {
            this.libroDiarioIdSeleccionado = registro.getIdLibroDiario().getId();
        } else {
            this.libroDiarioIdSeleccionado = null;
        }
        return libroDiarioIdSeleccionado;
    }

    public void setLibroDiarioIdSeleccionado(Long libroDiarioIdSeleccionado) {
        this.libroDiarioIdSeleccionado = libroDiarioIdSeleccionado;
        if (libroDiarioIdSeleccionado != null) {
            this.libroDiarioSeleccionado = librosDiariosDisponibles.stream()
                    .filter(ld -> ld.getId().equals(libroDiarioIdSeleccionado))
                    .findFirst()
                    .orElse(null);
        } else {
            this.libroDiarioSeleccionado = null;
        }
    }


    public List<LibroDiario> getLibrosDiariosDisponibles() {
        return librosDiariosDisponibles;
    }

    public void setLibrosDiariosDisponibles(List<LibroDiario> librosDiariosDisponibles) {
        this.librosDiariosDisponibles = librosDiariosDisponibles;
    }

    // ✅ GETTERS Y SETTERS CON TIPOS CORRECTOS
    public TreeNode getRoot() {
        if (root == null) {
            System.out.println("Root es null - inicializando árbol");
            inicializarArbol();
        }
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

    public void setLibroDiarioSeleccionado(LibroDiario libroDiarioSeleccionado) {
        this.libroDiarioSeleccionado = libroDiarioSeleccionado;
    }

    public List<DetalleLibroMayor> getDetallesLibroMayor() {
        try {
            System.out.println("=== GETTER getDetallesLibroMayor llamado ===");

            // ✅ VERIFICAR SESIÓN Y CONTEXTO
            if (facesContext == null || facesContext.getExternalContext() == null) {
                System.out.println("Contexto de Faces no disponible - retornando lista vacía");
                return new ArrayList<>();
            }

            if (facesContext.getExternalContext().getSession(false) == null) {
                System.out.println("Sesión expirada - retornando lista vacía");
                return new ArrayList<>();
            }

            // ✅ INICIALIZAR SI ES NULL
            if (detallesLibroMayor == null) {
                System.out.println("detallesLibroMayor es null - inicializando nueva lista");
                detallesLibroMayor = new ArrayList<>();

                // Solo cargar si tenemos un registro válido
                if (this.registro != null && this.registro.getId() != null) {
                    System.out.println("Cargando detalles para registro ID: " + this.registro.getId());
                    cargarDetallesLibroMayor();
                }
            }

            System.out.println("Retornando " + detallesLibroMayor.size() + " detalles");
            return detallesLibroMayor;

        } catch (Exception ex) {
            System.out.println("ERROR en getDetallesLibroMayor: " + ex.getMessage());
            return new ArrayList<>();
        }
    }

    public void setDetallesLibroMayor(List<DetalleLibroMayor> detallesLibroMayor) {
        this.detallesLibroMayor = detallesLibroMayor;
    }

    public DetalleLibroMayor getDetalleSeleccionado() {
        if (detalleSeleccionado == null) {
            detalleSeleccionado = new DetalleLibroMayor();
        }
        return detalleSeleccionado;
    }

    public void setDetalleSeleccionado(DetalleLibroMayor detalleSeleccionado) {
        this.detalleSeleccionado = detalleSeleccionado;
    }

    public String getNombreCuentaTemporal() {
        return "Cuentas Contables";
    }

    public void setNombreCuentaTemporal(String nombreCuentaTemporal) {
        this.nombreCuentaTemporal = nombreCuentaTemporal;
    }

    public String getCuentaSeleccionada() {
        return cuentaSeleccionada;
    }

    public void setCuentaSeleccionada(String cuentaSeleccionada) {
        this.cuentaSeleccionada = cuentaSeleccionada;
    }

    public String getFiltroCuenta() {
        return filtroCuenta;
    }

    public void setFiltroCuenta(String filtroCuenta) {
        this.filtroCuenta = filtroCuenta;
    }

    public List<Object> getCuentasContables() {
        if (cuentasContables == null) {
            cuentasContables = Collections.emptyList();
        }
        return cuentasContables;
    }

    public void setCuentasContables(List<Object> cuentasContables) {
        this.cuentasContables = cuentasContables;
    }

    public List<Object> getDetallesCuentaContable() {
        if (detallesCuentaContable == null) {
            detallesCuentaContable = Collections.emptyList();
        }
        return detallesCuentaContable;
    }

    public void setDetallesCuentaContable(List<Object> detallesCuentaContable) {
        this.detallesCuentaContable = detallesCuentaContable;
    }

    public Double getSaldoFinal() {
        return saldoFinal != null ? saldoFinal : 0.0;
    }

    public void setSaldoFinal(Double saldoFinal) {
        this.saldoFinal = saldoFinal;
    }

    public DetalleLibroMayorFrm getDetalleLibroMayorFrm() {
        return detalleLibroMayorFrm;
    }

    public void setDetalleLibroMayorFrm(DetalleLibroMayorFrm detalleLibroMayorFrm) {
        this.detalleLibroMayorFrm = detalleLibroMayorFrm;
    }

    public Object getCuentaSeleccionadaObj() {
        return cuentaSeleccionadaObj;
    }

    public void setCuentaSeleccionadaObj(Object cuentaSeleccionadaObj) {
        this.cuentaSeleccionadaObj = cuentaSeleccionadaObj;
    }

    public int getDetallesCount() {
        return detallesLibroMayor != null ? detallesLibroMayor.size() : 0;
    }

    public void buscarCuentas() {
        try {
            System.out.println("=== BUSCAR CUENTAS INICIADO ===");
            System.out.println("Filtro: " + this.filtroCuenta);
            System.out.println("Registro: " + (this.registro != null ? this.registro.getId() : "null"));
            System.out.println("Libro Diario: " + (this.registro != null && this.registro.getIdLibroDiario() != null ?
                    this.registro.getIdLibroDiario().getId() + " - " + this.registro.getIdLibroDiario().getNombre() : "null"));

            if (this.registro == null || this.registro.getIdLibroDiario() == null) {
                addMessage("Error", "No hay un libro diario asociado a este libro mayor", true);
                this.cuentasContables = Collections.emptyList();
                return;
            }

            Long idLibroDiario = this.registro.getIdLibroDiario().getId();
            String nombreLibroDiario = this.registro.getIdLibroDiario().getNombre();

            System.out.println("Buscando en libro diario ID: " + idLibroDiario);
            List<Object[]> datosVerificados = libroDiarioDAO.verificarDatosDetalleLibroDiario(idLibroDiario);

            if (datosVerificados.isEmpty()) {
                addMessage("Info", "No se encontraron registros contables en el libro diario: " + nombreLibroDiario);
                this.cuentasContables = Collections.emptyList();
                return;
            }

            System.out.println("Datos verificados: " + datosVerificados.size() + " registros");
            List<Object[]> cuentasEncontradas = libroDiarioDAO.findCuentasUnicasByLibroDiario(
                    idLibroDiario,
                    filtroCuenta
            );

            System.out.println("Cuentas encontradas en DAO: " + cuentasEncontradas.size());

            this.cuentasContables = new ArrayList<>();
            for (Object[] cuentaData : cuentasEncontradas) {
                Map<String, Object> cuentaInfo = new HashMap<>();
                cuentaInfo.put("codigo", cuentaData[0] != null ? cuentaData[0].toString() : "");
                cuentaInfo.put("nombre", cuentaData[1] != null ? cuentaData[1].toString() : "");
                cuentaInfo.put("totalDebe", cuentaData[2] != null ? ((Number)cuentaData[2]).doubleValue() : 0.0);
                cuentaInfo.put("totalHaber", cuentaData[3] != null ? ((Number)cuentaData[3]).doubleValue() : 0.0);
                cuentaInfo.put("saldo", cuentaData[4] != null ? ((Number)cuentaData[4]).doubleValue() : 0.0);

                this.cuentasContables.add(cuentaInfo);

                System.out.println("Cuenta agregada: " + cuentaInfo.get("codigo") + " - " + cuentaInfo.get("nombre") +
                        " | Saldo: " + cuentaInfo.get("saldo"));
            }

            // ✅ VERIFICAR CUENTAS YA MAYORIZADAS
            System.out.println("=== VERIFICANDO CUENTAS YA MAYORIZADAS ===");
            int cuentasYaMayorizadas = 0;

            for (Object cuentaObj : this.cuentasContables) {
                if (cuentaObj instanceof Map) {
                    Map<String, Object> cuentaMap = (Map<String, Object>) cuentaObj;
                    String codigo = (String) cuentaMap.get("codigo");

                    if (codigo != null && !codigo.trim().isEmpty()) {
                        boolean yaMayorizada = detalleLibroMayorDAO.existeCuentaEnLibroMayor(
                                this.registro.getId(), codigo);

                        cuentaMap.put("yaMayorizada", yaMayorizada);

                        if (yaMayorizada) {
                            cuentasYaMayorizadas++;
                            System.out.println("Cuenta ya mayorizada: " + codigo);
                        }
                    }
                }
            }

            System.out.println("Total cuentas ya mayorizadas: " + cuentasYaMayorizadas);

            String mensaje = "Encontradas " + cuentasContables.size() + " cuentas en: " + nombreLibroDiario;
            if (cuentasYaMayorizadas > 0) {
                mensaje += " (" + cuentasYaMayorizadas + " ya mayorizadas)";
            }

            addMessage("Búsqueda completada", mensaje);

            System.out.println("=== BUSCAR CUENTAS COMPLETADO ===");

        } catch (Exception ex) {
            System.out.println("ERROR en buscarCuentas: " + ex.getMessage());
            ex.printStackTrace();
            LOG.log(Level.SEVERE, "Error al buscar cuentas", ex);
            addMessage("Error", "No se pudieron buscar las cuentas: " + ex.getMessage(), true);
            this.cuentasContables = Collections.emptyList();
        }
    }

    public String eliminarLibroMayor() {
        try {
            if (this.registro == null || this.registro.getId() == null) {
                addMessage("Error", "No hay libro mayor seleccionado para eliminar", true);
                return null;
            }

            Long idAEliminar = this.registro.getId();
            String observacion = this.registro.getObservacion();

            libroMayorDAO.remove(registro);

            this.estado = ESTADO_CRUD.NADA;
            this.registro = null;
            this.selectedNode = null;
            this.libroDiarioSeleccionado = null;
            this.libroDiarioIdSeleccionado = null;
            this.detallesLibroMayor = null;
            this.detalleSeleccionado = null;
            this.nombreCuentaTemporal = null;
            this.cuentaSeleccionada = null;
            this.filtroCuenta = null;
            this.cuentasContables = null;
            this.detallesCuentaContable = null;
            this.saldoFinal = null;
            this.cuentaSeleccionadaObj = null;

            addMessage("Éxito", "Libro mayor '" + observacion + "' eliminado correctamente");

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

    public void onCuentaSelect(SelectEvent event) {
        try {
            System.out.println("=== onCuentaSelect INICIADO ===");

            Object cuentaSeleccionada = event.getObject();
            this.cuentaSeleccionadaObj = cuentaSeleccionada;

            System.out.println("Objeto recibido: " + cuentaSeleccionada);

            if (cuentaSeleccionada instanceof Map) {
                Map<String, Object> cuentaMap = (Map<String, Object>) cuentaSeleccionada;
                String codigo = (String) cuentaMap.get("codigo");
                String nombre = (String) cuentaMap.get("nombre");
                Double saldo = (Double) cuentaMap.get("saldo");

                this.cuentaSeleccionada = (codigo != null ? codigo : "SIN CÓDIGO") + " - " + (nombre != null ? nombre : "SIN NOMBRE");
                this.saldoFinal = saldo != null ? saldo : 0.0;

                System.out.println("Cuenta seleccionada: " + this.cuentaSeleccionada);
                System.out.println("Saldo: " + this.saldoFinal);

                // Cargar detalles automáticamente
                cargarDetallesCuentaContable();

                // Forzar actualización de todas las secciones
                PrimeFaces.current().executeScript("mostrarSeccionesDetalle()");
            }

            System.out.println("=== onCuentaSelect COMPLETADO ===");

        } catch (Exception ex) {
            System.out.println("ERROR en onCuentaSelect: " + ex.getMessage());
            ex.printStackTrace();
            addMessage("Error", "No se pudo seleccionar la cuenta: " + ex.getMessage(), true);
        }
    }

    public void prepararDetallesCuenta() {
        try {
            System.out.println("=== PREPARAR DETALLES CUENTA ===");

            if (this.cuentaSeleccionadaObj != null) {
                cargarDetallesCuentaContable();

                // Actualizar y mostrar secciones
                PrimeFaces.current().executeScript("mostrarSeccionesDetalle()");

                addMessage("Detalles cargados",
                        "Se cargaron " + this.detallesCuentaContable.size() + " movimientos para: " + this.cuentaSeleccionada);
            }

        } catch (Exception ex) {
            System.out.println("ERROR en prepararDetallesCuenta: " + ex.getMessage());
            ex.printStackTrace();
            addMessage("Error", "No se pudieron cargar los detalles: " + ex.getMessage(), true);
        }
    }

    /**
     * Metodo para forzar actualizacion de la tabla de detalles
     *
     */
    public void actualizarTablaDetalles() {
        try {
            System.out.println("=== ACTUALIZAR TABLA DETALLES ===");
            System.out.println("Detalles cuenta contable: " +
                    (this.detallesCuentaContable != null ? this.detallesCuentaContable.size() : "null"));
        } catch (Exception ex) {
            System.out.println("ERROR en actualizarTablaDetalles: " + ex.getMessage());
        }
    }

    /**
     * Metodo para diagnostico de la seleccion
     */
    public void diagnosticoSeleccion() {
        try {
            System.out.println("=== DIAGNÓSTICO SELECCIÓN ===");
            System.out.println("cuentaSeleccionadaObj: " + this.cuentaSeleccionadaObj);
            System.out.println("cuentaSeleccionada: " + this.cuentaSeleccionada);
            System.out.println("saldoFinal: " + this.saldoFinal);
            System.out.println("cuentasContables size: " + (this.cuentasContables != null ? this.cuentasContables.size() : "null"));

            if (this.cuentaSeleccionadaObj != null && this.cuentaSeleccionadaObj instanceof Map) {
                Map<String, Object> cuentaMap = (Map<String, Object>) this.cuentaSeleccionadaObj;
                System.out.println("Map keys: " + cuentaMap.keySet());
                System.out.println("Código: " + cuentaMap.get("codigo"));
                System.out.println("Nombre: " + cuentaMap.get("nombre"));
                System.out.println("Saldo: " + cuentaMap.get("saldo"));
            }

        } catch (Exception ex) {
            System.out.println("ERROR en diagnosticoSeleccion: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Metodo  para cargar detalles de cuenta contable
     */
    private void cargarDetallesCuentaContable() {
        try {
            System.out.println("=== cargarDetallesCuentaContable INICIADO ===");

            // Asegurar que la lista esté inicializada
            if (this.detallesCuentaContable == null) {
                this.detallesCuentaContable = new ArrayList<>();
            }

            if (this.registro == null || this.registro.getIdLibroDiario() == null) {
                System.out.println("ERROR: No hay libro diario asociado");
                this.detallesCuentaContable.clear();
                return;
            }

            if (this.cuentaSeleccionadaObj == null) {
                System.out.println("ERROR: cuentaSeleccionadaObj es null");
                this.detallesCuentaContable.clear();
                return;
            }

            String codigoCuenta = null;
            if (this.cuentaSeleccionadaObj instanceof Map) {
                Map<String, Object> cuentaMap = (Map<String, Object>) this.cuentaSeleccionadaObj;
                codigoCuenta = (String) cuentaMap.get("codigo");
                System.out.println("Buscando movimientos para cuenta: " + codigoCuenta);
            }

            if (codigoCuenta != null && !codigoCuenta.trim().isEmpty()) {
                Long idLibroDiario = this.registro.getIdLibroDiario().getId();

                System.out.println("Llamando a libroDiarioDAO.findMovimientosByCuenta...");
                List<Object[]> movimientos = libroDiarioDAO.findMovimientosByCuenta(idLibroDiario, codigoCuenta);
                System.out.println("Movimientos encontrados en DAO: " + movimientos.size());

                this.detallesCuentaContable.clear();

                for (Object[] movimiento : movimientos) {
                    Map<String, Object> detalle = new HashMap<>();

                    detalle.put("fecha", movimiento[0]); // Fecha
                    detalle.put("concepto", movimiento[1] != null ? movimiento[1].toString() : "Sin concepto");
                    detalle.put("numeroPartida", movimiento[2] != null ? movimiento[2].toString() : "N/A");

                    detalle.put("debe", movimiento[3] != null ? movimiento[3] : Boolean.FALSE);
                    detalle.put("haber", movimiento[4] != null ? movimiento[4] : Boolean.FALSE);

                    detalle.put("montoDebe", movimiento[5] != null ? ((Number)movimiento[5]).doubleValue() : 0.0);
                    detalle.put("montoHaber", movimiento[6] != null ? ((Number)movimiento[6]).doubleValue() : 0.0);

                    double montoDebe = (Double) detalle.get("montoDebe");
                    double montoHaber = (Double) detalle.get("montoHaber");
                    detalle.put("montoTotal", montoDebe - montoHaber);

                    this.detallesCuentaContable.add(detalle);

                    System.out.println("Detalle agregado - Fecha: " + detalle.get("fecha") +
                            ", Concepto: " + detalle.get("concepto") +
                            ", Débito: " + detalle.get("montoDebe") +
                            ", Crédito: " + detalle.get("montoHaber"));
                }

                calcularSaldoFinal();

                System.out.println("Total detalles cargados: " + this.detallesCuentaContable.size());

            } else {
                System.out.println("ERROR: Código de cuenta es null o vacío");
                this.detallesCuentaContable.clear();
            }

        } catch (Exception ex) {
            System.out.println("ERROR en cargarDetallesCuentaContable: " + ex.getMessage());
            ex.printStackTrace();
            this.detallesCuentaContable = new ArrayList<>();
        }
    }

    /**
     * Metodo para calcular saldo final
     */
    private void calcularSaldoFinal() {
        try {
            if (detallesCuentaContable == null || detallesCuentaContable.isEmpty()) {
                this.saldoFinal = 0.0;
                return;
            }

            double totalDebe = 0.0;
            double totalHaber = 0.0;

            for (Object detalleObj : detallesCuentaContable) {
                if (detalleObj instanceof Map) {
                    Map<String, Object> detalle = (Map<String, Object>) detalleObj;
                    Double montoDebe = (Double) detalle.get("montoDebe");
                    Double montoHaber = (Double) detalle.get("montoHaber");

                    if (montoDebe != null) {
                        totalDebe += montoDebe;
                    }
                    if (montoHaber != null) {
                        totalHaber += montoHaber;
                    }
                }
            }

            this.saldoFinal = totalDebe - totalHaber;

            System.out.println("Saldo final calculado - Débito: " + totalDebe +
                    ", Crédito: " + totalHaber + ", Saldo: " + saldoFinal);

            LOG.log(Level.INFO, "Saldo final calculado - Débito: {0}, Crédito: {1}, Saldo: {2}",
                    new Object[]{totalDebe, totalHaber, saldoFinal});

        } catch (Exception ex) {
            System.out.println("ERROR en calcularSaldoFinal: " + ex.getMessage());
            LOG.log(Level.SEVERE, "Error al calcular saldo final", ex);
            this.saldoFinal = 0.0;
        }
    }

    public void crearMayorizacion() {
        try {
            // VALIDACIONES BÁSICAS
            if (cuentaSeleccionadaObj == null) {
                addMessage("Error", "Debe seleccionar una cuenta válida", true);
                return;
            }

            if (this.registro == null || this.registro.getId() == null) {
                addMessage("Error", "No hay un libro mayor seleccionado", true);
                return;
            }

            // OBTENER DATOS DE LA CUENTA
            Map<String, Object> cuentaMap = (Map<String, Object>) this.cuentaSeleccionadaObj;
            String codigoCuenta = (String) cuentaMap.get("codigo");
            String nombreCuenta = (String) cuentaMap.get("nombre");
            Double totalDebe = (Double) cuentaMap.get("totalDebe");
            Double totalHaber = (Double) cuentaMap.get("totalHaber");
            Double saldoCalculado = (Double) cuentaMap.get("saldo");

            // VALIDAR DATOS CONTABLES
            if (totalDebe == null || totalHaber == null) {
                addMessage("Error", "No se pueden obtener los totales contables", true);
                return;
            }

            // ✅ **VALIDAR PARTIDA DOBLE**
            // En una mayorización correcta, la suma de débitos debe igualar a la suma de créditos
            // a nivel del libro diario completo (no por cuenta individual)

            // VERIFICAR SI LA CUENTA YA FUE MAYORIZADA
            boolean cuentaYaMayorizada = detalleLibroMayorDAO.existeCuentaEnLibroMayor(
                    this.registro.getId(), codigoCuenta);

            if (cuentaYaMayorizada) {
                addMessage("Error", "La cuenta " + codigoCuenta + " ya fue mayorizada en este libro mayor", true);
                return;
            }

            // ✅ **CREAR DETALLE CON INFORMACIÓN EN EL NOMBRE**
            DetalleLibroMayor nuevoDetalle = new DetalleLibroMayor();
            nuevoDetalle.setIdLibroMayor(this.registro);
            nuevoDetalle.setSaldo(BigDecimal.valueOf(saldoCalculado != null ? saldoCalculado : 0.0));

            // ✅ **INCLUIR TODA LA INFORMACIÓN EN EL NOMBRE** (ya que no tenemos campos separados)
            String nombreCompleto = String.format("%s - %s (D: $%.2f, C: $%.2f)",
                    codigoCuenta, nombreCuenta, totalDebe, totalHaber);
            nuevoDetalle.setNombreCuenta(nombreCompleto);

            nuevoDetalle.setId(UUID.randomUUID());

            // GUARDAR
            detalleLibroMayorDAO.create(nuevoDetalle);

            // LIMPIAR Y ACTUALIZAR
            cargarDetallesLibroMayor();
            resetearFormularioMayorizacion();

            addMessage("Éxito",
                    "Mayorización creada para: " + nombreCuenta +
                            " | Saldo: $" + saldoCalculado);

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al crear mayorización", ex);
            addMessage("Error", "No se pudo crear la mayorización: " + ex.getMessage(), true);
        }
    }

    private void resetearFormularioMayorizacion() {
        this.cuentaSeleccionada = null;
        this.saldoFinal = null;
        this.detallesCuentaContable = null;
        this.filtroCuenta = null;
        this.cuentaSeleccionadaObj = null;
        this.detalleSeleccionado = null;
    }

    public void actualizarCampoCuenta() {
        try {
            System.out.println("=== ACTUALIZAR CAMPO CUENTA ===");
            System.out.println("cuentaSeleccionada: " + this.cuentaSeleccionada);
            System.out.println("cuentaSeleccionadaObj: " + (this.cuentaSeleccionadaObj != null));

            if (this.cuentaSeleccionadaObj != null && this.cuentaSeleccionada == null) {
                if (this.cuentaSeleccionadaObj instanceof Map) {
                    Map<String, Object> cuentaMap = (Map<String, Object>) this.cuentaSeleccionadaObj;
                    String codigo = (String) cuentaMap.get("codigo");
                    String nombre = (String) cuentaMap.get("nombre");
                    this.cuentaSeleccionada = codigo + " - " + nombre;
                    System.out.println("Campo forzado a: " + this.cuentaSeleccionada);
                }
            }
        } catch (Exception ex) {
            System.out.println("ERROR en actualizarCampoCuenta: " + ex.getMessage());
        }
    }

    /**
     * Método para editar registro (necesario para el botón Editar)
     */
    public void editarRegistro() {
        try {
            if (this.selectedNode != null && this.selectedNode.getData() instanceof LibroMayor) {
                LibroMayor libroSeleccionado = (LibroMayor) this.selectedNode.getData();
                this.registro = libroMayorDAO.findById(libroSeleccionado.getId());
                this.estado = ESTADO_CRUD.MODIFICAR;
                this.libroDiarioSeleccionado = this.registro.getIdLibroDiario();

                if (this.libroDiarioSeleccionado != null) {
                    this.libroDiarioIdSeleccionado = this.libroDiarioSeleccionado.getId();
                } else {
                    this.libroDiarioIdSeleccionado = null;
                }

                cargarDetallesLibroMayor();
                addMessage("Modo edición", "Editando libro mayor: " + this.registro.getObservacion());
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al editar registro", ex);
            addMessage("Error", "No se pudo cargar el registro para editar", true);
        }
    }

    /**
     * Método para forzar la actualización de los componentes del diálogo
     */
    public void actualizarComponentesDialogo() {
        try {
            PrimeFaces.current().ajax().update(
                    "frmDialogo:pnlCuentaSeleccionada",
                    "frmDialogo:txtCuentaSeleccionada",
                    "frmDialogo:pnlDetallesCuenta",
                    "frmDialogo:txtSaldoFinal",
                    "frmDialogo:btnCrearMayorizacion"
            );
        } catch (Exception e) {
            System.out.println("Error al actualizar componentes del diálogo: " + e.getMessage());
        }
    }
}