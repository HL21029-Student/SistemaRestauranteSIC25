package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.Caracteristica;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class CaracteristicaDAO extends InventarioDefaultDataAccess<Caracteristica, Object> implements Serializable {
    @PersistenceContext(unitName = "SistemaRestaurantePU")
    private EntityManager em;

    public CaracteristicaDAO() {
        super(Caracteristica.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<Caracteristica> getEntityClass() {
        return Caracteristica.class;
    }

    @Override
    public int count() throws IllegalStateException {
        return super.count();
    }

    public List<Caracteristica> findByIdCaracteristica(Integer idCaracteristica, int first, int max) {
        if (idCaracteristica != null) {
            try {
                TypedQuery<Caracteristica> q = em.createNamedQuery("Caracteristica.findByIdCaracteristica", Caracteristica.class);
                q.setParameter("id", idCaracteristica);
                q.setFirstResult(first);
                q.setMaxResults(max);
                return q.getResultList();
            } catch (Exception ex) {
                Logger.getLogger(CaracteristicaDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        return List.of();
    }



    public List<Caracteristica> findByNombreLike(final String nombre , int first, int max){
        try{
            if(nombre!=null && !nombre.isBlank() && first>=0 && max>0){
                TypedQuery<Caracteristica> q = em.createNamedQuery("Caracteristica.findByNombreLike", Caracteristica.class);
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
