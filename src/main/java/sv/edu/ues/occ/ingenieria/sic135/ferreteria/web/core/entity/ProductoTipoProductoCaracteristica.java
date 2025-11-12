package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "producto_tipo_producto_caracteristica", schema = "public")
@NamedQueries({
     @NamedQuery(name = "ProductoTipoProductoCaracteristica.findByProductoTipoProductoId",
                query = "SELECT p FROM ProductoTipoProductoCaracteristica p WHERE p.idProductoTipoProducto.id = :idProductoTipoProducto"),
    })
public class ProductoTipoProductoCaracteristica {
    @Id
    @Column(name = "id_producto_tipo_producto_caracteristica", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto_tipo_producto")
    private ProductoTipoProducto idProductoTipoProducto;

    @JsonbTransient
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_producto_caracteristica")
    private TipoProductoCaracteristica idTipoProductoCaracteristica;

    @Column(name = "valor", length = Integer.MAX_VALUE)
    private String valor;

    @Column(name = "observaciones", length = Integer.MAX_VALUE)
    private String observaciones;

    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public ProductoTipoProducto getIdProductoTipoProducto() {
        return idProductoTipoProducto;
    }
    public void setIdProductoTipoProducto(ProductoTipoProducto idProductoTipoProducto) {
        this.idProductoTipoProducto = idProductoTipoProducto;
    }
    public TipoProductoCaracteristica getIdTipoProductoCaracteristica() {
        return idTipoProductoCaracteristica;
    }
    public void setIdTipoProductoCaracteristica(TipoProductoCaracteristica idTipoProductoCaracteristica) {
        this.idTipoProductoCaracteristica = idTipoProductoCaracteristica;
    }
    public String getValor() {
        return valor;
    }
    public void setValor(String valor) {
        this.valor = valor;
    }
    public String getObservaciones() {
        return observaciones;
    }
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}