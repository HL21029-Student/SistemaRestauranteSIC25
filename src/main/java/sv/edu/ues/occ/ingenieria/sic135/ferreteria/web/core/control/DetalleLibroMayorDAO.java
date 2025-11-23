package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.DetalleLibroDiario;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.DetalleLibroMayor;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.LibroMayor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class DetalleLibroMayorDAO extends  InventarioDefaultDataAccess<DetalleLibroMayor, Object> implements Serializable {
    @PersistenceContext(unitName = "FerreteriaPU")
    private EntityManager em;

    private static final Logger LOG = Logger.getLogger(DetalleLibroMayorDAO.class.getName());

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

    //obtener detalles_libro_mayor por id libro mayor
    public List<DetalleLibroMayor> findByLibroMayorId(Long libroMayorId, int first, int max){
        if(libroMayorId!=null){
            try {
                TypedQuery<DetalleLibroMayor> q = em.createNamedQuery("DetalleLibroMayor.findByLibroMayorId", DetalleLibroMayor.class);

                q.setParameter("libroMayorId", libroMayorId);
                q.setFirstResult(first);
                q.setMaxResults(max);
                return q.getResultList();
            }catch (Exception e){
                Logger.getLogger(DetalleLibroMayorDAO.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            }
        }
        return Collections.emptyList();
    }

    //funcion para calcular el saldo de correspondiente a una cuenta contable
    //con respcto a la naturaleza de la cuenta y los montos en debe y haber

    /**
     * Mayoriza iterativamente para una cuenta (por nombre) dentro de un LibroDiario.
     * - Trae los DetalleLibroDiario del libro indicado.
     * - Filtra por nombre de cuenta contable coincidente.
     * - Itera calculando saldo según reglas por tipo de cuenta.
     * - Persiste un DetalleLibroMayor con el saldo resultante asociado al LibroMayor dado.
     *
     * @param libroDiarioId id del LibroDiario (Long)
     * @param nombreCuenta nombre de la cuenta contable a mayorizar (String)
     * @param idLibroMayor id del LibroMayor donde se creará el detalle (Long)
     * @return el DetalleLibroMayor creado con el saldo calculado, o null en error
     */
    public DetalleLibroMayor mayorizarYCrearDetalle(final Long libroDiarioId, final String nombreCuenta, final Long idLibroMayor) {
        if (libroDiarioId == null || nombreCuenta == null || nombreCuenta.isBlank() || idLibroMayor == null) {
            LOG.log(Level.WARNING, "Parámetros inválidos para mayorizar");
            return null;
        }

        try {
            // 1) Traer detalles del libro diario
            TypedQuery<DetalleLibroDiario> q = em.createNamedQuery("DetalleLibroDiario.findByLibroDiarioId", DetalleLibroDiario.class);
            q.setParameter("libroDiarioId", libroDiarioId);
            List<DetalleLibroDiario> detalles = q.getResultList();

            // 2) Iterar y calcular saldo
            BigDecimal saldo = BigDecimal.ZERO;
            String buscado = nombreCuenta.trim().toLowerCase();

            for (DetalleLibroDiario d : detalles) {
                if (d == null || d.getIdCuentaContable() == null || d.getIdCuentaContable().getNombre() == null) {
                    continue;
                }
                String nombreFila = d.getIdCuentaContable().getNombre().trim().toLowerCase();
                if (!nombreFila.equals(buscado)) {
                    continue; // sólo procesar filas que coincidan
                }

                BigDecimal monto = d.getMonto() != null ? d.getMonto() : BigDecimal.ZERO;
                boolean esDebe = Boolean.TRUE.equals(d.getDebe());
                // delta = +monto si es debe, -monto si es haber
                BigDecimal delta = esDebe ? monto : monto.negate();

                // obtener tipo de cuenta (intentar nombre o código)
                String tipoCuentaTxt = "";
                try {
                    if (d.getIdCuentaContable().getIdTipoCuenta() != null) {
                        // se asume que TipoCuenta tiene getNombre() o getCodigo()
                        try {
                            tipoCuentaTxt = (String) d.getIdCuentaContable().getIdTipoCuenta().getClass()
                                    .getMethod("getNombre").invoke(d.getIdCuentaContable().getIdTipoCuenta());
                        } catch (NoSuchMethodException nsme) {
                            try {
                                tipoCuentaTxt = (String) d.getIdCuentaContable().getIdTipoCuenta().getClass()
                                        .getMethod("getCodigo").invoke(d.getIdCuentaContable().getIdTipoCuenta());
                            } catch (NoSuchMethodException ignore) {
                                tipoCuentaTxt = "";
                            }
                        }
                    }
                } catch (Exception ex) {
                    LOG.log(Level.FINE, "No se pudo leer tipo de cuenta, se usará vacío", ex);
                    tipoCuentaTxt = "";
                }
                tipoCuentaTxt = tipoCuentaTxt == null ? "" : tipoCuentaTxt.trim().toLowerCase();

                // Reglas:
                // grupoA: activo, gasto, resultado deudora, cuenta de orden => saldo = saldo + (debe - haber)  (es decir +delta)
                // grupoB: pasivo, patrimonio, ingreso, resultado acreedor => saldo = saldo - (debe) + (haber)  (es decir -delta)
                //poner en mayusculas para evitar errores
                boolean perteneceGrupoA = tipoCuentaTxt.contains("activo")
                        || tipoCuentaTxt.contains("gasto")
                        || tipoCuentaTxt.contains("resultado deudora")
                        || tipoCuentaTxt.contains("orden"); // cuenta de orden
                boolean perteneceGrupoB = tipoCuentaTxt.contains("pasivo")
                        || tipoCuentaTxt.contains("patrimonio")
                        || tipoCuentaTxt.contains("ingreso")
                        || tipoCuentaTxt.contains("resultado acreedor");

                if (perteneceGrupoA || tipoCuentaTxt.isBlank()) {
                    // si no se puede determinar, se deja en comportamiento por defecto de grupo A
                    saldo = saldo.add(delta);
                } else if (perteneceGrupoB) {
                    saldo = saldo.subtract(delta);
                } else {
                    // por seguridad, aplicar mismo comportamiento que grupo A
                    saldo = saldo.add(delta);
                }
            }

            // 3) Persistir DetalleLibroMayor con el saldo resultante
            LibroMayor libroMayor = em.find(LibroMayor.class, idLibroMayor);
            if (libroMayor == null) {
                LOG.log(Level.WARNING, "LibroMayor no encontrado con id {0}", idLibroMayor);
                return null;
            }

            DetalleLibroMayor dlm = new DetalleLibroMayor();
            dlm.setId(UUID.randomUUID());
            dlm.setSaldo(saldo);
            dlm.setIdLibroMayor(libroMayor);

            em.persist(dlm);
            em.flush();

            return dlm;
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al mayorizar", ex);
            return null;
        }
    }


}
