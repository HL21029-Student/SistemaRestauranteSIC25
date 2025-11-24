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
        if (r != null && r.getId() != null) {
            return r.getId().toString();
        }
        return null;
    }

    @Override
    protected DetalleLibroDiario getIdByText(String id) {
        if (id != null && id.isBlank() && this.modelo != null && this.modelo.getWrappedData() != null && !this.modelo.getWrappedData().isEmpty()) {
            try {
                Long buscado = Long.parseLong(id);
                return this.modelo.getWrappedData().stream().filter(r -> r.getId().equals(buscado)).findFirst().orElse(null);
            } catch (IllegalArgumentException e) {
                Logger.getLogger(DetalleLibroDiarioFrm.class.getName()).log(java.util.logging.Level.SEVERE, e.getMessage(), e);
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
        if (id instanceof UUID buscado && this.modelo != null) {
            return this.modelo.getWrappedData().stream().filter(r -> r.getId().equals(buscado)).findFirst().orElse(null);
        }
        return null;
    }

    //cargar datos
    @Override
    public List<DetalleLibroDiario> cargarDatos(int first, int max) {
        try {
            if (first >= 0 && max > 0 && this.idCuentaContable != null) {
                return detalleLibroDiarioDAO.findByCuentaContableId(this.idCuentaContable, first, max);
            }
        } catch (Exception e) {
            Logger.getLogger(DetalleLibroDiarioFrm.class.getName()).log(Level.SEVERE, e.getMessage(), e);
        }
        return listaDetalleLibroDiarios;
    }

    //contar datos
    @Override
    public int contarDatos() {
        try {
            if (this.idCuentaContable != null) {
                return this.detalleLibroDiarioDAO.countByCuentaContableId(this.idCuentaContable).intValue();
            }
        } catch (Exception e) {
            Logger.getLogger(DetalleLibroDiarioFrm.class.getName()).log(Level.SEVERE, e.getMessage(), e);
        }
        return 0;
    }


    //buscar cuenta por nombre de cuenta contable
    public List<CuentaContable> buscarCuentaContablePorNombre(final String nombre) {
        try {
            if (nombre != null && !nombre.isBlank()) {
                return cuentaContableDAO.findByNombreLike(nombre, 0, Integer.MAX_VALUE);
            }
        } catch (Exception e) {
            Logger.getLogger(DetalleLibroDiarioFrm.class.getName()).log(Level.SEVERE, e.getMessage(), e);
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
                        this.registro.setIdCuentaContable(cuenta);
                    } catch (Exception e) {
                        Logger.getLogger(DetalleLibroDiarioFrm.class.getName()).log(Level.WARNING, e.getMessage(), e);
                        this.registro.setIdCuentaContable(null);
                    }
                } else {
                    this.registro.setIdCuentaContable(null);
                }
                super.btnGuardarHandler(actionEvent);
            } catch (Exception e) {
                Logger.getLogger(DetalleLibroDiarioFrm.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }


    //btnSeleccionarCuentaContableHandler
    public void btnSeleccionarCuentaContableHandler(ActionEvent actionEvent) {
        if (this.registro != null &&
                this.idCuentaContable != null) {
            try {
                CuentaContable cuenta = cuentaContableDAO.findbyId(this.idCuentaContable);
                this.registro.setIdCuentaContable(cuenta);
            } catch (Exception e) {
                Logger.getLogger(DetalleLibroDiarioFrm.class.getName()).log(Level.WARNING, e.getMessage(), e);
                this.registro.setIdCuentaContable(null);
            }
        }
    }

    //Sobrescribir la seleccion para inicializar el flag en estado MODIFICAR
    @Override
    public void btnSeleccionarHandler(DetalleLibroDiario registro) {

    }


    public Long getIdCuentaContable() {
        return idCuentaContable;
    }

    public void setIdCuentaContable(Long idCuentaContable) {
        this.idCuentaContable = idCuentaContable;
    }

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
