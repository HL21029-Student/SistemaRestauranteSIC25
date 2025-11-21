package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
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

    public LibroDiarioDAO() {
        super(LibroDiario.class);
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
            Logger.getLogger(LibroDiarioDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return List.of();
        }
    }

    //findDiarioAjusteHijos
    public List<LibroDiario> findDiarioAjusteHijos(Long idPadre) {
        try {
            return em.createNamedQuery("LibroDiario.findDiarioAjusteHijos", LibroDiario.class)
                    .setParameter("idPadre", idPadre)
                    .getResultList();
        } catch (Exception ex) {
            Logger.getLogger(LibroDiarioDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return List.of();
        }
    }

    //findByNameLike
    public List<LibroDiario> findByNameLike(String nombre, int first, int max) {
        try{
            if(nombre!=null && !nombre.isBlank() && first<=0 && max>0){
                TypedQuery<LibroDiario> q = em.createNamedQuery("LibroDiario.findByNameLike", LibroDiario.class);
                q.setParameter("nombre", "%" + nombre.trim().toUpperCase() + "%");
                q.setFirstResult(first);
                q.setMaxResults(max);
                return q.getResultList();
            }
        }catch (Exception ex){
            Logger.getLogger(LibroDiarioDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return List.of();
    }

    public LibroDiario findById(final Long id) {
        if(id == null){
            return null;
        }
        try {
            return em.find(LibroDiario.class, id);
        } catch (Exception ex) {
            Logger.getLogger(LibroDiarioDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return null;
        }
    }


}
