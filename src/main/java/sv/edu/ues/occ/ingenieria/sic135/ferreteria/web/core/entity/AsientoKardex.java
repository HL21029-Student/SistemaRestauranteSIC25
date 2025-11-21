package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "asiento_kardex", schema = "public")
public class AsientoKardex {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_asiento_kardex", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_detalle_libro_diario", nullable = false)
    private DetalleLibroDiario idDetalleLibroDiario;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_kardex_detalle", nullable = false)
    private KardexDetalle idKardexDetalle;

    @Size(max = 50)
    @Column(name = "tipo_relacion", length = 50)
    private String tipoRelacion;

    @Lob
    @Column(name = "observaciones")
    private String observaciones;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public DetalleLibroDiario getIdDetalleLibroDiario() {
        return idDetalleLibroDiario;
    }

    public void setIdDetalleLibroDiario(DetalleLibroDiario idDetalleLibroDiario) {
        this.idDetalleLibroDiario = idDetalleLibroDiario;
    }

    public KardexDetalle getIdKardexDetalle() {
        return idKardexDetalle;
    }

    public void setIdKardexDetalle(KardexDetalle idKardexDetalle) {
        this.idKardexDetalle = idKardexDetalle;
    }

    public String getTipoRelacion() {
        return tipoRelacion;
    }

    public void setTipoRelacion(String tipoRelacion) {
        this.tipoRelacion = tipoRelacion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

}