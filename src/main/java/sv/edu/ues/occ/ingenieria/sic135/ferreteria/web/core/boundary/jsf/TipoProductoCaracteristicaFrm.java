package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ActionEvent;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.CaracteristicaDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.InventarioDAOInterface;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.InventarioDefaultDataAccess;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.TipoProductoCaracteristicaDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.Caracteristica;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.TipoProducto;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.TipoProductoCaracteristica;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


@Named
@ViewScoped
public class TipoProductoCaracteristicaFrm extends DefaultFrm<TipoProductoCaracteristica> implements Serializable {

    @Inject
    FacesContext facesContext;

    @Inject
    TipoProductoCaracteristicaDAO tipoProductoCaracteristicaDAO;

    @Inject
    CaracteristicaDAO caracteristicaDAO;

    List<Caracteristica> posibleCaracteristicas;

    List<TipoProductoCaracteristica> caracteristicasAsignadas;

    /**
     * Por compatibilidad `idCaracteristica` conserva su nombre antiguo,
     * pero su uso real es guardar el id del TipoProducto padre (Long).
     */
    protected Long idCaracteristica;

    protected String nombrebean = "page.tipoproductocaracteristica";

    @Named("tipoProductoFrm")
    @Inject
    private TipoProductoFrm tipoProductoFrm;

    private TipoProducto idTipoProducto;

    public String getNombrebean() {
        return nombrebean;
    }

    public TipoProductoCaracteristicaFrm() {}

    public Long getIdTipoProductoCaracteristicaSeleccionado() {
        if(this.registro != null && this.registro.getIdTipoProducto() != null){
            return this.registro.getIdTipoProducto().getId();
        }
        return null;
    }

    public void setIdTipoProductoCaracteristicaSeleccionado(final Long idTipoProductoSeleccionado) {
        if(this.registro != null && this.listaTipoProductoCaracteristica != null && !this.listaTipoProductoCaracteristica.isEmpty()){
            TipoProductoCaracteristica tpc = this.listaTipoProductoCaracteristica.stream()
                    .filter(tp-> tp.getId().equals(idTipoProductoSeleccionado))
                    .findFirst().orElse(null);
            if (tpc != null) {
                this.registro.setIdTipoProducto(tpc.getIdTipoProducto());
            }
        }
    }

    List<TipoProductoCaracteristica> listaTipoProductoCaracteristica;

