package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "unidad_medida", schema = "public")
@NamedQueries({
        @NamedQuery(name = "UnidadMedida.findByIdTipoUnidadMedida", query = "SELECT um FROM UnidadMedida um WHERE um.idTipoUnidadMedida.id = :idTipoUnidadMedida"),
        @NamedQuery(name = "UnidadMedida.countByIdTipoUnidadMedida", query = "SELECT COUNT(um) FROM UnidadMedida um WHERE um.idTipoUnidadMedida.id = :idTipoUnidadMedida")

})
public class UnidadMedida {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_unidad_medida", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_unidad_medida")
    private TipoUnidadMedida idTipoUnidadMedida;

    @Column(name = "equivalencia", precision = 8, scale = 2)
    private BigDecimal equivalencia;

    @Column(name = "expresion_regular", length = Integer.MAX_VALUE)
    private String expresionRegular;

    @Column(name = "activo")
    private Boolean activo;

    @Column(name = "comentarios", length = Integer.MAX_VALUE)
    private String comentarios;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TipoUnidadMedida getIdTipoUnidadMedida() {
        return idTipoUnidadMedida;
    }

    public void setIdTipoUnidadMedida(TipoUnidadMedida idTipoUnidadMedida) {
        this.idTipoUnidadMedida = idTipoUnidadMedida;
    }

    public BigDecimal getEquivalencia() {
        return equivalencia;
    }

    public void setEquivalencia(BigDecimal equivalencia) {
        this.equivalencia = equivalencia;
    }

    public String getExpresionRegular() {
        return expresionRegular;
    }

    public void setExpresionRegular(String expresionRegular) {
        this.expresionRegular = expresionRegular;
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

}