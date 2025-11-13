package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.LibroDiario;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class LibroDiarioDAO extends InventarioDefaultDataAccess<LibroDiario, Object> implements Serializable {
    @PersistenceContext(unitName = "FerreteriaPU")
    private EntityManager em;

    public LibroDiarioDAO(Class<LibroDiario> TipoDato) {
        super(TipoDato);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<LibroDiario> getEntityClass() {
        return LibroDiario.class;
    }

    //traer las cuentas de ajuste que le pertenezcan
    public List<LibroDiario> findDiarioAjustePadre() {
        try{
            return em.createNamedQuery("LibroDiario.findDiarioAjustePadre", LibroDiario.class).getResultList();
        }catch (Exception ex){
            Logger.getLogger(LibroDiarioDAO.class.getName()).log(Level.SEVERE, null, ex);
            return List.of();
        }
    }



}
