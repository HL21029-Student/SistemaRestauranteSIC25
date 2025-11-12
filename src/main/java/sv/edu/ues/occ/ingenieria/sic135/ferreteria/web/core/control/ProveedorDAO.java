package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.Proveedor;

import java.io.Serializable;

@Stateless
@LocalBean
public class ProveedorDAO extends InventarioDefaultDataAccess<Proveedor, Object> implements Serializable {

    @PersistenceContext(unitName = "FerreteriaPU")
    private EntityManager em;

    public ProveedorDAO() {
        super(Proveedor.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<Proveedor> getEntityClass() {
        return Proveedor.class;
    }

}
