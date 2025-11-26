package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.Factura;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.DetalleLibroDiario;

import java.io.Serializable;
import java.util.UUID;

@Stateless
@LocalBean
public class FacturaDAO extends InventarioDefaultDataAccess<Factura, Object> implements Serializable {

    @PersistenceContext(unitName = "SistemaRestaurantePU")
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
        try {
            // Asegurar que la relación a DetalleLibroDiario esté en estado managed y con tipo correcto
            if (entity.getIdDetalleLibroDiario() != null) {
                try {
                    java.util.UUID idDet = entity.getIdDetalleLibroDiario().getId();
                    if (idDet != null) {
                        DetalleLibroDiario managed = em.find(DetalleLibroDiario.class, idDet);
                        entity.setIdDetalleLibroDiario(managed);
                    } else {
                        // si no hay id, nulificar para evitar problemas de binding
                        entity.setIdDetalleLibroDiario(null);
                    }
                } catch (Exception e) {
                    // en caso de cualquier inconsistencia, limpiamos la relación para evitar bind incorrecto
                    entity.setIdDetalleLibroDiario(null);
                }
            }
            super.create(entity);
            // Forzar persistencia para atrapar violaciones de constraint ahora
            em.flush();
        } catch (Exception ex) {
            throw new IllegalStateException("Error al persistir Factura: " + ex.getMessage(), ex);
        }
    }
}
