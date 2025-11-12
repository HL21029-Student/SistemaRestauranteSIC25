package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "tipo_cuenta", schema = "public")
public class TipoCuenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_cuenta", nullable = false)
    private Long id;

    @Size(max = 255)
    @Column(name = "nombre")
    private String nombre;

    @Lob
    @Column(name = "descripcion")
    private String descripcion;

    @OneToMany(mappedBy = "idTipoCuenta")
    private Set<CuentaContable> cuentaContables = new LinkedHashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Set<CuentaContable> getCuentaContables() {
        return cuentaContables;
    }

    public void setCuentaContables(Set<CuentaContable> cuentaContables) {
        this.cuentaContables = cuentaContables;
    }

}