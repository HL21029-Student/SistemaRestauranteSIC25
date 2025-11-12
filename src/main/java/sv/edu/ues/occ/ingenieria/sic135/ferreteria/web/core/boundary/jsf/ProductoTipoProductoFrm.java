package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf;

import jakarta.enterprise.context.Dependent;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ActionEvent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.event.SelectEvent;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.*;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Dependent
@Named
public class ProductoTipoProductoFrm extends DefaultFrm<ProductoTipoProducto> implements Serializable {
    @Inject
    FacesContext facesContext;
    @Inject
    ProductoTipoProductoDAO productoTipoProductoDAO;
    @Inject
    TipoProductoDAO tipoProductoDAO;
    @Inject
    ProductoTipoProductoCaracteristicaDAO productoTipoProductoCaracteristicaDAO;
    @Inject
    TipoProductoCaracteristicaDAO tipoProductoCaracteristicaDAO;

    List<TipoProductoCaracteristica> posibleCaracteristicas;
    List<TipoProductoCaracteristica> caracteristicasAsignadas;
    List<ProductoTipoProductoCaracteristica> asignacionesPersistentes;
    List<ProductoTipoProductoCaracteristica> asignacionesAEliminar;

    private TipoProductoCaracteristica seleccionPosibleCaracteristica;
    private TipoProductoCaracteristica seleccionCaracteristicaAsignada;
    protected UUID idProducto;


    private String equivalenciaEditable;

    public String getEquivalenciaEditable() {
        return equivalenciaEditable;
    }

    public void setEquivalenciaEditable(String equivalenciaEditable) {
        this.equivalenciaEditable = equivalenciaEditable;
    }

    /**
     * Listener llamado desde la vista cuando se selecciona una caracteristica asignada.
     * Carga la asignacion persistente correspondiente y llena equivalenciaEditable.
     */
    public void seleccionarCaracteristicaAsignada(jakarta.faces.event.AjaxBehaviorEvent event) {
        try {
            if (seleccionCaracteristicaAsignada == null || this.registro == null || this.registro.getId() == null) {
                this.equivalenciaEditable = null;
                return;
            }
            // cargar asignaciones persistentes para el ProductoTipoProducto seleccionado
            this.asignacionesPersistentes = productoTipoProductoCaracteristicaDAO.findByProductoTipoProductoId(this.registro.getId(), 0, Integer.MAX_VALUE);
            ProductoTipoProductoCaracteristica encontrada = this.asignacionesPersistentes.stream()
                    .filter(a -> a.getIdTipoProductoCaracteristica() != null
                            && a.getIdTipoProductoCaracteristica().getId() != null
                            && a.getIdTipoProductoCaracteristica().getId().equals(seleccionCaracteristicaAsignada.getId()))
                    .findFirst()
                    .orElse(null);
            this.equivalenciaEditable = (encontrada != null) ? encontrada.getValor() : null;
        } catch (Exception ex) {
            Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al cargar equivalencia", ex.getMessage()));
            this.equivalenciaEditable = null;
        }
    }

