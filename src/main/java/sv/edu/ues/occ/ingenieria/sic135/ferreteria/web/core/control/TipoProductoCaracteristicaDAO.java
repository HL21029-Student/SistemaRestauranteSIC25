package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.ProductoTipoProductoCaracteristica;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.TipoProductoCaracteristica;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class TipoProductoCaracteristicaDAO extends InventarioDefaultDataAccess<TipoProductoCaracteristica, Object> implements Serializable {

    @PersistenceContext(unitName = "FerreteriaPU")
    private EntityManager em;
    public TipoProductoCaracteristicaDAO() {
        super(TipoProductoCaracteristica.class);
    }
    @Override
    public EntityManager getEntityManager() {
        return em;
    }
    @Override
    protected Class<TipoProductoCaracteristica> getEntityClass() {
        return TipoProductoCaracteristica.class;
    }
    @Override
    public int count() throws IllegalStateException {
        return super.count();
    }

    /**
     * Buscar por idTipoProducto (usar Long para el id)
     */
    public List<TipoProductoCaracteristica> findByIdTipoProducto(final Long idTipoProducto, int first, int max) {
        if (idTipoProducto != null) {
            try {
                return em.createNamedQuery("TipoProductoCaracteristica.findByIdTipoProducto", TipoProductoCaracteristica.class)
                        .setParameter("idTipoProducto", idTipoProducto)
                        .setFirstResult(first)
                        .setMaxResults(max)
                        .getResultList();
            } catch (Exception ex) {
                Logger.getLogger(TipoProductoCaracteristicaDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        return List.of();
    }
    /**
     * Contar por idTipoProducto
     */
    public Long countByIdTipoProducto(final Long idTipoProducto) {
        if (idTipoProducto != null) {
            try {
                TypedQuery<Long> q = em.createNamedQuery("TipoProductoCaracteristica.countByIdTipoProducto", Long.class);
                q.setParameter("idTipoProducto", idTipoProducto);
                return q.getSingleResult();
            } catch (Exception ex) {
                Logger.getLogger(TipoProductoCaracteristicaDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        return 0L;
    }

    public ProductoTipoProductoCaracteristica save(ProductoTipoProductoCaracteristica entidad) {
        try {
            if (entidad.getId() == null) {
                em.persist(entidad);
                return entidad;
            } else {
                return em.merge(entidad);
            }
        } catch (Exception ex) {
            Logger.getLogger(ProductoTipoProductoCaracteristicaDAO.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public TipoProductoCaracteristica findById(final Long id) {
        if (id == null) return null;
        try {
            return em.find(TipoProductoCaracteristica.class, id);
        } catch (Exception ex) {
            Logger.getLogger(TipoProductoCaracteristicaDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return null;
        }
    }
    public List<TipoProductoCaracteristica> findObligatoriasByTipo(Long idTipoProducto) {
        if (idTipoProducto == null) return List.of();
        try {
            TypedQuery<TipoProductoCaracteristica> q = em.createNamedQuery("TipoProductoCaracteristica.findObligatoriasByTipo", TipoProductoCaracteristica.class);
            q.setParameter("idTipo", idTipoProducto);
            return q.getResultList();
        } catch (Exception e) {
            return List.of();
        }
    }
}
