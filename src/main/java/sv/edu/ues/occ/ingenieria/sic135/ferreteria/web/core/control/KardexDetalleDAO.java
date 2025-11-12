package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.KardexDetalle;

import java.io.Serializable;

@Stateless
@LocalBean
public class KardexDetalleDAO extends InventarioDefaultDataAccess<KardexDetalle, Object> implements Serializable {
    @PersistenceContext(unitName = "FerreteriaPU")
    private EntityManager em;

    public KardexDetalleDAO() {
        super(KardexDetalle.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<KardexDetalle> getEntityClass() {
        return KardexDetalle.class;
    }

}
