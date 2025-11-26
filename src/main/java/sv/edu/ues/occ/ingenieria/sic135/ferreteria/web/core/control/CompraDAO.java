package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.Compra;

import java.io.Serializable;

@Stateless
@LocalBean
public class CompraDAO extends InventarioDefaultDataAccess<Compra, Object> implements Serializable {

    @PersistenceContext(unitName = "SistemaRestaurantePU")
    private EntityManager em;

    public CompraDAO() {
        super(Compra.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<Compra> getEntityClass() {
        return Compra.class;
    }

    @Override
    public int count() throws IllegalStateException {
        return super.count();
    }

}
