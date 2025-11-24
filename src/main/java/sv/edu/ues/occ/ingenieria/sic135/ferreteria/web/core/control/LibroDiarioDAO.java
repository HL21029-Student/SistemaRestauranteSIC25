package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.CuentaContable;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.LibroDiario;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class LibroDiarioDAO extends InventarioDefaultDataAccess<LibroDiario, Object> implements Serializable {
    @PersistenceContext(unitName = "FerreteriaPU")
    private EntityManager em;

    public LibroDiarioDAO() {
        super(LibroDiario.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<LibroDiario> getEntityClass() {
        return LibroDiario.class;
    }

    //traer las cuentas de ajuste que le pertenezcan
    public List<LibroDiario> findDiarioAjustePadre() {
        try{
            return em.createNamedQuery("LibroDiario.findDiarioAjustePadre", LibroDiario.class).getResultList();
        }catch (Exception ex){
            Logger.getLogger(LibroDiarioDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return List.of();
        }
    }

    public List<LibroDiario> findDiarioAjusteHijos(Long idPadre) {
        try {
            return em.createNamedQuery("LibroDiario.findDiarioAjusteHijos", LibroDiario.class)
                    .setParameter("idPadre", idPadre)
                    .getResultList();
        } catch (Exception ex) {
            Logger.getLogger(LibroDiarioDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return List.of();
        }
    }

    public List<LibroDiario> findByNameLike(String nombre, int first, int max) {
        try{
            if(nombre!=null && !nombre.isBlank() && first<=0 && max>0){
                TypedQuery<LibroDiario> q = em.createNamedQuery("LibroDiario.findByNameLike", LibroDiario.class);
                q.setParameter("nombre", "%" + nombre.trim().toUpperCase() + "%");
                q.setFirstResult(first);
                q.setMaxResults(max);
                return q.getResultList();
            }
        }catch (Exception ex){
            Logger.getLogger(LibroDiarioDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return List.of();
    }

    public LibroDiario findById(final Long id) {
        if(id == null){
            return null;
        }
        try {
            return em.find(LibroDiario.class, id);
        } catch (Exception ex) {
            Logger.getLogger(LibroDiarioDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return null;
        }
    }

    /**
     * Encuentra cuentas únicas y sus saldos para un libro diario específico
     * Usando la estructura correcta con DetalleLibroDiario
     */
    public List<Object[]> findCuentasUnicasByLibroDiario(Long libroDiarioId, String filtroNombre) {
        try {
            String sql = """
            SELECT 
                cc.codigo,
                cc.nombre,
                SUM(CASE WHEN dld.debe = true THEN dld.monto ELSE 0.0 END) as total_debe,
                SUM(CASE WHEN dld.debe = false THEN dld.monto ELSE 0.0 END) as total_haber,
                (SUM(CASE WHEN dld.debe = true THEN dld.monto ELSE 0.0 END) - 
                 SUM(CASE WHEN dld.debe = false THEN dld.monto ELSE 0.0 END)) as saldo
            FROM detalle_libro_diario dld
            JOIN cuenta_contable cc ON dld.id_cuenta_contable = cc.id_cuenta_contable
            WHERE dld.id_libro_diario = ?1
            """;

            if (filtroNombre != null && !filtroNombre.isBlank()) {
                sql += " AND UPPER(cc.nombre) LIKE UPPER(?2)";
            }

            sql += " GROUP BY cc.codigo, cc.nombre ORDER BY cc.codigo";

            var query = em.createNativeQuery(sql);
            query.setParameter(1, libroDiarioId);

            if (filtroNombre != null && !filtroNombre.isBlank()) {
                query.setParameter(2, "%" + filtroNombre + "%");
            }

            return query.getResultList();

        } catch (Exception ex) {
            Logger.getLogger(LibroDiarioDAO.class.getName()).log(Level.SEVERE,
                    "Error al buscar cuentas únicas para libro diario: " + libroDiarioId, ex);
            return List.of();
        }
    }

    /**
     * Metdod para encontrar movimientos por cuenta especifica utilizando JPQL
     */
    public List<Object[]> findMovimientosByCuenta(Long libroDiarioId, String codigoCuenta) {
        try {
            String jpql = """
                SELECT 
                    d.fecha, 
                    d.concepto, 
                    d.numeroPartida, 
                    d.debe, 
                    d.monto,
                    c.nombre
                FROM DetalleLibroDiario d 
                JOIN d.idCuentaContable c 
                WHERE d.libroDiario.id = :libroDiarioId 
                AND c.codigo = :codigoCuenta 
                ORDER BY d.fecha, d.numeroPartida
                """;

            List<Object[]> resultados = em.createQuery(jpql, Object[].class)
                    .setParameter("libroDiarioId", libroDiarioId)
                    .setParameter("codigoCuenta", codigoCuenta)
                    .getResultList();

            Logger.getLogger(LibroDiarioDAO.class.getName())
                    .log(Level.INFO, "Encontrados {0} movimientos para cuenta {1} en libro diario {2}",
                            new Object[]{resultados.size(), codigoCuenta, libroDiarioId});

            return resultados;

        } catch (Exception e) {
            Logger.getLogger(LibroDiarioDAO.class.getName())
                    .log(Level.SEVERE, "Error al buscar movimientos por cuenta: " + codigoCuenta + " en libro diario: " + libroDiarioId, e);
            return Collections.emptyList();
        }
    }

    /**
     * Metdodo de verificacion de existencia de datos en el libro diario
     */
    public List<Object[]> verificarDatosDetalleLibroDiario(Long libroDiarioId) {
        try {
            String jpql = """
                SELECT 
                    COUNT(d),
                    MIN(d.fecha),
                    MAX(d.fecha)
                FROM DetalleLibroDiario d 
                WHERE d.libroDiario.id = :libroDiarioId
                """;

            return em.createQuery(jpql, Object[].class)
                    .setParameter("libroDiarioId", libroDiarioId)
                    .getResultList();

        } catch (Exception e) {
            Logger.getLogger(LibroDiarioDAO.class.getName())
                    .log(Level.SEVERE, "Error al verificar datos del libro diario: " + libroDiarioId, e);
            return Collections.emptyList();
        }
    }

    /**
     * Metodo para veridicar datos detallasdos con informacion especifica
     */
    public List<Object[]> verificarDatosDetalladosLibroDiario(Long libroDiarioId) {
        try {
            String sql = """
            SELECT 
                dld.id_detalle_libro_diario,
                dld.fecha,
                cc.codigo,
                cc.nombre,
                dld.debe,
                dld.monto
            FROM detalle_libro_diario dld
            JOIN cuenta_contable cc ON dld.id_cuenta_contable = cc.id_cuenta_contable
            WHERE dld.id_libro_diario = ?1
            LIMIT 10
            """;

            var query = em.createNativeQuery(sql);
            query.setParameter(1, libroDiarioId);

            List<Object[]> resultados = query.getResultList();
            System.out.println("=== DATOS ENCONTRADOS EN DETALLE_LIBRO_DIARIO ===");
            System.out.println("Para libro diario ID: " + libroDiarioId);
            System.out.println("Registros encontrados: " + resultados.size());

            for (Object[] fila : resultados) {
                System.out.println("ID: " + fila[0] + ", Fecha: " + fila[1] +
                        ", Código: " + fila[2] + ", Nombre: " + fila[3] +
                        ", Débe: " + fila[4] + ", Monto: " + fila[5]);
            }

            return resultados;

        } catch (Exception ex) {
            Logger.getLogger(LibroDiarioDAO.class.getName()).log(Level.SEVERE,
                    "Error al verificar datos para libro diario: " + libroDiarioId, ex);
            return List.of();
        }
    }

    /**
     * Metodo para obtener una cuenta contable por codigo
     */
    public CuentaContable findCuentaByCodigo(String codigo) {
        try {
            TypedQuery<CuentaContable> query = em.createQuery(
                    "SELECT c FROM CuentaContable c WHERE c.codigo = :codigo",
                    CuentaContable.class
            );
            query.setParameter("codigo", codigo);
            return query.getSingleResult();
        } catch (Exception ex) {
            Logger.getLogger(LibroDiarioDAO.class.getName()).log(Level.SEVERE,
                    "Error al buscar cuenta por código: " + codigo, ex);
            return null;
        }
    }

    /**
     * Metodo para calcular saldo por tipo de cuenta
     */
    public Double calcularSaldoPorTipoCuenta(String codigoCuenta, String tipoCuenta) {
        try {
            String jpql = """
                SELECT 
                    CASE 
                        WHEN :tipoCuenta IN ('activo', 'gasto', 'costo') 
                        THEN (SUM(CASE WHEN d.debe = true THEN d.monto ELSE 0.0 END) - 
                              SUM(CASE WHEN d.debe = false THEN d.monto ELSE 0.0 END))
                        ELSE (SUM(CASE WHEN d.debe = false THEN d.monto ELSE 0.0 END) - 
                              SUM(CASE WHEN d.debe = true THEN d.monto ELSE 0.0 END))
                    END
                FROM DetalleLibroDiario d 
                JOIN d.idCuentaContable c 
                WHERE c.codigo = :codigoCuenta
                """;

            return (Double) em.createQuery(jpql)
                    .setParameter("codigoCuenta", codigoCuenta)
                    .setParameter("tipoCuenta", tipoCuenta)
                    .getSingleResult();

        } catch (Exception e) {
            Logger.getLogger(LibroDiarioDAO.class.getName())
                    .log(Level.SEVERE, "Error al calcular saldo para cuenta: " + codigoCuenta, e);
            return 0.0;
        }
    }
}