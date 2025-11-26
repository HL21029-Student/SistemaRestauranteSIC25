package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.Venta;

import java.io.Serializable;

@Stateless
@LocalBean
public class VentaDAO extends InventarioDefaultDataAccess<Venta, Object> implements Serializable {

    @PersistenceContext(unitName = "SistemaRestaurantePU")
    private EntityManager em;

    public VentaDAO() {
        super(Venta.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<Venta> getEntityClass() {
        return Venta.class;
    }

    @Override
    public int count() throws IllegalStateException {
        return super.count();
    }
}