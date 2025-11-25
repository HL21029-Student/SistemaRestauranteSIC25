package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.math.BigDecimal;

import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.dto.EstadoResultadoDTO;

@Stateless
@LocalBean
public class EstadoResultadoDAO {

    @PersistenceContext(unitName = "FerreteriaPU")
    private EntityManager em;

    /**
     * Genera el Estado de Resultados a partir de un Libro Mayor seleccionado.
     */
    public EstadoResultadoDTO generarPorLibroMayor(Long idLibroMayor, BigDecimal tasaISR) {

        EstadoResultadoDTO dto = new EstadoResultadoDTO();

        // --------------------------------------------------------------------
        // BLOQUE 1: INGRESOS
        // Código 5%
        // --------------------------------------------------------------------
        BigDecimal ingresos = sumPorCodigoAndLibroMayor("5%", idLibroMayor);
        dto.setIngresos(ingresos);

        // --------------------------------------------------------------------
        // BLOQUE 2: COSTO DE VENTAS
        // Código 61%
        // --------------------------------------------------------------------
        BigDecimal costoVentas = sumPorCodigoAndLibroMayor("61%", idLibroMayor);
        dto.setCostoVentas(costoVentas);

        // UTILIDAD BRUTA
        dto.setUtilidadBruta(ingresos.subtract(costoVentas));

        // --------------------------------------------------------------------
        // BLOQUE 3: GASTOS DE VENTA
        // Código 4101%
        // --------------------------------------------------------------------
        BigDecimal gastoVenta = sumPorCodigoAndLibroMayor("4101%", idLibroMayor);
        dto.setGastoVenta(gastoVenta);

        // --------------------------------------------------------------------
        // BLOQUE 4: GASTOS ADMINISTRATIVOS
        // Código 4102%
        // --------------------------------------------------------------------
        BigDecimal gastoAdmin = sumPorCodigoAndLibroMayor("4102%", idLibroMayor);
        dto.setGastoAdministracion(gastoAdmin);

        // UTILIDAD OPERATIVA
        dto.setUtilidadOperativa(
                dto.getUtilidadBruta()
                        .subtract(gastoVenta)
                        .subtract(gastoAdmin)
        );

        // --------------------------------------------------------------------
        // BLOQUE 5: GASTOS FINANCIEROS
        // Código 4103%
        // --------------------------------------------------------------------
        BigDecimal gastoFin = sumPorCodigoAndLibroMayor("4103%", idLibroMayor);
        dto.setGastoFinanciero(gastoFin);

        // --------------------------------------------------------------------
        // BLOQUE 6: OTROS GASTOS NO OPERATIVOS
        // Código 4201%
        // --------------------------------------------------------------------
        BigDecimal otros = sumPorCodigoAndLibroMayor("4201%", idLibroMayor);
        dto.setOtrosGastos(otros);

        // UTILIDAD ANTES DE ISR
        dto.setUtilidadAntesISR(
                dto.getUtilidadOperativa()
                        .subtract(gastoFin)
                        .subtract(otros)
        );

        // --------------------------------------------------------------------
        // IMPUESTO SOBRE LA RENTA
        // Si no mandan tasaISR → usamos 25%
        // --------------------------------------------------------------------
        BigDecimal tasa = (tasaISR == null) ? new BigDecimal("0.25") : tasaISR;

        BigDecimal impuesto = dto.getUtilidadAntesISR()
                .max(BigDecimal.ZERO)     // no calcular ISR si es pérdida
                .multiply(tasa);

        dto.setImpuestoRenta(impuesto);

        // UTILIDAD NETA
        dto.setUtilidadNeta(dto.getUtilidadAntesISR().subtract(impuesto));

        return dto;
    }

    /**
     * Suma los saldos del Libro Mayor filtrando por código contable.
     * Coincidencia por nombre: CuentaContable.nombre = DetalleLibroMayor.nombreCuenta
     */
    private BigDecimal sumPorCodigoAndLibroMayor(String codigoPattern, Long idLibroMayor) {

        BigDecimal result = (BigDecimal) em.createQuery(
                        "SELECT COALESCE(SUM(d.saldo), 0) " +
                                "FROM DetalleLibroMayor d, CuentaContable c " +
                                "WHERE c.nombre = d.nombreCuenta " +
                                "AND c.codigo LIKE :codigo " +
                                "AND d.idLibroMayor.id = :idLibroMayor"
                )
                .setParameter("codigo", codigoPattern)
                .setParameter("idLibroMayor", idLibroMayor)
                .getSingleResult();

        return result == null ? BigDecimal.ZERO : result;
    }
}
