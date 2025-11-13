package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.DetalleLibroDiario;

import java.io.Serializable;

@Stateless
@LocalBean
public class DetalleLibroDiarioDAO extends  InventarioDefaultDataAccess<DetalleLibroDiario, Object> implements Serializable {
    @PersistenceContext(unitName = "FerreteriaPU")
    private EntityManager em;

    public DetalleLibroDiarioDAO(Class<DetalleLibroDiario> TipoDato) {
        super(TipoDato);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<DetalleLibroDiario> getEntityClass() {
        return DetalleLibroDiario.class;
    }
}
