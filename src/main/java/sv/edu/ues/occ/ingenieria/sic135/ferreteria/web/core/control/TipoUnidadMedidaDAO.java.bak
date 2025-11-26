package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.TipoUnidadMedida;

import java.io.Serializable;

@Stateless
@LocalBean
public class TipoUnidadMedidaDAO extends InventarioDefaultDataAccess<TipoUnidadMedida, Object> implements Serializable {

    @PersistenceContext(unitName = "FerreteriaPU")
    private EntityManager em;

    public TipoUnidadMedidaDAO() {
        super(TipoUnidadMedida.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }
    @Override
    public Class<TipoUnidadMedida> getEntityClass() {
        return TipoUnidadMedida.class;
    }

}
