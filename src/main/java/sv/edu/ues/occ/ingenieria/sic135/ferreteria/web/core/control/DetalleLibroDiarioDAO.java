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
public class DetalleLibroDiarioDAO extends  InventarioDefaultDataAccess<DetalleLibroDiario, Object> implements Serializable {
    @PersistenceContext(unitName = "FerreteriaPU")
    private EntityManager em;

    public DetalleLibroDiarioDAO(Class<DetalleLibroDiario> TipoDato) {
        super(TipoDato);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<DetalleLibroDiario> getEntityClass() {
        return DetalleLibroDiario.class;
    }

    //buscar por id libro diario
    public List<DetalleLibroDiario> findByLibroDiarioId(Long libroDiarioId, int first, int max){
        if(libroDiarioId!=null){
            try {
                TypedQuery<DetalleLibroDiario> q = em.createNamedQuery("DetalleLibroDiario.findByLibroDiarioId", DetalleLibroDiario.class);

                q.setParameter("libroDiarioId", libroDiarioId);
                q.setFirstResult(first);
                q.setMaxResults(max);
                return q.getResultList();
            }catch (Exception e){
                Logger.getLogger(DetalleLibroDiarioDAO.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            }
        }
        return Collections.emptyList();

    }
}
