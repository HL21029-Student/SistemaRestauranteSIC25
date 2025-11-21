package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.Dependent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ActionEvent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.*;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.CuentaContable;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.DetalleLibroDiario;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.KardexDetalle;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.LibroDiario;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Dependent
@Named
public class DetalleLibroDiarioFrm extends DefaultFrm<DetalleLibroDiario> implements Serializable {
    @Inject
    FacesContext facesContext;
    @Inject
    DetalleLibroDiarioDAO detalleLibroDiarioDAO;
    List<DetalleLibroDiario> detalleLibroDiarios;
    List<DetalleLibroDiario> listaDetalleLibroDiarios;

    //cuenta contable para poder recomendar la cuenta que se va a usar en el detalle del libro diario
    @Inject
    CuentaContableDAO cuentaContableDAO;
    protected Long idCuentaContable;
    List<CuentaContable> cuentaContables;

    //para poder implementar referenias atravez de kardex
    @Inject
    KardexDetalleDAO kardexDetalleDAO;
    protected UUID idKardexDetalle;

    @Named("libroDiarioFrm")
    @Inject
    private LibroDiarioFrm libroDiarioFrm;
    private LibroDiario idLibroDiario;

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
        if(r!= null && r.getId() != null){
            return r.getId().toString();
        }
        return null;
    }

    @Override
    protected DetalleLibroDiario getIdByText(String id) {
        if(id != null && id.isBlank() && this.modelo != null && this.modelo.getWrappedData() != null && !this.modelo.getWrappedData().isEmpty()){
            try{
                Long buscado = Long.parseLong(id);
                return this.modelo.getWrappedData().stream().filter(r -> r.getId().equals(buscado)).findFirst().orElse(null);
            }catch(IllegalArgumentException e){
                Logger.getLogger(DetalleLibroDiarioFrm.class.getName()).log(java.util.logging.Level.SEVERE,e.getMessage(),e);
            }
        }
        return null;
    }

    @PostConstruct
    @Override
    public void inicializar(){
        super.inicializar();
        if(this.idCuentaContable !=null){
            listaDetalleLibroDiarios = detalleLibroDiarioDAO.findByCuentaContableId(this.idCuentaContable, 0, Integer.MAX_VALUE);
        }else {
            listaDetalleLibroDiarios = List.of();
        }
    }

    @Override
    protected DetalleLibroDiario nuevoRegistro() {
        DetalleLibroDiario detalleLibroDiario = new DetalleLibroDiario();
        detalleLibroDiario.setId(UUID.randomUUID());
        detalleLibroDiario.setFecha(Date.from(new Date().toInstant()));
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
        if(id instanceof UUID buscado && this.modelo != null){
            return this.modelo.getWrappedData().stream().filter(r -> r.getId().equals(buscado)).findFirst().orElse(null);
        }
        return null;
    }

    //cargar datos
    @Override
    public List<DetalleLibroDiario> cargarDatos(int first, int max){
        try{
            if(first>=0 && max>0 && this.idCuentaContable!=null) {
                return detalleLibroDiarioDAO.findByCuentaContableId(this.idCuentaContable, first, max);
            }
        }catch (Exception e){
            Logger.getLogger(DetalleLibroDiarioFrm.class.getName()).log(Level.SEVERE,e.getMessage(),e);
        }
        return listaDetalleLibroDiarios;
    }

    //contar datos
    @Override
    public int contarDatos(){
        try{
            if(this.idCuentaContable!=null) {
                return this.detalleLibroDiarioDAO.countByCuentaContableId(this.idCuentaContable).intValue();
            }
        }catch (Exception e){
            Logger.getLogger(DetalleLibroDiarioFrm.class.getName()).log(Level.SEVERE,e.getMessage(),e);
        }
        return 0;
    }


    //buscar cuenta por nombre de cuenta contable
    public List<CuentaContable> buscarCuentaContablePorNombre(final String nombre) {
        try{
            if(nombre != null && !nombre.isBlank()) {
                return cuentaContableDAO.findByNombreLike(nombre, 0, Integer.MAX_VALUE);
            }
        } catch (Exception e) {
            Logger.getLogger(DetalleLibroDiarioFrm.class.getName()).log(Level.SEVERE,e.getMessage(),e);
        }
        return List.of();
    }

    //btnGuardarHandler
    @Override
    public void btnGuardarHandler(ActionEvent actionEvent) {
        if (this.registro != null &&
                libroDiarioFrm != null &&
                libroDiarioFrm.getRegistro() != null) {
            try {
                // vincular el libro diario al detalle
                this.registro.setLibroDiario(libroDiarioFrm.getRegistro());
                this.idLibroDiario = libroDiarioFrm.getRegistro();

                // si se ha seleccionado una cuenta contable, cargarla y asignarla al detalle
                if (this.idCuentaContable != null) {
                    try {
                        CuentaContable cuenta = cuentaContableDAO.findbyId(this.idCuentaContable);
                        if (cuenta != null) {
                            this.registro.setIdCuentaContable(cuenta);
                        } else {
                            this.registro.setIdCuentaContable(null);
                        }
                    } catch (Exception e) {
                        Logger.getLogger(DetalleLibroDiarioFrm.class.getName()).log(Level.WARNING, e.getMessage(), e);
                        this.registro.setIdCuentaContable(null);
                    }
                } else {
                    this.registro.setIdCuentaContable(null);
                }

                // NORMALIZAR idKardexDetalle: si hay UUID cargar la entidad; si no, asegurar null.
                if (this.idKardexDetalle != null) {
                    try {
                        KardexDetalle kardex = kardexDetalleDAO.findbyId(this.idKardexDetalle);
                        if (kardex != null) {
                            this.registro.setIdKardexDetalle(kardex);
                        } else {
                            this.registro.setIdKardexDetalle(null);
                        }
                    } catch (Exception e) {
                        Logger.getLogger(DetalleLibroDiarioFrm.class.getName()).log(Level.WARNING, e.getMessage(), e);
                        this.registro.setIdKardexDetalle(null);
                    }
                } else {
                    // Protección adicional: limpiar posibles valores inválidos que vengan del formulario
                    if (this.registro.getIdKardexDetalle() != null) {
                        try {
                            UUID posibleId = this.registro.getIdKardexDetalle().getId();
                            if (posibleId == null) {
                                this.registro.setIdKardexDetalle(null);
                            } else {
                                // opcional: reemplazar por la entidad gestionada
                                KardexDetalle kardex = kardexDetalleDAO.findbyId(posibleId);
                                this.registro.setIdKardexDetalle(kardex);
                            }
                        } catch (Exception ex) {
                            Logger.getLogger(DetalleLibroDiarioFrm.class.getName()).log(Level.WARNING, ex.getMessage(), ex);
                            this.registro.setIdKardexDetalle(null);
                        }
                    } else {
                        this.registro.setIdKardexDetalle(null);
                    }
                }

                super.btnGuardarHandler(actionEvent);
            } catch (Exception e) {
                Logger.getLogger(DetalleLibroDiarioFrm.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    //btnSeleccionarCuentaContableHandler
    public void btnSeleccionarCuentaContableHandler(ActionEvent actionEvent) {
        if(this.registro != null && this.registro.getIdCuentaContable() != null){
            this.cuentaContables = cuentaContableDAO.findByIdCuentaContable(this.registro.getIdCuentaContable().getId(), 0, Integer.MAX_VALUE);
            this.idCuentaContable = this.registro.getIdCuentaContable().getId();
        }else{
            this.cuentaContables = List.of();
            this.idCuentaContable = null;
        }
    }

    //Logica base para poder inicializar la funcionalidad de kardexDetalle
    //pero en realidad esta logica va mas allá de kardex
    public void btnSeleccionarKardexDetalleHandler(ActionEvent actionEvent) {
        if(this.registro != null && this.registro.getIdKardexDetalle() != null){
            this.idKardexDetalle = this.registro.getIdKardexDetalle().getId();
        } else {
            this.idKardexDetalle = null;
        }
    }

    public void btnLimpiarKardexHandler(ActionEvent actionEvent) {
        if(this.registro != null){
            this.registro.setIdKardexDetalle(null);
            this.idKardexDetalle = null;
        }
    }

    //se utiliza el nombre del lote para referenciar y buscar por él
    public List<KardexDetalle> buscarKardexDetallePorReferencia(final String lote){
        try{
            if(lote != null && !lote.isBlank()) {
                return kardexDetalleDAO.findByReferenciaLoteLike(lote, 0, Integer.MAX_VALUE);
            }
        } catch (Exception e) {
            Logger.getLogger(DetalleLibroDiarioFrm.class.getName()).log(Level.SEVERE,e.getMessage(),e);
        }
        return List.of();
    }

    public UUID getIdKardexDetalle() {
        return idKardexDetalle;
    }
    public void setIdKardexDetalle(UUID idKardexDetalle) {
        this.idKardexDetalle = idKardexDetalle;
    }

    public Long getIdCuentaContable() {
        return idCuentaContable;
    }

    public void setIdCuentaContable(Long idCuentaContable) {this.idCuentaContable = idCuentaContable;}

    public LibroDiario getIdLibroDiario() {
        return idLibroDiario;
    }

    public void setIdLibroDiario(LibroDiario libroDiario) {
        this.idLibroDiario = libroDiario;
    }

    public LibroDiarioFrm getLibroDiarioFrm() {
        return libroDiarioFrm;
    }

    public void setLibroDiarioFrm(LibroDiarioFrm libroDiarioFrm) {
        this.libroDiarioFrm = libroDiarioFrm;
    }
}
