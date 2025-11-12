package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ActionEvent;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.ClienteDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.InventarioDAOInterface;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.InventarioDefaultDataAccess;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.Cliente;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ViewScoped
public class ClienteFrm extends DefaultFrm<Cliente> implements Serializable {


    @Inject
    FacesContext facesContext;

    @Inject
    ClienteDAO clienteDAO;

    private List<Cliente> clientes;

    public ClienteFrm() {}

    @Override
    protected FacesContext getFacesContext() {
        return facesContext;
    }

    @Override
    protected InventarioDAOInterface<Cliente, Object> getDao() {
        return clienteDAO;
    }

    @Override
    protected String getIdAsText(Cliente r) {
        if (r != null && r.getId() != null) {
            return r.getId().toString();
        }
        return null;
    }

    @Override
    protected Cliente getIdByText(String id) {
        if (id != null) {
            try {
                UUID buscado = UUID.fromString(id);
                return this.modelo.getWrappedData().stream()
                        .filter(r -> r.getId().equals(buscado))
                        .findFirst().orElse(null);
            } catch (IllegalArgumentException e) {
                Logger.getLogger(ClienteFrm.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        return null;
    }

    @PostConstruct
    @Override
    public void inicializar() {
        super.inicializar();
        clientes = clienteDAO.findRange(0, Integer.MAX_VALUE);
    }

    @Override
    protected Cliente nuevoRegistro() {
        Cliente cliente = new Cliente();
        cliente.setId(UUID.randomUUID());
        cliente.setNombre("");
        cliente.setDui("");
        cliente.setNit("");
        cliente.setActivo(true);
        return cliente;
    }


    @Override
    public InventarioDefaultDataAccess getDataAccess() {
        return clienteDAO;
    }

    @Override
    protected Cliente buscarRegistroPorId(Object id) {
        if (id instanceof UUID buscado && this.modelo != null) {
            return this.modelo.getWrappedData().stream()
                    .filter(r -> r.getId().equals(buscado))
                    .findFirst().orElse(null);
        }
        return null;
    }
    @Override
    public void btnGuardarHandler(ActionEvent actionEvent) {
        try {
            if (registro != null) {// Validaciones antes de guardar
                if (!validarCampos()) {
                    return; // si hay error, no continúa
                }

                getDao().create(registro);
                this.enviarMensaje("Registro creado correctamente", FacesMessage.SEVERITY_INFO);
                this.estado = ESTADO_CRUD.NADA;
                this.registro = null;
                inicializarRegistros();
                return;
            }
        } catch (Exception ex) {
            enviarMensaje("Error al crear el registro: " + ex.getMessage(), FacesMessage.SEVERITY_ERROR);
            return;
        }
        enviarMensaje("El registro a almacenar no puede ser nulo", FacesMessage.SEVERITY_WARN);
        this.estado = ESTADO_CRUD.NADA;
    }

    @Override
    public void btnModificarHandler(ActionEvent actionEvent) {
        if (this.registro == null) {
            this.enviarMensaje("No hay registro seleccionado", FacesMessage.SEVERITY_ERROR);
            return;
        }

        try {// Validaciones antes de modificar
            if (!validarCampos()) {
                return; // si hay error, no continúa
            }

            this.getDao().update(this.registro);
            enviarMensaje("Registro modificado correctamente", FacesMessage.SEVERITY_INFO);
            this.inicializarRegistros();
            this.estado = ESTADO_CRUD.NADA;
            this.registro = null;
        } catch (Exception ex) {
            enviarMensaje("Error al modificar el registro: " + ex.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }

    /**
     * Validar longitud de DUI y NIT antes de crear o modificar.
     */
    private boolean validarCampos() {
        if (registro.getDui() == null || registro.getDui().length() != 9) {
            enviarMensaje("El DUI debe tener exactamente 9 dígitos.", FacesMessage.SEVERITY_WARN);
            return false;
        }

        if (registro.getNit() == null || registro.getNit().length() != 14) {
            enviarMensaje("El NIT debe tener exactamente 14 dígitos.", FacesMessage.SEVERITY_WARN);
            return false;
        }

        return true;
    }


    public List<Cliente> buscarClientesPorNombre(final String nombre){
        try{
            if(nombre !=null && !nombre.isEmpty()) {
                return clienteDAO.buscarClientePorNombre(nombre, 0, Integer.MAX_VALUE);
            }
        }catch (Exception ex){
            Logger.getLogger(ClienteFrm.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return List.of();
    }



    private String nombreBean = "page.cliente";

    public String getNombreBean() {
        return nombreBean;
    }

    public void setNombreBean(String nombreBean) {
        this.nombreBean = nombreBean;
    }

    public List<Cliente> getListaClientes() {
        return clientes;
    }

    public void setListaClientes(List<Cliente> clientes) {
        this.clientes = clientes;
    }
}

