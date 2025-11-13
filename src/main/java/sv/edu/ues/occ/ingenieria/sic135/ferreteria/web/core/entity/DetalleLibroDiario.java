package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "detalle_libro_diario", schema = "public")
@NamedQueries({
        @NamedQuery(name="DetalleLibroDiario.findByLibroDiarioId",
                query="SELECT d FROM DetalleLibroDiario d WHERE d.libroDiario.id = :libroDiarioId")
})
public class DetalleLibroDiario {
    @Id
    @Column(name = "id_detalle_libro_diario", nullable = false)
    private UUID id;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "id_libro_diario")
    private LibroDiario libroDiario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_kardex_detalle")
    private KardexDetalle idKardexDetalle;

    @Column(name = "fecha")
    private OffsetDateTime fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cuenta_contable")
    private CuentaContable idCuentaContable;

    @NotNull
    @Column(name = "numero_partida", nullable = false)
    private Long numeroPartida;

    @Lob
    @Column(name = "concepto")
    private String concepto;

    @Column(name = "parcial")
    private BigDecimal parcial;

    @Column(name = "debe")
    private Boolean debe;

    @Column(name = "monto")
    private BigDecimal monto;

    @Column(name = "\"create at\"")
    private OffsetDateTime createAt;

    @Column(name = "\"update at\"")
    private OffsetDateTime updateAt;


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public KardexDetalle getIdKardexDetalle() {
        return idKardexDetalle;
    }

    public void setIdKardexDetalle(KardexDetalle idKardexDetalle) {
        this.idKardexDetalle = idKardexDetalle;
    }

    public OffsetDateTime getFecha() {
        return fecha;
    }

    public void setFecha(OffsetDateTime fecha) {
        this.fecha = fecha;
    }

    public CuentaContable getIdCuentaContable() {
        return idCuentaContable;
    }

    public void setIdCuentaContable(CuentaContable idCuentaContable) {
        this.idCuentaContable = idCuentaContable;
    }

    public Long getNumeroPartida() {
        return numeroPartida;
    }

    public void setNumeroPartida(Long numeroPartida) {
        this.numeroPartida = numeroPartida;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public BigDecimal getParcial() {
        return parcial;
    }

    public void setParcial(BigDecimal parcial) {
        this.parcial = parcial;
    }

    public Boolean getDebe() {
        return debe;
    }

    public void setDebe(Boolean debe) {
        this.debe = debe;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
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

    public LibroDiario getLibroDiario() {
        return libroDiario;
    }

    public void setLibroDiario(LibroDiario libroDiario) {
        this.libroDiario = libroDiario;
    }
}