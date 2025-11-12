package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "caracteristica", schema = "public")
@NamedQueries({
        @NamedQuery(name = "Caracteristica.findByIdCaracteristica", query = "SELECT c FROM Caracteristica c WHERE c.id = :id"),
        @NamedQuery(name="Caracteristica.findByNombreLike", query = "SELECT c FROM Caracteristica c WHERE upper(c.nombre) like :nombre")
})
public class Caracteristica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_caracteristica", nullable = false)
    private Integer id;

    @Column(name = "nombre", length = Integer.MAX_VALUE)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="id_tipo_unidad_medida")
    private TipoUnidadMedida idTipoUnidadMedida;

    @Column(name = "activo")
    private Boolean activo;

    @Column(name = "descripcion", length = Integer.MAX_VALUE)
    private String descripcion;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public TipoUnidadMedida getIdTipoUnidadMedida() {
        return idTipoUnidadMedida;
    }
    public void setIdTipoUnidadMedida(TipoUnidadMedida idTipoUnidadMedida) {
        this.idTipoUnidadMedida = idTipoUnidadMedida;
    }
}