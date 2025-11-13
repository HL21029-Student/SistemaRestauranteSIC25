package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.DetalleLibroMayor;

import java.io.Serializable;

@Stateless
@LocalBean
public class DetalleLibroMayorDAO extends  InventarioDefaultDataAccess<DetalleLibroMayor, Object> implements Serializable {
    @PersistenceContext
    private EntityManager em;

    public DetalleLibroMayorDAO() {
        super(DetalleLibroMayor.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<DetalleLibroMayor> getEntityClass() {
        return DetalleLibroMayor.class;
    }
}
