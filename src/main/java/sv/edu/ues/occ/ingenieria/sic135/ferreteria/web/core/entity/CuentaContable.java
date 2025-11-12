package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "cuenta_contable", schema = "public")
public class CuentaContable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cuenta_contable", nullable = false)
    private Long id;

    @Size(max = 20)
    @Column(name = "codigo", length = 20)
    private String codigo;

    @Size(max = 255)
    @Column(name = "nombre")
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_cuenta")
    private TipoCuenta idTipoCuenta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sub_tipo_cuenta")
    private SubTipoCuenta idSubTipoCuenta;

    @Lob
    @Column(name = "descripcion")
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_padre")
    private CuentaContable cuentaPadre;

    @Column(name = "create_at")
    private OffsetDateTime createAt;

    @Column(name = "update_at")
    private OffsetDateTime updateAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario idUsuario;

    @OneToMany(mappedBy = "cuentaPadre")
    private Set<CuentaContable> cuentaContables = new LinkedHashSet<>();

    @OneToMany(mappedBy = "idCuentaContable")
    private Set<DetalleLibroDiario> detalleLibroDiarios = new LinkedHashSet<>();

    @OneToMany(mappedBy = "idCuentaContable")
    private Set<ManualCuenta> manualCuentas = new LinkedHashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public TipoCuenta getIdTipoCuenta() {
        return idTipoCuenta;
    }

    public void setIdTipoCuenta(TipoCuenta idTipoCuenta) {
        this.idTipoCuenta = idTipoCuenta;
    }

    public SubTipoCuenta getIdSubTipoCuenta() {
        return idSubTipoCuenta;
    }

    public void setIdSubTipoCuenta(SubTipoCuenta idSubTipoCuenta) {
        this.idSubTipoCuenta = idSubTipoCuenta;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public CuentaContable getCuentaPadre() {
        return cuentaPadre;
    }

    public void setCuentaPadre(CuentaContable cuentaPadre) {
        this.cuentaPadre = cuentaPadre;
    }

    public OffsetDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(OffsetDateTime createAt) {
        this.createAt = createAt;
    }

    public OffsetDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(OffsetDateTime updateAt) {
        this.updateAt = updateAt;
    }

    public Usuario getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Usuario idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Set<CuentaContable> getCuentaContables() {
        return cuentaContables;
    }

    public void setCuentaContables(Set<CuentaContable> cuentaContables) {
        this.cuentaContables = cuentaContables;
    }

    public Set<DetalleLibroDiario> getDetalleLibroDiarios() {
        return detalleLibroDiarios;
    }

    public void setDetalleLibroDiarios(Set<DetalleLibroDiario> detalleLibroDiarios) {
        this.detalleLibroDiarios = detalleLibroDiarios;
    }

    public Set<ManualCuenta> getManualCuentas() {
        return manualCuentas;
    }

    public void setManualCuentas(Set<ManualCuenta> manualCuentas) {
        this.manualCuentas = manualCuentas;
    }

}