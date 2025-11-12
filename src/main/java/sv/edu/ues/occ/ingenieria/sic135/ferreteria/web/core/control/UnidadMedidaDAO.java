package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.UnidadMedida;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class UnidadMedidaDAO extends InventarioDefaultDataAccess<UnidadMedida, Object> implements Serializable {
    @PersistenceContext(unitName = "FerreteriaPU")
    private EntityManager em;

    public UnidadMedidaDAO() {
        super(UnidadMedida.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<UnidadMedida> getEntityClass() {
        return UnidadMedida.class;
    }

    public List<UnidadMedida> findByIdTipoUnidadMedida(Integer idTipoUnidadMedida, int first, int max) {
        if (idTipoUnidadMedida != null) {
            try {
                TypedQuery<UnidadMedida> q = em.createNamedQuery("UnidadMedida.findByIdTipoUnidadMedida", UnidadMedida.class);
                q.setParameter("idTipoUnidadMedida", idTipoUnidadMedida);
                q.setFirstResult(first);
                q.setMaxResults(max);
                return q.getResultList();
            } catch (Exception ex) {
                Logger.getLogger(ProductoTipoProductoDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return Collections.emptyList();
    }

    public Long countByIdTipoUnidadMedida(Integer idTipoUnidadMedida) {
        if (idTipoUnidadMedida != null) {
            try {
                TypedQuery<Long> q = em.createNamedQuery("UnidadMedida.countByIdTipoUnidadMedida", Long.class);
                q.setParameter("idTipoUnidadMedida", idTipoUnidadMedida);
                return q.getSingleResult();
            } catch (Exception ex) {
                Logger.getLogger(ProductoTipoProductoDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 0L;
    }
}