    /**
     * Guarda la equivalencia (valor) en la entidad ProductoTipoProductoCaracteristica.
     */
    public void guardarEquivalenciaHandler(ActionEvent event) {
        if (this.seleccionCaracteristicaAsignada == null) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Seleccione una característica", null));
            return;
        }
        if (this.registro == null || this.registro.getId() == null) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Seleccione primero el tipo de producto", null));
            return;
        }
        // Validación: si la característica es obligatoria, la equivalencia no puede ser nula/vacía
        if (Boolean.TRUE.equals(this.seleccionCaracteristicaAsignada.getObligatorio())
                && (this.equivalenciaEditable == null || this.equivalenciaEditable.trim().isEmpty())) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                    "Equivalencia requerida",
                    "Debe asignar un valor para la equivalencia de la característica obligatoria."));
            return;
        }

        try {
            // buscar asignacion existente
            ProductoTipoProductoCaracteristica asignacion = (this.asignacionesPersistentes != null ? this.asignacionesPersistentes.stream()
                    .filter(a -> a.getIdTipoProductoCaracteristica() != null
                            && a.getIdTipoProductoCaracteristica().getId() != null
                            && a.getIdTipoProductoCaracteristica().getId().equals(seleccionCaracteristicaAsignada.getId()))
                    .findFirst().orElse(null) : null);

            if (asignacion == null) {
                asignacion = new ProductoTipoProductoCaracteristica();
                asignacion.setId(UUID.randomUUID());
                asignacion.setIdProductoTipoProducto(this.registro);
                asignacion.setIdTipoProductoCaracteristica(this.seleccionCaracteristicaAsignada);
            }
            asignacion.setValor(this.equivalenciaEditable == null ? null : this.equivalenciaEditable.trim());

            // persistir: usar el DAO que en el proyecto expone save para ProductoTipoProductoCaracteristica
            // en este proyecto existe save en TipoProductoCaracteristicaDAO (revisar si debe usarse otro DAO)
            ProductoTipoProductoCaracteristica guardado = tipoProductoCaracteristicaDAO.save(asignacion);
            if (guardado != null) {
                // refrescar lista de asignaciones
                this.asignacionesPersistentes = productoTipoProductoCaracteristicaDAO.findByProductoTipoProductoId(this.registro.getId(), 0, Integer.MAX_VALUE);
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Equivalencia guardada", null));
            } else {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "No se pudo guardar la equivalencia", null));
            }
        } catch (Exception ex) {
            Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al guardar equivalencia: " + ex.getMessage(), null));
        }
    }

    public String getEquivalenciaPara(TipoProductoCaracteristica tpc) {
        if (tpc == null) return "";
        if (this.asignacionesPersistentes == null || this.asignacionesPersistentes.isEmpty()) return "";
        for (ProductoTipoProductoCaracteristica ap : this.asignacionesPersistentes) {
            if (ap.getIdTipoProductoCaracteristica() != null
                    && ap.getIdTipoProductoCaracteristica().getId() != null
                    && ap.getIdTipoProductoCaracteristica().getId().equals(tpc.getId())) {
                String v = ap.getValor();
                return (v == null) ? "" : v;
            }
        }
        return "";
    }

    @Override
    public List<ProductoTipoProducto> cargarDatos(int first, int max) {
        try {
            if (first >= 0 && max > 0 && this.idProducto != null) {
                return productoTipoProductoDAO.findByidProducto(this.idProducto, first, max);
            }
        } catch (Exception ex) {
            Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return List.of();
    }

    @Override
    public int contarDatos() {
        try {
            if (this.idProducto != null) {
                return this.productoTipoProductoDAO.countByidProducto(this.idProducto).intValue();
            }
        } catch (Exception ex) {
            Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return 0;
    }

    @Override
    protected FacesContext getFacesContext() {
        return facesContext;
    }

    @Override
    protected InventarioDAOInterface<ProductoTipoProducto, Object> getDao() {
        return productoTipoProductoDAO;
    }

    @Override
    protected String getIdAsText(ProductoTipoProducto r) {
        if (r != null && r.getId() != null) {
            return r.getId().toString();
        }
        return null;
    }

    @Override
    protected ProductoTipoProducto getIdByText(String id) {
        if (id != null && !id.isBlank() && this.modelo != null && this.modelo.getWrappedData() != null && !this.modelo.getWrappedData().isEmpty()) {
            try {
                UUID idUUID = UUID.fromString(id);
                return this.modelo.getWrappedData().stream().filter(r -> r.getId().equals(idUUID)).findFirst().orElse(null);
            } catch (Exception ex) {
                Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        return null;
    }

    @Override
    protected ProductoTipoProducto nuevoRegistro() {
        ProductoTipoProducto producto = new ProductoTipoProducto();
        producto.setActivo(true);
        producto.setId(UUID.randomUUID());
        producto.setFechaCreacion(new Date());
        if (idProducto != null) {
            producto.setIdProducto(new Producto());
            producto.getIdProducto().setId(idProducto);
        }
        return producto;
    }


    List<ProductoTipoProducto> listaTipoProducto;

    public List<ProductoTipoProducto> getListaTipoProducto() {
        return listaTipoProducto;
    }

    public ProductoTipoProductoFrm() {
    }

    public Long getIdTipoProductoSeleccionado() {
        if (this.registro != null && this.registro.getIdTipoProducto() != null) {
            return this.registro.getIdTipoProducto().getId();
        }
        return null;
    }

    public void setIdTipoProductoSeleccionado(final Long idTipoProducto) {
        if (this.registro != null && this.listaTipoProducto != null && idTipoProducto != null) {
            TipoProducto seleccionado = this.listaTipoProducto.stream()
                    .filter(tp -> tp.getId() != null && tp.getId().equals(idTipoProducto))
                    .findFirst()
                    .orElse(null).getIdTipoProducto();
            this.registro.setIdTipoProducto(seleccionado);
        }
    }

    public List<TipoProducto> buscarTiposPorNombres(final String nombres) {
        try {
            if (nombres != null && !nombres.isBlank()) {
                return tipoProductoDAO.findByNameLike(nombres, 0, Integer.MAX_VALUE);
            }
        } catch (Exception ex) {
            Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return List.of();
    }

    public void btnSeleccionarTipoProductoHandler(ActionEvent event) {
        try {
            Long idTipo = this.registro.getIdTipoProducto().getIdTipoProducto();
            this.posibleCaracteristicas = tipoProductoCaracteristicaDAO.findByIdTipoProducto(idTipo, 0, Integer.MAX_VALUE);
            if (this.registro != null && this.registro.getId() != null) {
                this.asignacionesPersistentes = productoTipoProductoCaracteristicaDAO.findByProductoTipoProductoId(this.registro.getId(), 0, Integer.MAX_VALUE);
                asegurarFechaCreacion();
            } else {
                this.asignacionesPersistentes = new java.util.ArrayList<>();
            }
            this.caracteristicasAsignadas = this.asignacionesPersistentes.stream()
                    .map(ProductoTipoProductoCaracteristica::getIdTipoProductoCaracteristica)
                    .map(tpc -> tpc)
                    .toList();
            if (this.posibleCaracteristicas != null) {
                this.posibleCaracteristicas = new java.util.ArrayList<>(this.posibleCaracteristicas);
            } else {
                this.posibleCaracteristicas = new java.util.ArrayList<>();
            }
            if (this.caracteristicasAsignadas != null) {
                this.caracteristicasAsignadas = new java.util.ArrayList<>(this.caracteristicasAsignadas);
            } else {
                this.caracteristicasAsignadas = new java.util.ArrayList<>();
            }
            if (this.asignacionesPersistentes != null) {
                this.asignacionesPersistentes = new java.util.ArrayList<>(this.asignacionesPersistentes);
            } else {
                this.asignacionesPersistentes = new java.util.ArrayList<>();
            }
            if (this.posibleCaracteristicas != null) {
                this.posibleCaracteristicas.removeIf(pc ->
                        this.asignacionesPersistentes.stream()
                                .anyMatch(ap -> ap.getIdTipoProductoCaracteristica().getId().equals(pc.getId())));
            }
            List<TipoProductoCaracteristica> obligatorias = tipoProductoCaracteristicaDAO.findObligatoriasByTipo(idTipo);
            for (TipoProductoCaracteristica tpc : obligatorias) {
                boolean ya = this.asignacionesPersistentes.stream()
                        .anyMatch(ap -> ap.getIdTipoProductoCaracteristica().getId().equals(tpc.getId()));
                if (!ya) {
                    ProductoTipoProductoCaracteristica nueva = new ProductoTipoProductoCaracteristica();
                    nueva.setId(UUID.randomUUID());
                    nueva.setIdProductoTipoProducto(this.registro);
                    nueva.setIdTipoProductoCaracteristica(tpc);
                    // obligatorias no tienen flag en la entidad intermedia; la obligación está en TipoProductoCaracteristica.obligatorio
                    this.asignacionesPersistentes.add(nueva);
                }
            }
            return;
        } catch (Exception ex) {
            Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        this.posibleCaracteristicas = List.of();
        this.caracteristicasAsignadas = new java.util.ArrayList<>();
        this.asignacionesPersistentes = new java.util.ArrayList<>();
    }

    public void btnAgregarPosibleCaracteristicaHandler(ActionEvent event) {
        try {
            Object sel = event.getComponent().getAttributes().get("selectedCaracteristica");
            if (!(sel instanceof TipoProductoCaracteristica)) {
                return;
            }
            TipoProductoCaracteristica seleccion = (TipoProductoCaracteristica) sel;
            if (this.asignacionesPersistentes == null) this.asignacionesPersistentes = new java.util.ArrayList<>();

            ProductoTipoProductoCaracteristica nueva = new ProductoTipoProductoCaracteristica();
            nueva.setId(UUID.randomUUID());
            nueva.setIdProductoTipoProducto(this.registro);
            nueva.setIdTipoProductoCaracteristica(seleccion);
            this.asignacionesPersistentes.add(nueva);

            if (this.caracteristicasAsignadas == null) this.caracteristicasAsignadas = new java.util.ArrayList<>();
            if (!this.caracteristicasAsignadas.contains(seleccion)) {
                this.caracteristicasAsignadas.add(seleccion);
            }
            if (this.posibleCaracteristicas != null) {
                this.posibleCaracteristicas.removeIf(pc -> pc.getId().equals(seleccion.getId()));
            }
        } catch (Exception ex) {
            Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public void btnEliminarCaracteristicaAsignadaHandler(ActionEvent event) {
        try {
            Object sel = event.getComponent().getAttributes().get("selectedCaracteristica");
            if (!(sel instanceof TipoProductoCaracteristica)) {
                return;
            }
            TipoProductoCaracteristica seleccion = (TipoProductoCaracteristica) sel;

            if (Boolean.TRUE.equals(seleccion.getObligatorio())) {
                Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.INFO, "Intento de eliminar caracteristica obligatoria denegado");
                facesContext.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN,
                                "No se puede eliminar",
                                "La característica es obligatoria y no puede eliminarse."));
                return;
            }

            ProductoTipoProductoCaracteristica encontrada = null;
            if (this.asignacionesPersistentes != null) {
                for (ProductoTipoProductoCaracteristica ap : this.asignacionesPersistentes) {
                    if (ap.getIdTipoProductoCaracteristica().getId().equals(seleccion.getId())) {
                        encontrada = ap;
                        break;
                    }
                }
            }

            if (encontrada != null) {
                if (productoTipoProductoCaracteristicaDAO.exists(encontrada.getId())) {
                    productoTipoProductoCaracteristicaDAO.delete(encontrada);
                }
                ProductoTipoProductoCaracteristica finalEncontrada = encontrada;
                this.asignacionesPersistentes.removeIf(a -> a.getId().equals(finalEncontrada.getId()));
            }
            if (this.caracteristicasAsignadas != null) {
                this.caracteristicasAsignadas.removeIf(c -> c.getId().equals(seleccion.getId()));
            }
            if (this.posibleCaracteristicas == null) this.posibleCaracteristicas = new java.util.ArrayList<>();
            if (this.posibleCaracteristicas.stream().noneMatch(p -> p.getId().equals(seleccion.getId()))) {
                this.posibleCaracteristicas.add(seleccion);
            }
        } catch (Exception ex) {
            Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    @Override
    public void btnModificarHandler(ActionEvent actionEvent) {
        if (this.registro == null) {
            this.enviarMensaje("No hay registro seleccionado", FacesMessage.SEVERITY_ERROR);
            return;
        }
        try {
            List<ProductoTipoProductoCaracteristica> bdActuales =
                    productoTipoProductoCaracteristicaDAO.findByProductoTipoProductoId(this.registro.getId(), 0, Integer.MAX_VALUE);
            List<ProductoTipoProductoCaracteristica> aCrear = new java.util.ArrayList<>();
            for (ProductoTipoProductoCaracteristica ap : getAsignacionesPersistentes()) {
                boolean found = bdActuales.stream()
                        .anyMatch(b -> b.getIdTipoProductoCaracteristica().getId().equals(ap.getIdTipoProductoCaracteristica().getId()));
                if (!found) aCrear.add(ap);
            }
            List<ProductoTipoProductoCaracteristica> aEliminar = new java.util.ArrayList<>();
            for (ProductoTipoProductoCaracteristica b : bdActuales) {
                boolean found = getAsignacionesPersistentes().stream()
                        .anyMatch(ap -> ap.getIdTipoProductoCaracteristica().getId().equals(b.getIdTipoProductoCaracteristica().getId()));
                if (!found) aEliminar.add(b);
            }
            for (ProductoTipoProductoCaracteristica crear : aCrear) {
                productoTipoProductoCaracteristicaDAO.create(crear);
            }
            for (ProductoTipoProductoCaracteristica eliminar : aEliminar) {
                productoTipoProductoCaracteristicaDAO.delete(eliminar);
            }

            asegurarFechaCreacion();

            this.getDao().update(this.registro);

            enviarMensaje("Registro modificado", FacesMessage.SEVERITY_INFO);
            this.inicializarRegistros();
            this.estado = ESTADO_CRUD.NADA;
            this.registro = null;
        } catch (Exception ex) {
            enviarMensaje("Error al modificar el registro: " + ex.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }


    @Override
    public void seleccionarRegistro(SelectEvent<ProductoTipoProducto> event) {
        super.seleccionarRegistro(event);
        asegurarFechaCreacion();
    }

    private void asegurarFechaCreacion() {
        try {
            if (this.registro != null && this.registro.getId() != null) {
                ProductoTipoProducto existente = productoTipoProductoDAO.findById(this.registro.getId());
                if (existente != null
                        && this.registro.getFechaCreacion() == null
                        && existente.getFechaCreacion() != null) {
                    this.registro.setFechaCreacion(existente.getFechaCreacion());
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public List<TipoProductoCaracteristica> getCaracteristicasAsignadas() {
        if (this.caracteristicasAsignadas == null) {
            this.caracteristicasAsignadas = new java.util.ArrayList<>();
        }
        return caracteristicasAsignadas;
    }

    public List<TipoProductoCaracteristica> getPosibleCaracteristicas() {
        if (this.posibleCaracteristicas == null) {
            this.posibleCaracteristicas = new java.util.ArrayList<>();
        }
        return posibleCaracteristicas;
    }

    public TipoProductoCaracteristica getSeleccionPosibleCaracteristica() {
        return seleccionPosibleCaracteristica;
    }

    public void setSeleccionPosibleCaracteristica(TipoProductoCaracteristica seleccionPosibleCaracteristica) {
        this.seleccionPosibleCaracteristica = seleccionPosibleCaracteristica;
    }

    public TipoProductoCaracteristica getSeleccionCaracteristicaAsignada() {
        return seleccionCaracteristicaAsignada;
    }

    public void setSeleccionCaracteristicaAsignada(TipoProductoCaracteristica seleccionCaracteristicaAsignada) {
        this.seleccionCaracteristicaAsignada = seleccionCaracteristicaAsignada;
    }

    public List<ProductoTipoProductoCaracteristica> getAsignacionesPersistentes() {
        if (this.asignacionesPersistentes == null) this.asignacionesPersistentes = new java.util.ArrayList<>();
        return this.asignacionesPersistentes;
    }

    @Override
    public InventarioDefaultDataAccess getDataAccess() {
        return productoTipoProductoDAO;
    }

    @Override
    protected ProductoTipoProducto buscarRegistroPorId(Object id) {
        if (id instanceof UUID buscado && this.modelo != null) {
            return this.modelo.getWrappedData().stream().filter(r -> r.getId().equals(buscado)).findFirst().orElse(null);
        }
        return null;
    }

    public UUID getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(UUID idProducto) {
        this.idProducto = idProducto;
    }

    protected String nombreBean = "page.productoTipoProducto";

    @Override
    public String getNombreBean() {
        return nombreBean;
    }
}
