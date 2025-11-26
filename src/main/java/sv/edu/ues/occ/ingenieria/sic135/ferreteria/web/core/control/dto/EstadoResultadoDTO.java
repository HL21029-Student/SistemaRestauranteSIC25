package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.dto;

import java.math.BigDecimal;

public class EstadoResultadoDTO {

    private BigDecimal ingresos = BigDecimal.ZERO;
    private BigDecimal costoVentas = BigDecimal.ZERO;
    private BigDecimal utilidadBruta = BigDecimal.ZERO;

    private BigDecimal gastoVenta = BigDecimal.ZERO;
    private BigDecimal gastoAdministracion = BigDecimal.ZERO;
    private BigDecimal utilidadOperativa = BigDecimal.ZERO;

    private BigDecimal gastoFinanciero = BigDecimal.ZERO;
    private BigDecimal otrosGastos = BigDecimal.ZERO;

    private BigDecimal utilidadAntesISR = BigDecimal.ZERO;
    private BigDecimal impuestoRenta = BigDecimal.ZERO;
    private BigDecimal utilidadNeta = BigDecimal.ZERO;

    public BigDecimal getIngresos() { return ingresos; }
    public void setIngresos(BigDecimal ingresos) { this.ingresos = ingresos; }

    public BigDecimal getCostoVentas() { return costoVentas; }
    public void setCostoVentas(BigDecimal costoVentas) { this.costoVentas = costoVentas; }

    public BigDecimal getUtilidadBruta() { return utilidadBruta; }
    public void setUtilidadBruta(BigDecimal utilidadBruta) { this.utilidadBruta = utilidadBruta; }

    public BigDecimal getGastoVenta() { return gastoVenta; }
    public void setGastoVenta(BigDecimal gastoVenta) { this.gastoVenta = gastoVenta; }

    public BigDecimal getGastoAdministracion() { return gastoAdministracion; }
    public void setGastoAdministracion(BigDecimal gastoAdministracion) { this.gastoAdministracion = gastoAdministracion; }

    public BigDecimal getUtilidadOperativa() { return utilidadOperativa; }
    public void setUtilidadOperativa(BigDecimal utilidadOperativa) { this.utilidadOperativa = utilidadOperativa; }

    public BigDecimal getGastoFinanciero() { return gastoFinanciero; }
    public void setGastoFinanciero(BigDecimal gastoFinanciero) { this.gastoFinanciero = gastoFinanciero; }

    public BigDecimal getOtrosGastos() { return otrosGastos; }
    public void setOtrosGastos(BigDecimal otrosGastos) { this.otrosGastos = otrosGastos; }

    public BigDecimal getUtilidadAntesISR() { return utilidadAntesISR; }
    public void setUtilidadAntesISR(BigDecimal utilidadAntesISR) { this.utilidadAntesISR = utilidadAntesISR; }

    public BigDecimal getImpuestoRenta() { return impuestoRenta; }
    public void setImpuestoRenta(BigDecimal impuestoRenta) { this.impuestoRenta = impuestoRenta; }

    public BigDecimal getUtilidadNeta() { return utilidadNeta; }
    public void setUtilidadNeta(BigDecimal utilidadNeta) { this.utilidadNeta = utilidadNeta; }

    // ---- Métodos auxiliares ----
    private BigDecimal abs(BigDecimal val) {
        return val == null ? BigDecimal.ZERO : val.abs();
    }

    private boolean isNeg(BigDecimal val) {
        return val != null && val.compareTo(BigDecimal.ZERO) < 0;
    }

    // ---- Ingresos ----
    public BigDecimal getIngresosAbs() { return abs(ingresos); }
    public boolean isIngresosNeg() { return isNeg(ingresos); }

    // ---- Costo Ventas ----
    public BigDecimal getCostoVentasAbs() { return abs(costoVentas); }
    public boolean isCostoVentasNeg() { return isNeg(costoVentas); }

    // ---- Utilidad Bruta ----
    public BigDecimal getUtilidadBrutaAbs() { return abs(utilidadBruta); }
    public boolean isUtilidadBrutaNeg() { return isNeg(utilidadBruta); }

    // ---- Gastos de Venta ----
    public BigDecimal getGastoVentaAbs() { return abs(gastoVenta); }
    public boolean isGastoVentaNeg() { return isNeg(gastoVenta); }

    // ---- Gastos de Administración ----
    public BigDecimal getGastoAdministracionAbs() { return abs(gastoAdministracion); }
    public boolean isGastoAdministracionNeg() { return isNeg(gastoAdministracion); }

    // ---- Utilidad Operativa ----
    public BigDecimal getUtilidadOperativaAbs() { return abs(utilidadOperativa); }
    public boolean isUtilidadOperativaNeg() { return isNeg(utilidadOperativa); }

    // ---- Gasto Financiero ----
    public BigDecimal getGastoFinancieroAbs() { return abs(gastoFinanciero); }
    public boolean isGastoFinancieroNeg() { return isNeg(gastoFinanciero); }

    // ---- Otros Gastos ----
    public BigDecimal getOtrosGastosAbs() { return abs(otrosGastos); }
    public boolean isOtrosGastosNeg() { return isNeg(otrosGastos); }

    // ---- Utilidad Antes ISR ----
    public BigDecimal getUtilidadAntesISRAbs() { return abs(utilidadAntesISR); }
    public boolean isUtilidadAntesISRNeg() { return isNeg(utilidadAntesISR); }

    // ---- Impuesto Renta ----
    public BigDecimal getImpuestoRentaAbs() { return abs(impuestoRenta); }
    public boolean isImpuestoRentaNeg() { return isNeg(impuestoRenta); }

    // ---- Utilidad Neta ----
    public BigDecimal getUtilidadNetaAbs() { return abs(utilidadNeta); }
    public boolean isUtilidadNetaNeg() { return isNeg(utilidadNeta); }
}