    @Override
    public List<TipoProductoCaracteristica> cargarDatos(int first, int max) {
        try{
            if(first>=0 && max>0 && this.idCaracteristica!=null){
                return tipoProductoCaracteristicaDAO.findByIdTipoProducto(this.idCaracteristica, first, max);
            }
        }catch(Exception ex){
            Logger.getLogger(TipoProductoCaracteristicaFrm.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return listaTipoProductoCaracteristica;
    }

    @Override
    public int contarDatos() {
        try{
            if(this.idCaracteristica!=null){
                return this.tipoProductoCaracteristicaDAO.countByIdTipoProducto(this.idCaracteristica).intValue();
            }
        }catch(Exception ex){
            Logger.getLogger(TipoProductoCaracteristicaFrm.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return 0;
    }

    @Override
    protected FacesContext getFacesContext() {
        return facesContext;
    }

    @Override
    protected InventarioDAOInterface<TipoProductoCaracteristica, Object> getDao() {
        return tipoProductoCaracteristicaDAO;
    }

    @Override
    protected String getIdAsText(TipoProductoCaracteristica r) {
        if (r != null && r.getId() != null) {
            return r.getId().toString();
        }
        return null;
    }

    @Override
    protected TipoProductoCaracteristica getIdByText(String id) {
        if (id != null) {
            try {
                Long buscado = Long.parseLong(id);
                return this.modelo.getWrappedData().stream().filter(r -> r.getId().equals(buscado)).findFirst().orElse(null);
            } catch (IllegalArgumentException e) {
                Logger.getLogger(TipoProductoCaracteristicaFrm.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            }
        }
        return null;
    }

    @PostConstruct
    @Override
    public void inicializar() {
        super.inicializar();
        if (this.idCaracteristica != null) {
            listaTipoProductoCaracteristica =
                    tipoProductoCaracteristicaDAO.findByIdTipoProducto(idCaracteristica, 0, Integer.MAX_VALUE);
        } else {
            listaTipoProductoCaracteristica = List.of();
        }
    }

    @Override
    protected TipoProductoCaracteristica nuevoRegistro() {
        TipoProductoCaracteristica tipoProductoCaracteristica = new TipoProductoCaracteristica();
        tipoProductoCaracteristica.setFechaCreacion(new Date());
        tipoProductoCaracteristica.setObligatorio(true);
        return tipoProductoCaracteristica;
    }

    @Override
    public InventarioDefaultDataAccess getDataAccess() {
        return tipoProductoCaracteristicaDAO;
    }

    @Override
    protected TipoProductoCaracteristica buscarRegistroPorId(Object id) {
        if (id != null && this.modelo != null) {
            try {
                Long idLong = Long.parseLong(id.toString());
                return this.modelo.getWrappedData().stream().filter(r -> r.getId().equals(idLong)).findFirst().orElse(null);
            } catch (IllegalArgumentException e) {
                Logger.getLogger(TipoProductoCaracteristicaFrm.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            }
        }
        return null;
    }

    public List<Caracteristica> buscarCaracteristicaPorNombres(final String nombres) {
        try {
            if (nombres != null && !nombres.isBlank()) {
                return caracteristicaDAO.findByNombreLike(nombres, 0, 25);
            }
        } catch (Exception ex) {
            Logger.getLogger(TipoProductoCaracteristicaFrm.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return List.of();
    }

    @Override
    public void btnGuardarHandler(ActionEvent actionEvent) {
        try {
            if (this.registro != null
                    && tipoProductoFrm != null
                    && tipoProductoFrm.getRegistro() != null) {
                this.registro.setIdTipoProducto(tipoProductoFrm.getRegistro());
                this.idCaracteristica = tipoProductoFrm.getRegistro().getId();
            }
            super.btnGuardarHandler(actionEvent);
            if (this.estado == ESTADO_CRUD.NADA) {
                if (idCaracteristica != null) {
                    listaTipoProductoCaracteristica =
                            tipoProductoCaracteristicaDAO.findByIdTipoProducto(idCaracteristica, 0, Integer.MAX_VALUE);
                }

                if (this.modelo != null) {
                    try {
                        this.modelo.setRowCount(contarDatos());
                    } catch (Exception ex) {
                    }
                }
            }
        } catch (Exception e) {
            Logger.getLogger(TipoProductoCaracteristicaFrm.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    public void btnSeleccionarCaracteristicaHandler(ActionEvent actionEvent) {
        if (this.registro != null && this.registro.getIdCaracteristica() != null) {
            this.posibleCaracteristicas = caracteristicaDAO.findByIdCaracteristica(this.registro.getIdCaracteristica().getId(), 0, Integer.MAX_VALUE);
        } else {
            this.posibleCaracteristicas = List.of();
        }
    }

    public List<TipoProductoCaracteristica> getTipoProductoCaracteristicas() {
        return listaTipoProductoCaracteristica;
    }

    public void setListaTipoProductoCaracteristica(List<TipoProductoCaracteristica> listaTipoProductoCaracteristica) {
        this.listaTipoProductoCaracteristica = listaTipoProductoCaracteristica;
    }

    public List<Caracteristica> getPosibleCaracteristicas() {
        return posibleCaracteristicas;
    }

    public void setPosibleCaracteristicas(List<Caracteristica> posibleCaracteristicas) {
        this.posibleCaracteristicas = posibleCaracteristicas;
    }

    public List<TipoProductoCaracteristica> getCaracteristicasAsignadas() {
        return caracteristicasAsignadas;
    }

    public void setCaracteristicasAsignadas(List<TipoProductoCaracteristica> caracteristicasAsignadas) {
        this.caracteristicasAsignadas = caracteristicasAsignadas;
    }

    public Long getIdCaracteristica() {
        return idCaracteristica;
    }

    public void setIdCaracteristica(Long idCaracteristica) {
        this.idCaracteristica = idCaracteristica;
    }

    public void setIdTipoProducto(TipoProducto idTipoProducto) {
        this.idTipoProducto = idTipoProducto;
    }
    public TipoProducto getTipoProducto() {
        return idTipoProducto;
    }

}
