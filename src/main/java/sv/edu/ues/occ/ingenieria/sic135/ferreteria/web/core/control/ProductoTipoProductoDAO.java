package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.ProductoTipoProducto;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class ProductoTipoProductoDAO extends InventarioDefaultDataAccess<ProductoTipoProducto, Object> implements Serializable {
    @PersistenceContext(unitName = "SistemaRestaurantePU")
    private EntityManager em;
    public ProductoTipoProductoDAO() {
        super(ProductoTipoProducto.class);
    }
    @Override
    public EntityManager getEntityManager() {
        return em;
    }
    @Override
    protected Class<ProductoTipoProducto> getEntityClass() {
        return ProductoTipoProducto.class;
    }

    public List<ProductoTipoProducto> findByidProducto(UUID idProducto, int first, int max) {
        if (idProducto != null) {
            try {
                TypedQuery<ProductoTipoProducto> q = em.createNamedQuery("ProductoTipoProducto.findByIdProducto", ProductoTipoProducto.class);
                q.setParameter("idProducto", idProducto);
                q.setFirstResult(first);
                q.setMaxResults(max);
                return q.getResultList();
            } catch (Exception ex) {
                Logger.getLogger(ProductoTipoProductoDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return Collections.emptyList();
    }
    public Long countByidProducto(UUID idProducto) {
        if (idProducto != null) {
            try {
                TypedQuery<Long> q = em.createNamedQuery("ProductoTipoProducto.countByIdProducto", Long.class);
                q.setParameter("idProducto", idProducto);
                return q.getSingleResult();
            } catch (Exception ex) {
                Logger.getLogger(ProductoTipoProductoDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 0L;
    }
}
