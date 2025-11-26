package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.AsientoKardex;

import java.io.Serializable;

@Stateless
@LocalBean
public class AsientoKardexDAO extends InventarioDefaultDataAccess<AsientoKardex, Object> implements Serializable {
    @PersistenceContext(unitName = "SistemaRestaurantePU")
    private EntityManager em;

    public AsientoKardexDAO() {
        super(AsientoKardex.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    public Class<AsientoKardex> getEntityClass() {
        return AsientoKardex.class;
    }



}
