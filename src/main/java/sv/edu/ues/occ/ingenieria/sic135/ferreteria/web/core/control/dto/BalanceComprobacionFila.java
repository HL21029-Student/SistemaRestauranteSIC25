package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.dto;

import java.math.BigDecimal;

public class BalanceComprobacionFila {

    private String cuenta;
    private BigDecimal debe;
    private BigDecimal haber;

    public BalanceComprobacionFila(String cuenta, BigDecimal debe, BigDecimal haber) {
        this.cuenta = cuenta;
        this.debe = debe;
        this.haber = haber;
    }

    public String getCuenta() { return cuenta; }
    public BigDecimal getDebe() { return debe; }
    public BigDecimal getHaber() { return haber; }

    public void setCuenta(String cuenta) { this.cuenta = cuenta; }
    public void setDebe(BigDecimal debe) { this.debe = debe; }
    public void setHaber(BigDecimal haber) { this.haber = haber; }
}
