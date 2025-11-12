package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "manual_cuentas", schema = "public")
public class ManualCuenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_manual_cuentas", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cuenta_contable")
    private CuentaContable idCuentaContable;

    @Lob
    @Column(name = "funcion_cuenta")
    private String funcionCuenta;

    @Lob
    @Column(name = "naturaleza_cuenta")
    private String naturalezaCuenta;

    @Lob
    @Column(name = "ejemplo_movimiento")
    private String ejemploMovimiento;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CuentaContable getIdCuentaContable() {
        return idCuentaContable;
    }

    public void setIdCuentaContable(CuentaContable idCuentaContable) {
        this.idCuentaContable = idCuentaContable;
    }

    public String getFuncionCuenta() {
        return funcionCuenta;
    }

    public void setFuncionCuenta(String funcionCuenta) {
        this.funcionCuenta = funcionCuenta;
    }

    public String getNaturalezaCuenta() {
        return naturalezaCuenta;
    }

    public void setNaturalezaCuenta(String naturalezaCuenta) {
        this.naturalezaCuenta = naturalezaCuenta;
    }

    public String getEjemploMovimiento() {
        return ejemploMovimiento;
    }

    public void setEjemploMovimiento(String ejemploMovimiento) {
        this.ejemploMovimiento = ejemploMovimiento;
    }

}