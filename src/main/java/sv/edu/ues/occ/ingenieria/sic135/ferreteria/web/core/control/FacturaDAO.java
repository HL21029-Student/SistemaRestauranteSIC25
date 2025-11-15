package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.Factura;

import java.io.Serializable;
import java.util.UUID;

@Stateless
@LocalBean
public class FacturaDAO extends InventarioDefaultDataAccess<Factura, Object> implements Serializable {

    @PersistenceContext(unitName = "FerreteriaPU")
    private EntityManager em;

    public FacturaDAO() {
        super(Factura.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<Factura> getEntityClass() {
        return Factura.class;
    }

    @Override
    public void create(Factura entity) {
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());
        }
        super.create(entity);
    }
}
