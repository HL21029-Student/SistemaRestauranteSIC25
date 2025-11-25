package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.DetalleLibroDiario;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class DetalleLibroDiarioDAO extends InventarioDefaultDataAccess<DetalleLibroDiario, Object> implements Serializable {

    @PersistenceContext(unitName = "FerreteriaPU")
    private EntityManager em;

    public DetalleLibroDiarioDAO() {
        super(DetalleLibroDiario.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<DetalleLibroDiario> getEntityClass() {
        return DetalleLibroDiario.class;
    }

    public List<DetalleLibroDiario> findByLibroDiarioId(Long libroDiarioId, int first, int max){
        if(libroDiarioId != null){
            try {
                String jpql = "SELECT d FROM DetalleLibroDiario d WHERE d.libroDiario.id = :libroDiarioId ORDER BY d.fecha DESC";
                TypedQuery<DetalleLibroDiario> q = em.createQuery(jpql, DetalleLibroDiario.class);
                q.setParameter("libroDiarioId", libroDiarioId);
                q.setFirstResult(first);
                q.setMaxResults(max);
                return q.getResultList();
            } catch (Exception e){
                Logger.getLogger(DetalleLibroDiarioDAO.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            }
        }
        return Collections.emptyList();
    }

    public Long countByLibroDiarioId(final Long libroDiarioId){
        if(libroDiarioId != null){
            try {
                String jpql = "SELECT COUNT(d) FROM DetalleLibroDiario d WHERE d.libroDiario.id = :libroDiarioId";
                TypedQuery<Long> q = em.createQuery(jpql, Long.class);
                q.setParameter("libroDiarioId", libroDiarioId);
                return q.getSingleResult();
            } catch (Exception e){
                Logger.getLogger(DetalleLibroDiarioDAO.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            }
        }
        return 0L;
    }

    public List<DetalleLibroDiario> findByCuentaContableId(Long cuentaContableId, int first, int max){
        if(cuentaContableId != null){
            try {
                String jpql = "SELECT d FROM DetalleLibroDiario d WHERE d.idCuentaContable.id = :cuentaContableId";
                TypedQuery<DetalleLibroDiario> q = em.createQuery(jpql, DetalleLibroDiario.class);
                q.setParameter("cuentaContableId", cuentaContableId);
                q.setFirstResult(first);
                q.setMaxResults(max);
                return q.getResultList();
            } catch (Exception e){
                Logger.getLogger(DetalleLibroDiarioDAO.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            }
        }
        return Collections.emptyList();
    }

    public Long countByCuentaContableId(final Long cuentaContableId){
        if(cuentaContableId != null){
            try {
                String jpql = "SELECT COUNT(d) FROM DetalleLibroDiario d WHERE d.idCuentaContable.id = :cuentaContableId";
                TypedQuery<Long> q = em.createQuery(jpql, Long.class);
                q.setParameter("cuentaContableId", cuentaContableId);
                return q.getSingleResult();
            } catch (Exception e){
                Logger.getLogger(DetalleLibroDiarioDAO.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            }
        }
        return 0L;
    }
}