package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.VentaDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.VentaDetalleDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.Venta;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.VentaDetalle;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named("reporteVentasDiariasFrm")
@ViewScoped
public class ReporteVentasDiariasFrm implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(ReporteVentasDiariasFrm.class.getName());

    @Inject
    private VentaDAO ventaDAO;

    @Inject
    private VentaDetalleDAO ventaDetalleDAO;

    @PersistenceContext(unitName = "FerreteriaPU")
    private EntityManager em;

    private LocalDate fecha;
    private List<Venta> ventas = new ArrayList<>();

    @PostConstruct
    public void init() {
        fecha = LocalDate.now();
        cargarVentas();
    }

    public void cargarVentas() {
        if (fecha == null) {
            LOGGER.log(Level.WARNING, "Fecha es null, usando fecha actual");
            fecha = LocalDate.now();
        }

        try {
            // Convertir LocalDate a Date para inicio del día (00:00:00)
            Date fechaInicio = Date.from(fecha.atStartOfDay(ZoneId.systemDefault()).toInstant());

            // Fecha fin es el inicio del día siguiente (para incluir todo el día seleccionado)
            Date fechaFin = Date.from(fecha.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

            LOGGER.log(Level.INFO, "Buscando ventas entre {0} y {1}", new Object[]{fechaInicio, fechaFin});

            // Query para filtrar ventas por fecha
            TypedQuery<Venta> query = em.createQuery(
                "SELECT v FROM Venta v WHERE v.fecha >= :fechaInicio AND v.fecha < :fechaFin ORDER BY v.fecha DESC",
                Venta.class
            );
            query.setParameter("fechaInicio", fechaInicio);
            query.setParameter("fechaFin", fechaFin);

            ventas = query.getResultList();

            LOGGER.log(Level.INFO, "Se encontraron {0} ventas para la fecha {1}",
                new Object[]{ventas.size(), fecha});

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al cargar ventas por fecha", e);
            ventas = new ArrayList<>();
        }
    }

    /**
     * Calcula el total de una venta sumando todos sus detalles (precio x cantidad)
     */
    public BigDecimal calcularTotal(Venta venta) {
        if (venta == null || venta.getId() == null) {
            return BigDecimal.ZERO;
        }

        try {
            // Obtener todos los detalles de la venta (0, Integer.MAX_VALUE para traer todos)
            List<VentaDetalle> detalles = ventaDetalleDAO.findByIdVenta(venta.getId(), 0, Integer.MAX_VALUE);

            // Usar el método ya existente en VentaDetalleDAO
            return ventaDetalleDAO.calcularMontoTotal(detalles);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error calculando total de venta: " + venta.getId(), e);
            return BigDecimal.ZERO;
        }
    }

    /**
     * Obtiene los detalles (productos) de una venta específica
     */
    public List<VentaDetalle> obtenerDetalles(Venta venta) {
        if (venta == null || venta.getId() == null) {
            return new ArrayList<>();
        }

        try {
            return ventaDetalleDAO.findByIdVenta(venta.getId(), 0, Integer.MAX_VALUE);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error obteniendo detalles de venta: " + venta.getId(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Obtiene el método de pago de una venta
     * Por ahora usa las observaciones, pero puede mejorarse agregando un campo específico
     */
    public String obtenerMetodoPago(Venta venta) {
        if (venta == null) {
            return "N/A";
        }

        String observaciones = venta.getObservaciones();
        if (observaciones != null && !observaciones.isEmpty()) {
            // Buscar palabras clave en observaciones
            String obsLower = observaciones.toLowerCase();
            if (obsLower.contains("efectivo") || obsLower.contains("cash")) {
                return "EFECTIVO";
            } else if (obsLower.contains("tarjeta") || obsLower.contains("card")) {
                return "TARJETA";
            } else if (obsLower.contains("transferencia") || obsLower.contains("transfer")) {
                return "TRANSFERENCIA";
            }
        }

        // Por defecto, asumir efectivo si no se especifica
        return "EFECTIVO";
    }

    /**
     * Calcula el total de todas las ventas del día seleccionado
     */
    public BigDecimal calcularTotalDia() {
        if (ventas == null || ventas.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return ventas.stream()
            .map(this::calcularTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Cuenta cuántas ventas fueron en efectivo
     */
    public long contarVentasEfectivo() {
        if (ventas == null || ventas.isEmpty()) {
            return 0;
        }

        return ventas.stream()
            .filter(v -> "EFECTIVO".equals(obtenerMetodoPago(v)))
            .count();
    }

    /**
     * Calcula el monto total de ventas en efectivo
     */
    public BigDecimal calcularTotalEfectivo() {
        if (ventas == null || ventas.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return ventas.stream()
            .filter(v -> "EFECTIVO".equals(obtenerMetodoPago(v)))
            .map(this::calcularTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Obtiene el total de una venta como texto formateado para exportación
     */
    public String obtenerTotalFormateado(Venta venta) {
        BigDecimal total = calcularTotal(venta);
        return String.format("$ %.2f", total);
    }

    /**
     * Obtiene el total del día formateado como texto para exportación
     */
    public String obtenerTotalDiaFormateado() {
        BigDecimal total = calcularTotalDia();
        return String.format("$ %.2f", total);
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public List<Venta> getVentas() {
        return ventas;
    }

    /**
     * Verifica si hay ventas disponibles para mostrar
     */
    public boolean isHayVentas() {
        boolean resultado = ventas != null && !ventas.isEmpty();
        LOGGER.log(Level.INFO, "isHayVentas: {0}, cantidad de ventas: {1}",
            new Object[]{resultado, ventas != null ? ventas.size() : 0});
        return resultado;
    }
}

