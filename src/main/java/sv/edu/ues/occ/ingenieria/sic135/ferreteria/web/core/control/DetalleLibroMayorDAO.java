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

    /*
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
    /*
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

     */


    /**
     * Mayoriza iterativamente para cada cuenta contable especifica dentro de un LibroDiario.
     * Solo procesa los detalles que coinciden con el nombre de cuenta contable especificado
     * @param libroDiarioId id del LibroDiario
     * @param nombreCuenta  nombre de la cuenta contable a mayorizar
     * @param idLibroMayor id del libro mayor creado con el saldo calculado, o null en error
     * @return
     */
    public DetalleLibroMayor mayorizarYCrearDetalle(final Long libroDiarioId, final String nombreCuenta, final Long idLibroMayor ){
        if (libroDiarioId == null || nombreCuenta == null || nombreCuenta.isBlank() || idLibroMayor == null) {
            LOG.log(Level.WARNING, "Parámetros inválidos para mayorizar: libroDiarioId={0}, nombreCuenta={1}, idLibroMayor={2}",
                    new Object[]{libroDiarioId, nombreCuenta, idLibroMayor});
            return null;
        }
        try{
            // 1 Traer detalles del libro diario
            TypedQuery<DetalleLibroDiario> q= em.createQuery(
                    "SELECT d FROM DetalleLibroDiario d " +
                            "WHERE d.libroDiario.id = :libroDiarioId " +
                            "AND LOWER(TRIM(d.idCuentaContable.nombre)) = LOWER(TRIM(:nombreCuenta))",
                    DetalleLibroDiario.class
            );
            q.setParameter("libroDiarioId", libroDiarioId);
            q.setParameter("nombreCuenta", nombreCuenta);
            List<DetalleLibroDiario> detalles = q.getResultList();

            // 2 Si no hay detalles que coincidan , retornara null
            if(detalles.isEmpty()){
                LOG.log(Level.INFO,"No se encontraron detalles  para la cuenta: {0} en el libro diario: {1} ",
                        new Object[]{nombreCuenta, libroDiarioId});
                return null;
            }

            // 3 Iterando y calculado saldo - inicio saldo 0
            BigDecimal saldo = BigDecimal.ZERO;

            for (DetalleLibroDiario detalle : detalles) {
                //Validando que el detalle tenga los datos necesarios
                if(detalle.getMonto() == null){
                    continue;
                }

                //Obteniendo Montos (filtrados para la cuenta seleccionada)
                BigDecimal montoDebe =Boolean.TRUE.equals(detalle.getMonto()) ? detalle.getMonto() : BigDecimal.ZERO;
                BigDecimal montoHaber = Boolean.FALSE.equals(detalle.getDebe()) ? detalle.getMonto() : BigDecimal.ZERO;

                //Obteniendo el tipo de cuenta
                String tipoCuenta = obtenerTipoCuenta(detalle);

                //Aplicando reglas contables segun el tipo de cuenta
                switch (tipoCuenta.toLowerCase()) {
                    case "activo":
                    case "gasto":
                    case "resultado deudora":
                    case "cuenta de orden":
                        // saldo + debe - haber = saldo
                        saldo = saldo.add(montoDebe).subtract(montoHaber);
                        LOG.log(Level.FINE, "Procesado [Grupo A]: Debe={0}, Haber={1}, SaldoAcumulado={2}",
                                new Object[]{montoDebe, montoHaber, saldo});
                        break;

                    case "pasivo":
                    case "patrimonio":
                    case "ingreso":
                    case "resultado acreedor":
                        // saldo - debe + haber = saldo
                        saldo = saldo.subtract(montoDebe).add(montoHaber);
                        LOG.log(Level.FINE, "Procesado [Grupo B]: Debe={0}, Haber={1}, SaldoAcumulado={2}",
                                new Object[]{montoDebe, montoHaber, saldo});
                        break;

                    default:
                        // Por defecto, aplicar regla de activo/gasto
                        saldo = saldo.add(montoDebe).subtract(montoHaber);
                        LOG.log(Level.WARNING, "Tipo de cuenta no reconocido: {0}. Aplicando regla por defecto", tipoCuenta);
                        break;
                }
            }
            return  crearDetalleLibroMayor(saldo,idLibroMayor,nombreCuenta);
        }catch (Exception ex){
            LOG.log(Level.SEVERE, "Error al mayorizar para cuenta: " + nombreCuenta + " en libro diario: " + libroDiarioId, ex);
            return null;
        }
    }
    /**
     * Metodo auxiliar DetalleLibroMayor con el saldo resultante
     * */
    private String obtenerTipoCuenta(DetalleLibroDiario detalle) {
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
                //Iterando  el metodo  getCodigo() si getNombre no existe
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
     * Metodo auxiliar para crear y persistir el DetalleLibroMayor
     */
    private DetalleLibroMayor crearDetalleLibroMayor(BigDecimal saldo, Long idLibroMayor, String nombreCuenta) {
        try {
            LibroMayor libroMayor = em.find(LibroMayor.class, idLibroMayor);
            if (libroMayor == null) {
                LOG.log(Level.WARNING, "LibroMayor no encontrado con id {0}", idLibroMayor);
                return null;
            }

            DetalleLibroMayor detalleLibroMayor = new DetalleLibroMayor();
            detalleLibroMayor.setId(UUID.randomUUID());
            detalleLibroMayor.setSaldo(saldo);
            detalleLibroMayor.setIdLibroMayor(libroMayor);
            detalleLibroMayor.setNombreCuenta(nombreCuenta != null ? nombreCuenta.trim() : null);

            em.persist(detalleLibroMayor);
            em.flush();

            LOG.log(Level.INFO, "DetalleLibroMayor creado exitosamente: Cuenta={0}, Saldo={1}",
                    new Object[]{nombreCuenta, saldo});

            return detalleLibroMayor;
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
            Logger.getLogger(DetalleLibroDiarioDAO.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}
