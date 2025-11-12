package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "factura", schema = "public")
public class Factura {
    @Id
    @Column(name = "id_factura", nullable = false)
    private UUID id;

    @Lob
    @Column(name = "numero_factura")
    private String numeroFactura;

    @Column(name = "fecha_factura")
    private OffsetDateTime fechaFactura;

    @Column(name = "subtotal")
    private BigDecimal subtotal;

    @Column(name = "descuento")
    private BigDecimal descuento;

    @Column(name = "iva")
    private Boolean iva;

    @Column(name = "total")
    private BigDecimal total;

    @Lob
    @Column(name = "estado")
    private String estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_detalle_libro_diario")
    private DetalleLibroDiario idDetalleLibroDiario;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public OffsetDateTime getFechaFactura() {
        return fechaFactura;
    }

    public void setFechaFactura(OffsetDateTime fechaFactura) {
        this.fechaFactura = fechaFactura;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public Boolean getIva() {
        return iva;
    }

    public void setIva(Boolean iva) {
        this.iva = iva;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public DetalleLibroDiario getIdDetalleLibroDiario() {
        return idDetalleLibroDiario;
    }

    public void setIdDetalleLibroDiario(DetalleLibroDiario idDetalleLibroDiario) {
        this.idDetalleLibroDiario = idDetalleLibroDiario;
    }

}