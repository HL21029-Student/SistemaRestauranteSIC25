package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.TipoAlmacen;
import java.io.Serializable;

@Stateless
@LocalBean
public class TipoAlmacenDAO extends InventarioDefaultDataAccess<TipoAlmacen, Object> implements Serializable {
    @PersistenceContext(unitName = "FerreteriaPU")
    private EntityManager em;

    public TipoAlmacenDAO() {
        super(TipoAlmacen.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    public Class<TipoAlmacen> getEntityClass() {
        return TipoAlmacen.class;
    }

    @Override
    public TipoAlmacen findById(Object id) {
        return super.findById(id);
    }

    @Override
    public int count() throws IllegalStateException {
        return super.count();
    }

}
