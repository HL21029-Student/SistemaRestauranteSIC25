package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "producto_tipo_producto", schema = "public")
@NamedQueries({
        @NamedQuery(name = "ProductoTipoProducto.findByIdProducto", query = "SELECT p FROM ProductoTipoProducto p WHERE p.idProducto.id = :idProducto"),
        @NamedQuery(name = "ProductoTipoProducto.countByIdProducto", query = "SELECT COUNT(p) FROM ProductoTipoProducto p WHERE p.idProducto.id = :idProducto")
})
public class ProductoTipoProducto {
    @Id
    @Column(name = "id_producto_tipo_producto", nullable = false)
    private UUID id;


    @JsonbTransient
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto")
    private Producto idProducto;

    @JsonbTransient
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_tipo_producto")
    @NotNull
    private TipoProducto idTipoProducto;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonbTransient
    @Column(name = "fecha_creacion")
    private Date fechaCreacion;

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }


    @Column(name = "activo")
    private Boolean activo;

    @Column(name = "observaciones", length = Integer.MAX_VALUE)
    private String observaciones;

    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public Producto getIdProducto() {
        return idProducto;
    }
    public void setIdProducto(Producto idProducto) {
        this.idProducto = idProducto;
    }
    public Boolean getActivo() {
        return activo;
    }
    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
    public String getObservaciones() {
        return observaciones;
    }
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    public java.util.stream.Stream<ProductoTipoProducto> stream() {
        return java.util.stream.Stream.of(this);
    }
    public @NotNull TipoProducto getIdTipoProducto() {
        return idTipoProducto;
    }
    public void setIdTipoProducto(@NotNull TipoProducto idTipoProducto) {
        this.idTipoProducto = idTipoProducto;
    }
}