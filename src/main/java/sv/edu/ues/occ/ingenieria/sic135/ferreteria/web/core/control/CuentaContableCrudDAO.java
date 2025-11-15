package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.CuentaContable;

import java.io.Serializable;

@Stateless
@LocalBean
public class CuentaContableCrudDAO extends InventarioDefaultDataAccess<CuentaContable, Object> implements Serializable {

    @PersistenceContext(unitName = "FerreteriaPU")
    private EntityManager em;

    public CuentaContableCrudDAO() {
        super(CuentaContable.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<CuentaContable> getEntityClass() {
        return CuentaContable.class;
    }
}

