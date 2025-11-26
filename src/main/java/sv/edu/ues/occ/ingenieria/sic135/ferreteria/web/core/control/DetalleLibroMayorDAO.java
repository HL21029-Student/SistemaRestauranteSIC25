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
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class DetalleLibroMayorDAO extends InventarioDefaultDataAccess<DetalleLibroMayor, Object> implements Serializable {
    @PersistenceContext(unitName = "SistemaRestaurantePU")
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

    // Obtener detalles_libro_mayor por id libro mayor
    public List<DetalleLibroMayor> findByLibroMayorId(Long libroMayorId, int first, int max) {
        if (libroMayorId != null) {
            try {
                TypedQuery<DetalleLibroMayor> q = em.createNamedQuery("DetalleLibroMayor.findByLibroMayorId", DetalleLibroMayor.class);

                q.setParameter("libroMayorId", libroMayorId);
                q.setFirstResult(first);
                q.setMaxResults(max);
                return q.getResultList();
            } catch (Exception e) {
                Logger.getLogger(DetalleLibroMayorDAO.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            }
        }
        return Collections.emptyList();
    }

    public boolean existeCuentaEnLibroMayor(Long libroMayorId, String codigoCuenta) {
        try {
            String queryStr = "SELECT COUNT(d) FROM DetalleLibroMayor d " +
                    "WHERE d.idLibroMayor.id = :libroMayorId " +
                    "AND d.nombreCuenta LIKE :codigoCuentaPattern";

            Long count = em.createQuery(queryStr, Long.class)
                    .setParameter("libroMayorId", libroMayorId)
                    .setParameter("codigoCuentaPattern", codigoCuenta + "%")
                    .getSingleResult();

            return count > 0;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error al verificar cuenta mayorizada", e);
            return false;
        }
    }

    /**
     * Metodo para mayoriza por partidas contables
     */
    public DetalleLibroMayor mayorizarYCrearDetalle(final Long libroDiarioId, final String nombreCuenta, final Long idLibroMayor) {
        if (libroDiarioId == null || nombreCuenta == null || nombreCuenta.isBlank() || idLibroMayor == null) {
            LOG.log(Level.WARNING, "Parámetros inválidos para mayorizar: libroDiarioId={0}, nombreCuenta={1}, idLibroMayor={2}",
                    new Object[]{libroDiarioId, nombreCuenta, idLibroMayor});
            return null;
        }

        try {
            // Validar equilibrio contable
            if (!validarPartidaDoble(libroDiarioId)) {
                LOG.log(Level.WARNING, "No se puede mayorizar debido a desbalance contable en libro diario: {0}", libroDiarioId);
                return null;
            }

            List<Long> partidasIds = obtenerPartidasConCuenta(libroDiarioId, nombreCuenta);

            if (partidasIds.isEmpty()) {
                LOG.log(Level.INFO, "No se encontraron partidas para la cuenta: {0} en el libro diario: {1}",
                        new Object[]{nombreCuenta, libroDiarioId});
                return null;
            }

            LOG.log(Level.INFO, "Iniciando mayorización para cuenta: {0} con {1} partidas",
                    new Object[]{nombreCuenta, partidasIds.size()});

            BigDecimal saldoFinal = BigDecimal.ZERO;
            int partidasProcesadas = 0;

            for (Long partidaId : partidasIds) {
                BigDecimal saldoPartida = procesarPartidaCompleta(partidaId, nombreCuenta, saldoFinal);
                saldoFinal = saldoPartida;
                partidasProcesadas++;

                LOG.log(Level.FINE, "Partida {0} procesada - Saldo acumulado: {1}",
                        new Object[]{partidaId, saldoFinal});
            }

            LOG.log(Level.INFO, "✅ Mayorización completada - Cuenta: {0}, Partidas: {1}, Saldo Final: {2}",
                    new Object[]{nombreCuenta, partidasProcesadas, saldoFinal});

            return crearDetalleLibroMayor(saldoFinal, idLibroMayor, nombreCuenta);

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "❌ Error al mayorizar para cuenta: " + nombreCuenta + " en libro diario: " + libroDiarioId, ex);
            return null;
        }
    }

    /**
     * Metodo para obtener IDs de partidas que involucran la cuenta específica
     */
    private List<Long> obtenerPartidasConCuenta(Long libroDiarioId, String nombreCuenta) {
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT DISTINCT d.numeroPartida FROM DetalleLibroDiario d " +
                            "WHERE d.libroDiario.id = :libroDiarioId " +
                            "AND LOWER(TRIM(d.idCuentaContable.nombre)) = LOWER(TRIM(:nombreCuenta)) " +
                            "ORDER BY d.numeroPartida",
                    Long.class
            );
            query.setParameter("libroDiarioId", libroDiarioId);
            query.setParameter("nombreCuenta", nombreCuenta);

            return query.getResultList();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al obtener partidas para cuenta: " + nombreCuenta, ex);
            return Collections.emptyList();
        }
    }

    /**
     * Metodo para procesar una partida completa y calcular su efecto en el saldo
     */
    private BigDecimal procesarPartidaCompleta(Long numeroPartida, String nombreCuenta, BigDecimal saldoActual) {
        try {
            TypedQuery<DetalleLibroDiario> query = em.createQuery(
                    "SELECT d FROM DetalleLibroDiario d " +
                            "WHERE d.numeroPartida = :numeroPartida " +
                            "AND LOWER(TRIM(d.idCuentaContable.nombre)) = LOWER(TRIM(:nombreCuenta)) " +
                            "ORDER BY d.id",
                    DetalleLibroDiario.class
            );
            query.setParameter("numeroPartida", numeroPartida);
            query.setParameter("nombreCuenta", nombreCuenta);

            List<DetalleLibroDiario> detalles = query.getResultList();

            if (detalles.isEmpty()) {
                return saldoActual;
            }

            BigDecimal saldoPartida = saldoActual;

            // Procesar cada detalle de la partida para esta cuenta específica
            for (DetalleLibroDiario detalle : detalles) {
                if (detalle.getMonto() == null || detalle.getDebe() == null) {
                    LOG.log(Level.WARNING, "Detalle omitido en partida {0} - Datos incompletos", numeroPartida);
                    continue;
                }

                BigDecimal montoDebe = Boolean.TRUE.equals(detalle.getDebe()) ? detalle.getMonto() : BigDecimal.ZERO;
                BigDecimal montoHaber = Boolean.FALSE.equals(detalle.getDebe()) ? detalle.getMonto() : BigDecimal.ZERO;

                String tipoCuenta = obtenerTipoCuenta(detalle);
                saldoPartida = aplicarReglaContable(saldoPartida, montoDebe, montoHaber, tipoCuenta, detalle.getId());
            }

            return saldoPartida;

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al procesar partida: " + numeroPartida + " para cuenta: " + nombreCuenta, ex);
            return saldoActual;
        }
    }

    /**
     * Metodo para aplicar reglas contables de forma centralizada
     */
    private BigDecimal aplicarReglaContable(BigDecimal saldoActual, BigDecimal montoDebe, BigDecimal montoHaber,
                                            String tipoCuenta, UUID detalleId) {

        BigDecimal saldoAnterior = saldoActual;
        BigDecimal saldoNuevo;

        if (tipoCuenta == null || tipoCuenta.isBlank()) {
            tipoCuenta = "default";
        }

        switch (tipoCuenta.toLowerCase()) {
            case "activo":
            case "gasto":
            case "resultado deudora":
            case "cuenta de orden":
                // saldo + debe - haber
                saldoNuevo = saldoActual.add(montoDebe).subtract(montoHaber);
                LOG.log(Level.FINEST, "Detalle {0} [Grupo A]: Debe={1}, Haber={2}, Saldo {3} -> {4}",
                        new Object[]{detalleId, montoDebe, montoHaber, saldoAnterior, saldoNuevo});
                break;

            case "pasivo":
            case "patrimonio":
            case "ingreso":
            case "resultado acreedor":
                // : saldo - debe + haber
                saldoNuevo = saldoActual.subtract(montoDebe).add(montoHaber);
                LOG.log(Level.FINEST, "Detalle {0} [Grupo B]: Debe={1}, Haber={2}, Saldo {3} -> {4}",
                        new Object[]{detalleId, montoDebe, montoHaber, saldoAnterior, saldoNuevo});
                break;

            default:
                // saldo + debe - haber
                saldoNuevo = saldoActual.add(montoDebe).subtract(montoHaber);
                LOG.log(Level.WARNING, "Detalle {0} [Default]: Tipo cuenta '{1}' no reconocido. Debe={2}, Haber={3}, Saldo {4} -> {5}",
                        new Object[]{detalleId, tipoCuenta, montoDebe, montoHaber, saldoAnterior, saldoNuevo});
                break;
        }

        return saldoNuevo;
    }

    /**
     * Metodo para validar partida doble
     */
    private boolean validarPartidaDoble(Long libroDiarioId) {
        try {
            String query = "SELECT " +
                    "COALESCE(SUM(CASE WHEN d.debe = true THEN d.monto ELSE 0 END), 0), " +
                    "COALESCE(SUM(CASE WHEN d.debe = false THEN d.monto ELSE 0 END), 0) " +
                    "FROM DetalleLibroDiario d WHERE d.libroDiario.id = :libroDiarioId";

            Object[] resultado = em.createQuery(query, Object[].class)
                    .setParameter("libroDiarioId", libroDiarioId)
                    .getSingleResult();

            BigDecimal totalDebe = (BigDecimal) resultado[0];
            BigDecimal totalHaber = (BigDecimal) resultado[1];

            boolean balanceado = totalDebe.compareTo(totalHaber) == 0;

            if (!balanceado) {
                LOG.log(Level.WARNING, " DESBALANCE CONTABLE - LibroDiario ID: {0}", libroDiarioId);
                LOG.log(Level.WARNING, "Total Débito: {0}, Total Crédito: {1}, Diferencia: {2}",
                        new Object[]{totalDebe, totalHaber, totalDebe.subtract(totalHaber).abs()});
            } else {
                LOG.log(Level.INFO, "✓ Partida doble validada - LibroDiario ID: {0}, Débito: {1}, Crédito: {2}",
                        new Object[]{libroDiarioId, totalDebe, totalHaber});
            }

            return balanceado;

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al validar partida doble para libro diario: " + libroDiarioId, ex);
            return false;
        }
    }

    /**
     * Metodo para obtener tipo de cuenta
     */
    private String obtenerTipoCuenta(DetalleLibroDiario detalle) {
        try {
            if (detalle == null || detalle.getIdCuentaContable() == null) {
                return "default";
            }

            String tipoCuenta = obtenerTipoCuentaReflection(detalle);
            return tipoCuenta != null ? tipoCuenta.trim() : "default";

        } catch (Exception ex) {
            LOG.log(Level.WARNING, "Error al obtener tipo de cuenta para detalle ID: {0}",
                    detalle != null ? detalle.getId() : "null");
            return "default";
        }
    }

    /**
     * Metodo auxiliar para obtener tipo de cuenta usando relection
     */
    private String obtenerTipoCuentaReflection(DetalleLibroDiario detalle) {
        if (detalle == null || detalle.getIdCuentaContable() == null ||
                detalle.getIdCuentaContable().getIdTipoCuenta() == null) {
            return "";
        }

        try {
            Object tipoCuenta = detalle.getIdCuentaContable().getIdTipoCuenta();

            // Intentar metodo getNombre
            try {
                java.lang.reflect.Method metodoGetNombre = tipoCuenta.getClass().getMethod("getNombre");
                Object resultado = metodoGetNombre.invoke(tipoCuenta);
                if (resultado instanceof String) {
                    return ((String) resultado).trim();
                }
            } catch (NoSuchMethodException e) {
                // Intentar el metodo getCodigo() si getNombre no existe
                try {
                    java.lang.reflect.Method metodoGetCodigo = tipoCuenta.getClass().getMethod("getCodigo");
                    Object resultado = metodoGetCodigo.invoke(tipoCuenta);
                    if (resultado instanceof String) {
                        return ((String) resultado).trim();
                    }
                } catch (NoSuchMethodException e2) {
                    LOG.log(Level.FINE, "No se encontraron métodos getNombre ni getCodigo en TipoCuenta");
                }
            }
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "Error al obtener tipo de cuenta", ex);
        }

        return "";
    }

    /**
     * ✅ Crear detalle con validaciones adicionales
     */
    private DetalleLibroMayor crearDetalleLibroMayor(BigDecimal saldo, Long idLibroMayor, String nombreCuenta) {
        try {
            if (saldo == null || idLibroMayor == null || nombreCuenta == null) {
                LOG.log(Level.WARNING, "No se puede crear detalle - parámetros inválidos");
                return null;
            }

            LibroMayor libroMayor = em.find(LibroMayor.class, idLibroMayor);
            if (libroMayor == null) {
                LOG.log(Level.WARNING, "No se encontró LibroMayor con ID: {0}", idLibroMayor);
                return null;
            }

            DetalleLibroMayor detalle = new DetalleLibroMayor();
            detalle.setId(UUID.randomUUID());
            detalle.setSaldo(saldo);
            detalle.setIdLibroMayor(libroMayor);
            detalle.setNombreCuenta(nombreCuenta);

            em.persist(detalle);

            LOG.log(Level.INFO, "✓ DetalleLibroMayor creado - Cuenta: {0}, Saldo: {1}, LibroMayor ID: {2}",
                    new Object[]{nombreCuenta, saldo, idLibroMayor});

            return detalle;

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al crear DetalleLibroMayor", ex);
            return null;
        }
    }

    /**
     * Actualizar un detalle de libro mayor
     */
    public void edit(DetalleLibroMayor detalle) {
        try {
            if (detalle != null) {
                em.merge(detalle);
                em.flush();
                LOG.log(Level.INFO, "DetalleLibroMayor actualizado: {0}", detalle.getId());
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al actualizar DetalleLibroMayor", ex);
            throw new RuntimeException("Error al actualizar detalle", ex);
        }
    }

    /**
     * Eliminar un detalle de libro mayor
     */
    public void remove(DetalleLibroMayor detalle) {
        try {
            if (detalle != null) {
                if (!em.contains(detalle)) {
                    detalle = em.merge(detalle);
                }
                em.remove(detalle);
                em.flush();
                LOG.log(Level.INFO, "DetalleLibroMayor eliminado: {0}", detalle.getId());
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al eliminar DetalleLibroMayor", ex);
            throw new RuntimeException("Error al eliminar detalle", ex);
        }
    }

    /**
     * Encuentra todas las cuentas contables únicas en un libro diario
     */
    public List<String> findCuentasUnicasByLibroDiario(Long libroDiarioId) {
        try {
            TypedQuery<String> q = em.createQuery(
                    "SELECT DISTINCT d.idCuentaContable.nombre FROM DetalleLibroDiario d " +
                            "WHERE d.libroDiario.id = :libroDiarioId " +
                            "AND d.idCuentaContable.nombre IS NOT NULL",
                    String.class
            );
            q.setParameter("libroDiarioId", libroDiarioId);
            return q.getResultList();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error al obtener cuentas únicas del libro diario", e);
            return Collections.emptyList();
        }
    }
}