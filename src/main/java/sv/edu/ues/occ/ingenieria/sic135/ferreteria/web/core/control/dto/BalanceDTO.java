package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.dto;

public class BalanceDTO {

    private Long idCuenta;
    private String codigo;
    private String nombre;
    private String tipoCuenta;
    private Double saldo;

    public BalanceDTO(Long idCuenta, String codigo, String nombre, String tipoCuenta, Double saldo) {
        this.idCuenta = idCuenta;
        this.codigo = codigo;
        this.nombre = nombre;
        this.tipoCuenta = tipoCuenta;
        this.saldo = saldo;
    }

    public boolean isDeudor() {
        return tipoCuenta.equalsIgnoreCase("ACTIVO")
                || tipoCuenta.equalsIgnoreCase("GASTO")
                || tipoCuenta.equalsIgnoreCase("COSTO");
    }

    public boolean isAcreedor() {
        return tipoCuenta.equalsIgnoreCase("PASIVO")
                || tipoCuenta.equalsIgnoreCase("CAPITAL")
                || tipoCuenta.equalsIgnoreCase("INGRESO");
    }

    public Long getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(Long idCuenta) {
        this.idCuenta = idCuenta;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipoCuenta() {
        return tipoCuenta;
    }

    public void setTipoCuenta(String tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }
}