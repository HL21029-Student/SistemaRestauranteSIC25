package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "tipo_almacen", schema = "public")
public class TipoAlmacen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_almacen", nullable = false)
    private Integer id;

    @Size(max = 155, min = 5, message = "debe tener minimo 5 caracteres")
    @NotNull(
            message = "El nombre no puede ser nulo")
    @Column(name = "nombre", length = 155)
    private String nombre;

    @Column(name = "activo")
    private Boolean activo;

    @Column(name = "obsevaciones", length = Integer.MAX_VALUE)
    private String obsevaciones;



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

    public String getObsevaciones() {
        return obsevaciones;
    }

    public void setObsevaciones(String obsevaciones) {
        this.obsevaciones = obsevaciones;
    }

    public boolean isEmpty() {
        return this.id == null || this.id == 0;
    }

    public Integer getIdTipoAlmacen() {
        return id;
    }

    public java.util.stream.Stream<TipoAlmacen> stream() {
        return java.util.stream.Stream.of(this);
    }



}