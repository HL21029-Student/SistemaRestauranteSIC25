package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ActionEvent;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.InventarioDAOInterface;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.InventarioDefaultDataAccess;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.LibroDiario;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.LibroDiarioDAO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Named("libroDiarioFrm")
@ViewScoped
public class LibroDiarioFrm extends DefaultFrm<LibroDiario> implements Serializable {
    @Inject
    FacesContext facesContext;
    @Inject
    LibroDiarioDAO libroDiarioDAO;

    @Inject
    protected DetalleLibroDiarioFrm detalleLibroDiarioFrm;

    private TreeNode root;
    private TreeNode selectedNode;
    List<LibroDiario> listaLibroDiario;

    @Override
    protected FacesContext getFacesContext() {
        return facesContext;
    }

    @Override
    protected InventarioDAOInterface<LibroDiario, Object> getDao() {
        return libroDiarioDAO;
    }

    @Override
    protected String getIdAsText(LibroDiario r) {
        if(r != null && r.getId() != null){
            return r.getId().toString();
        }
        return null;
    }

    @Override
    protected LibroDiario getIdByText(String id) {
        if(id!=null){
            try{
                Long buscado = Long.parseLong(id);
                return this.modelo.getWrappedData().stream().filter(r -> r.getId().equals(buscado)).findFirst().orElse(null);
            }catch(IllegalArgumentException e){
                Logger.getLogger(LibroDiarioFrm.class.getName()).log(Level.SEVERE,e.getMessage(),e);
            }
        }
        return null;
    }

    @PostConstruct
    @Override
    public void inicializar(){
        super.inicializar();
        if(this.registro == null){
            this.registro = nuevoRegistro();
        }
        listaLibroDiario = libroDiarioDAO.findRange(0, Integer.MAX_VALUE);
        cargarArbol();
    }

    @Override
    protected LibroDiario nuevoRegistro() {
        LibroDiario librodiario = new LibroDiario();
        librodiario.setNombre("");
        librodiario.setComentario("");
        return librodiario;
    }

    @Override
    public InventarioDefaultDataAccess getDataAccess() {
        return libroDiarioDAO;
    }

    @Override
    protected LibroDiario buscarRegistroPorId(Object id) {
        if(id instanceof  Long buscado && this.modelo != null){
            return this.modelo.getWrappedData().stream().filter(r -> r.getId().equals(buscado)).findFirst().orElse(null);
        }
        return null;
    }


    public void cargarArbol(){
        root = new DefaultTreeNode("Root", null);

        List<LibroDiario> padres = libroDiarioDAO.findDiarioAjustePadre();
        for(LibroDiario padre : padres){
            TreeNode padreNode = new DefaultTreeNode(padre, root);
            cargarHijos(padreNode, padre.getId());
        }
    }

    public void cargarHijos(TreeNode padreNode, Long id){
        List<LibroDiario> hijos = libroDiarioDAO.findDiarioAjusteHijos(id);
        for(LibroDiario hijo : hijos){
            TreeNode hijoNode = new DefaultTreeNode(hijo, padreNode);
            cargarHijos(hijoNode, hijo.getId());
        }
    }

    public void onNodeSelect(NodeSelectEvent event) {
        this.selectedNode = event.getTreeNode();
        if(selectedNode != null && selectedNode.getData() != null){
            LibroDiario seleccionado = (LibroDiario) selectedNode.getData();
            this.registro = seleccionado;
            this.estado = ESTADO_CRUD.valueOf("MODIFICAR");

            //si se neceista sincronizar otros campos referentes, hacerlo aqui

        }
    }

    //instanciacion de btnGuardarHandler
    @Override
    public void btnGuardarHandler(ActionEvent actionEvent) {
        super.btnGuardarHandler(actionEvent);
        if(this.estado == ESTADO_CRUD.NADA){
            cargarArbol();
            listaLibroDiario = libroDiarioDAO.findRange(0, Integer.MAX_VALUE);
        }
    }

    //instanciacion de btnEliminarHandler
    @Override
    public void btnEliminarHandler(ActionEvent actionEvent) {
        super.btnEliminarHandler(actionEvent);
        if(this.estado == ESTADO_CRUD.NADA){
            cargarArbol();
            listaLibroDiario = libroDiarioDAO.findRange(0, Integer.MAX_VALUE);
        }
    }

    public List<LibroDiario> getLibroDiarioDisponibles(){
        if(listaLibroDiario == null){
            return List.of();
        }
        return listaLibroDiario.stream()
                .filter(t-> registro ==null || !t.getId().equals(registro.getId()))
                .collect(Collectors.toList());
    }

    //instanciacion de otro campo para la sincronizacion
    public DetalleLibroDiarioFrm getDetalleLibroDiarioFrm() {
        if(this.registro != null && this.registro.getId() != null){
            detalleLibroDiarioFrm.setIdCuentaContable(registro.getId());
        }
        return detalleLibroDiarioFrm;
    }


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
}
