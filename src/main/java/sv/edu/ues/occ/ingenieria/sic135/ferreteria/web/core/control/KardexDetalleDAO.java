package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.KardexDetalle;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class KardexDetalleDAO extends InventarioDefaultDataAccess<KardexDetalle, Object> implements Serializable {
    @PersistenceContext(unitName = "FerreteriaPU")
    private EntityManager em;

    public KardexDetalleDAO() {
        super(KardexDetalle.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<KardexDetalle> getEntityClass() {
        return KardexDetalle.class;
    }

    public KardexDetalle findbyId(UUID idKardexDetalle) {
        return em.find(KardexDetalle.class, idKardexDetalle);
    }

    public List<KardexDetalle> findByReferenciaLoteLike(final String lote, int first, int pageSize) {
       try{ return em.createNamedQuery("KardexDetalle.findByReferenciaLoteLike", KardexDetalle.class)
                .setParameter("lote", "%" + lote + "%")
                .setFirstResult(first)
                .setMaxResults(pageSize)
                .getResultList();
    }catch(Exception e){
           Logger.getLogger(KardexDetalleDAO.class.getName()).log(Level.SEVERE, e.getMessage(), e);
       }
       return null;
    }
}
