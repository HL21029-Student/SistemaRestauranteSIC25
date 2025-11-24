package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.CuentaContableDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.DetalleLibroDiarioDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.LibroDiarioDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.VentaDetalleDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servicio para la creación automática de asientos contables
 */
@ApplicationScoped
public class AsientoContableService implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(AsientoContableService.class.getName());

    @PersistenceContext(unitName = "FerreteriaPU")
    private EntityManager em;

    @Inject
    private LibroDiarioDAO libroDiarioDAO;

    @Inject
    private DetalleLibroDiarioDAO detalleLibroDiarioDAO;

    @Inject
    private CuentaContableDAO cuentaContableDAO;

    @Inject
    private VentaDetalleDAO ventaDetalleDAO;

    /**
     * Crea ÚNICAMENTE el ENCABEZADO del libro diario cuando se registra una venta/factura.
     * NO crea líneas de detalle.
     * NO consulta cuentas contables.
     * NO calcula número de partida.
     *
     * @param venta La venta para la cual crear el encabezado
     * @param factura La factura asociada (opcional, puede ser null)
     */
    @Transactional
    public void crearEncabezadoLibroDiario(Venta venta, Factura factura) {
        LOGGER.log(Level.INFO, "=== CREANDO SOLO ENCABEZADO DE LIBRO DIARIO ===");

        if (factura == null || factura.getNumeroFactura() == null) {
            LOGGER.log(Level.WARNING, "No se puede crear encabezado: factura es null o no tiene número");
            return;
        }

        try {
            // Crear descripción para el encabezado
            String descripcion = "Asiento generado automáticamente para factura #" + factura.getNumeroFactura();
            if (venta != null && venta.getIdCliente() != null && venta.getIdCliente().getNombre() != null) {
                descripcion += " - Cliente: " + venta.getIdCliente().getNombre();
            }

            LOGGER.log(Level.INFO, "Descripción del encabezado: {0}", descripcion);

            // Crear SOLO el encabezado del libro diario
            LibroDiario encabezado = new LibroDiario();
            encabezado.setNombre(descripcion);
            encabezado.setComentario("Pendiente de registrar detalles contables");

            // Guardar el encabezado
            libroDiarioDAO.create(encabezado);

            LOGGER.log(Level.INFO, "✓ Encabezado de libro diario creado exitosamente con ID: {0}", encabezado.getId());
            LOGGER.log(Level.INFO, "=== NO SE CREARON DETALLES - SOLO ENCABEZADO ===");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al crear encabezado de libro diario para factura: " + factura.getNumeroFactura(), e);
            // No lanzamos la excepción para que no afecte la transacción principal
        }
    }

    // MÉTODO DESACTIVADO - Ya no se calcula total de venta para detalles
    /*
    private BigDecimal calcularTotalVenta(Venta venta) {
        try {
            List<VentaDetalle> detalles = ventaDetalleDAO.findByIdVenta(venta.getId(), 0, Integer.MAX_VALUE);
            return ventaDetalleDAO.calcularMontoTotal(detalles);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error al calcular total de venta: " + venta.getId(), e);
            return BigDecimal.ZERO;
        }
    }
    */

    // MÉTODO DESACTIVADO - Ya no se busca libro diario específico para ventas
    /*
    private LibroDiario obtenerOCrearLibroDiarioVentas() {
        try {
            LOGGER.log(Level.INFO, "Buscando libro diario de VENTAS...");

            TypedQuery<LibroDiario> query = em.createQuery(
                "SELECT l FROM LibroDiario l WHERE UPPER(l.nombre) = 'VENTAS' OR UPPER(l.nombre) LIKE '%VENTA%'",
                LibroDiario.class
            );
            query.setMaxResults(1);

            List<LibroDiario> resultados = query.getResultList();
            if (!resultados.isEmpty()) {
                LibroDiario libro = resultados.get(0);
                LOGGER.log(Level.INFO, "Libro diario encontrado: ID={0}, Nombre={1}",
                    new Object[]{libro.getId(), libro.getNombre()});
                return libro;
            }

            LOGGER.log(Level.INFO, "No existe libro diario de VENTAS, creando uno nuevo...");
            LibroDiario nuevoLibro = new LibroDiario();
            nuevoLibro.setNombre("VENTAS");
            nuevoLibro.setComentario("Libro diario para registro automático de ventas");
            libroDiarioDAO.create(nuevoLibro);

            LOGGER.log(Level.INFO, "✓ Nuevo libro diario de VENTAS creado con ID: {0}", nuevoLibro.getId());
            return nuevoLibro;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al obtener o crear libro diario de ventas", e);
            return null;
        }
    }
    */

    // MÉTODO DESACTIVADO - Ya no se consulta MAX(numero_partida)
    /*
    private Long obtenerSiguienteNumeroPartida(LibroDiario libroDiario) {
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT MAX(d.numeroPartida) FROM DetalleLibroDiario d WHERE d.libroDiario.id = :libroDiarioId",
                Long.class
            );
            query.setParameter("libroDiarioId", libroDiario.getId());

            Long maxPartida = query.getSingleResult();
            return (maxPartida != null) ? maxPartida + 1 : 1L;

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error al obtener siguiente número de partida, usando 1", e);
            return 1L;
        }
    }
    */

    // MÉTODO DESACTIVADO - Ya no se buscan cuentas contables
    /*
    private CuentaContable obtenerCuentaCaja() {
        try {
            LOGGER.log(Level.INFO, "Buscando cuenta de Caja...");

            // Buscar cuenta de Caja (código 1101 o similar)
            TypedQuery<CuentaContable> query = em.createQuery(
                "SELECT c FROM CuentaContable c WHERE c.codigo LIKE '1101%' OR c.codigo LIKE '11%' OR UPPER(c.nombre) LIKE '%CAJA%' ORDER BY c.codigo",
                CuentaContable.class
            );
            query.setMaxResults(1);

            List<CuentaContable> resultados = query.getResultList();
            if (!resultados.isEmpty()) {
                CuentaContable cuenta = resultados.get(0);
                LOGGER.log(Level.INFO, "Cuenta Caja encontrada: ID={0}, Código={1}, Nombre={2}",
                    new Object[]{cuenta.getId(), cuenta.getCodigo(), cuenta.getNombre()});
                return cuenta;
            }

            LOGGER.log(Level.WARNING, "No se encontró cuenta de Caja con código 11XX o nombre CAJA");

            // Intentar buscar cualquier cuenta de activo
            LOGGER.log(Level.INFO, "Intentando buscar cualquier cuenta que comience con '1'...");
            TypedQuery<CuentaContable> queryActivo = em.createQuery(
                "SELECT c FROM CuentaContable c WHERE c.codigo LIKE '1%' ORDER BY c.codigo",
                CuentaContable.class
            );
            queryActivo.setMaxResults(1);
            List<CuentaContable> resultadosActivo = queryActivo.getResultList();
            if (!resultadosActivo.isEmpty()) {
                CuentaContable cuenta = resultadosActivo.get(0);
                LOGGER.log(Level.INFO, "Cuenta alternativa encontrada: ID={0}, Código={1}, Nombre={2}",
                    new Object[]{cuenta.getId(), cuenta.getCodigo(), cuenta.getNombre()});
                return cuenta;
            }

            LOGGER.log(Level.WARNING, "No se encontró ninguna cuenta de Caja");
            return null;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al buscar cuenta de Caja", e);
            return null;
        }
    }

    // MÉTODO DESACTIVADO - Ya no se buscan cuentas de ingresos
    /*
    private CuentaContable obtenerCuentaIngresos() {
        try {
            LOGGER.log(Level.INFO, "Buscando cuenta de Ingresos...");

            // Buscar cuenta de Ingresos por Ventas (código 4101 o similar)
            TypedQuery<CuentaContable> query = em.createQuery(
                "SELECT c FROM CuentaContable c WHERE c.codigo LIKE '41%' OR c.codigo LIKE '4%' OR UPPER(c.nombre) LIKE '%INGRESO%' OR UPPER(c.nombre) LIKE '%VENTA%' ORDER BY c.codigo",
                CuentaContable.class
            );
            query.setMaxResults(1);

            List<CuentaContable> resultados = query.getResultList();
            if (!resultados.isEmpty()) {
                CuentaContable cuenta = resultados.get(0);
                LOGGER.log(Level.INFO, "Cuenta Ingresos encontrada: ID={0}, Código={1}, Nombre={2}",
                    new Object[]{cuenta.getId(), cuenta.getCodigo(), cuenta.getNombre()});
                return cuenta;
            }

            LOGGER.log(Level.WARNING, "No se encontró cuenta de Ingresos con código 4XX o nombre INGRESO/VENTA");

            // Intentar buscar la primera cuenta disponible
            LOGGER.log(Level.INFO, "Intentando buscar cualquier cuenta disponible...");
            TypedQuery<CuentaContable> queryCualquiera = em.createQuery(
                "SELECT c FROM CuentaContable c ORDER BY c.codigo",
                CuentaContable.class
            );
            queryCualquiera.setMaxResults(2); // Traer 2 para usar la segunda si la primera es la de caja
            List<CuentaContable> todasCuentas = queryCualquiera.getResultList();
            if (todasCuentas.size() >= 2) {
                CuentaContable cuenta = todasCuentas.get(1); // Usar la segunda cuenta
                LOGGER.log(Level.INFO, "Cuenta alternativa encontrada: ID={0}, Código={1}, Nombre={2}",
                    new Object[]{cuenta.getId(), cuenta.getCodigo(), cuenta.getNombre()});
                return cuenta;
            }

            LOGGER.log(Level.WARNING, "No se encontró ninguna cuenta de Ingresos");
            return null;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al buscar cuenta de Ingresos", e);
            return null;
        }
    }
    */

    // MÉTODO DESACTIVADO - Ya no se crea concepto para detalles
    /*
    private String crearConceptoVenta(Venta venta, Factura factura) {
        StringBuilder concepto = new StringBuilder("Venta ");

        if (factura != null && factura.getNumeroFactura() != null) {
            concepto.append("Factura #").append(factura.getNumeroFactura());
        } else {
            concepto.append("ID: ").append(venta.getId().toString().substring(0, 8));
        }

        if (venta.getIdCliente() != null && venta.getIdCliente().getNombre() != null) {
            concepto.append(" - Cliente: ").append(venta.getIdCliente().getNombre());
        }

        return concepto.toString();
    }
    */
}

