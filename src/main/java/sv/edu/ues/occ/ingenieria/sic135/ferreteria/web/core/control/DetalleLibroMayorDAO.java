package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.DetalleLibroMayor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class DetalleLibroMayorDAO extends  InventarioDefaultDataAccess<DetalleLibroMayor, Object> implements Serializable {
    @PersistenceContext(unitName = "FerreteriaPU")
    private EntityManager em;

    public DetalleLibroMayorDAO() {
        super(DetalleLibroMayor.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<DetalleLibroMayor> getEntityClass() {
        return DetalleLibroMayor.class;
    }

    //obtener detalles_libro_mayor por id libro mayor
    public List<DetalleLibroMayor> findByLibroMayorId(Long libroMayorId, int first, int max){
        if(libroMayorId!=null){
            try {
                TypedQuery<DetalleLibroMayor> q = em.createNamedQuery("DetalleLibroMayor.findByLibroMayorId", DetalleLibroMayor.class);

                q.setParameter("libroMayorId", libroMayorId);
                q.setFirstResult(first);
                q.setMaxResults(max);
                return q.getResultList();
            }catch (Exception e){
                Logger.getLogger(DetalleLibroMayorDAO.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            }
        }
        return Collections.emptyList();

    }
}
