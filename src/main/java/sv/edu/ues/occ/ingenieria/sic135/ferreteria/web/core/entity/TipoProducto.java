package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

@Entity
@Table(name = "tipo_producto", schema = "public")
@NamedQueries({
        @NamedQuery(name="TipoProducto.findByNombreLike", query = "SELECT t FROM TipoProducto t WHERE upper(t.nombre) like :nombre"),
        @NamedQuery(name="TipoProducto.findTiposPadre", query="SELECT t FROM TipoProducto t WHERE t.idTipoProductoPadre IS NULL ORDER BY t.nombre"),
        @NamedQuery(name="TipoProducto.findHijosByPadre", query = "SELECT t FROM TipoProducto t WHERE t.idTipoProductoPadre.id = :idPadre ORDER BY t.nombre")
})
public class TipoProducto implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_producto", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_producto_padre")
    private TipoProducto idTipoProductoPadre;

    @Size(max = 155)
    @Column(name = "nombre", length = 155)
    private String nombre;

    @Column(name = "activo")
    private Boolean activo;

    @Column(name = "comentarios", length = Integer.MAX_VALUE)
    private String comentarios;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TipoProducto)) return false;
        TipoProducto that = (TipoProducto) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return (id != null) ? id.hashCode() : 0;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public TipoProducto getIdTipoProductoPadre() {
        return idTipoProductoPadre;
    }
    public void setIdTipoProductoPadre(TipoProducto idTipoProductoPadre) {
        this.idTipoProductoPadre = idTipoProductoPadre;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public Boolean getActivo() {
        return activo;
    }
    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
    public String getComentarios() {
        return comentarios;
    }
    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }
    public boolean isEmpty() {
        return this.id == null || this.id == 0;
    }
    public Long getIdTipoProducto() {
        return id;
    }
    public java.util.stream.Stream<TipoProducto> stream() {
        return java.util.stream.Stream.of(this);
    }
}