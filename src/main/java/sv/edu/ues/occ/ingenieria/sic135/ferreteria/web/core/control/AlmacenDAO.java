package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.Almacen;

import java.io.Serializable;

//Revisar esta clase, algo malo tiene xd

@Stateless
@LocalBean
public class AlmacenDAO extends InventarioDefaultDataAccess<Almacen, Object> implements Serializable {
    @PersistenceContext(unitName = "SistemaRestaurantePU")
    private EntityManager em;

    public AlmacenDAO() {
        super(Almacen.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<Almacen> getEntityClass() {
        return Almacen.class;
    }

    @Override
    public int count() throws IllegalStateException {
        return super.count();
    }


}
