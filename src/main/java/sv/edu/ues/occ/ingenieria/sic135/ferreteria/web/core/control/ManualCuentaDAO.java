package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.ManualCuenta;

import java.io.Serializable;

@Stateless
@LocalBean
public class ManualCuentaDAO extends InventarioDefaultDataAccess<ManualCuenta, Object> implements Serializable {

    @PersistenceContext(unitName = "FerreteriaPU")
    private EntityManager em;

    public ManualCuentaDAO() {
        super(ManualCuenta.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<ManualCuenta> getEntityClass() {
        return ManualCuenta.class;
    }

    public ManualCuenta findByCuentaContableId(Long idCuenta) {
        if (idCuenta == null) {
            return null;
        }
        try {
            return em.createQuery("SELECT m FROM ManualCuenta m WHERE m.idCuentaContable.id = :idCuenta", ManualCuenta.class)
                    .setParameter("idCuenta", idCuenta)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
