package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.TipoProducto;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class TipoProductoDAO extends InventarioDefaultDataAccess<TipoProducto, Object>  implements Serializable {
    @PersistenceContext(unitName = "SistemaRestaurantePU")
    private EntityManager em;

    public TipoProductoDAO() {
        super (TipoProducto.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    public Class<TipoProducto> getEntityClass() {
        return TipoProducto.class;
    }

    @Override
    public TipoProducto findById(Object id) {
        return super.findById(id);
    }

    @Override
    public int count() throws IllegalStateException {
        return super.count();
    }

    public List<TipoProducto> findTiposPadre() {
        try {
            return em.createNamedQuery("TipoProducto.findTiposPadre", TipoProducto.class).getResultList();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return List.of();
        }
    }

    public List<TipoProducto> findHijosByPadre(Long idPadre) {
        try {
            return em.createNamedQuery("TipoProducto.findHijosByPadre", TipoProducto.class)
                    .setParameter("idPadre", idPadre)
                    .getResultList();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return List.of();
        }
    }

    public List<TipoProducto> findByNameLike(final String nombre , int first, int max){
        try{
            if(nombre!=null && !nombre.isBlank() && first>=0 && max>0){
                TypedQuery<TipoProducto> q = em.createNamedQuery("TipoProducto.findByNombreLike", TipoProducto.class);
                q.setParameter("nombre", "%" + nombre.trim().toUpperCase() + "%");
                q.setFirstResult(first);
                q.setMaxResults(max);
                return q.getResultList();
            }
        }catch(Exception ex){
            Logger.getLogger(TipoProductoDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return List.of();
    }

}
