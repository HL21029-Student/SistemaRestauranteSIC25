package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.Kardex;

import java.io.Serializable;

@Stateless
@LocalBean
public class KardexDAO extends InventarioDefaultDataAccess<Kardex, Object> implements Serializable {
    @PersistenceContext(unitName = "SistemaRestaurantePU")
    private EntityManager em;

    public KardexDAO() {
        super(Kardex.class);
    }


    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<Kardex> getEntityClass() {
        return Kardex.class;
    }

}
