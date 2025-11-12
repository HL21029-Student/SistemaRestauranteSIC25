package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf.VentaFrm;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.VentaDetalle;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class VentaDetalleDAO extends InventarioDefaultDataAccess<VentaDetalle, Object> implements Serializable {
    @PersistenceContext(unitName = "FerreteriaPU")
    private EntityManager em;

    public VentaDetalleDAO() {
        super(VentaDetalle.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<VentaDetalle> getEntityClass() {
        return VentaDetalle.class;
    }

    //buscar por idVenta<---------------------------------------------------------------------------------------------------------
    public List<VentaDetalle> findByIdVenta(UUID idVenta, int first, int max) {
        if (idVenta != null) {
            try {
                TypedQuery<VentaDetalle> q = em.createNamedQuery("VentaDetalle.findByIdVenta", VentaDetalle.class);
                q.setParameter("idVenta", idVenta);
                q.setFirstResult(first);
                q.setMaxResults(max);
                return q.getResultList();
            } catch (Exception ex) {
                throw new IllegalStateException("Parametro no valido", ex);
            }
        }
        return List.of();
    }

    //contar por idVenta<---------------------------------------------------------------------------------------------------------
    public int countByIdVenta(UUID idVenta) {
        if (idVenta != null) {
            try {
                TypedQuery<Long> q = em.createNamedQuery("VentaDetalle.countByIdVenta", Long.class);
                q.setParameter("idVenta", idVenta);
                return ((Long) q.getSingleResult()).intValue();
            } catch (Exception ex) {
                throw new IllegalStateException("Parametro no valido", ex);
            }
        }
        return 0;
    }

    public java.math.BigDecimal calcularMontoTotal(java.util.List<VentaDetalle> detalles) {
        java.math.BigDecimal total = java.math.BigDecimal.ZERO;
        if (detalles == null || detalles.isEmpty()) return total;
        try {
            for (VentaDetalle d : detalles) {
                java.math.BigDecimal cantidadBd = java.math.BigDecimal.ZERO;
                try {
                    Object c = d.getCantidad();
                    if (c instanceof Number) {
                        cantidadBd = java.math.BigDecimal.valueOf(((Number) c).doubleValue());
                    } else {
                        cantidadBd = new java.math.BigDecimal(c == null ? "0" : c.toString());
                    }
                } catch (Exception ex) {
                    cantidadBd = java.math.BigDecimal.ZERO;
                }

                java.math.BigDecimal precioBd = java.math.BigDecimal.ZERO;
                try {
                    Object p = d.getPrecio();
                    if (p != null) {
                        precioBd = (java.math.BigDecimal) p;
                    } else if (p instanceof Number) {
                        precioBd = java.math.BigDecimal.valueOf(((Number) p).doubleValue());
                    } else {
                        precioBd = new java.math.BigDecimal(p == null ? "0" : p.toString());
                    }
                } catch (Exception ex) {
                    precioBd = java.math.BigDecimal.ZERO;
                }

                total = total.add(precioBd.multiply(cantidadBd));
            }
        } catch (Exception ex) {
            Logger.getLogger(VentaFrm.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return total;
    }


}