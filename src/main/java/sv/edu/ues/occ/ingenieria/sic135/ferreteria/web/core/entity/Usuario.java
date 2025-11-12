package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "usuario", schema = "public")
public class Usuario {
    @Id
    @Column(name = "id_usuario", nullable = false)
    private UUID id;

    @Size(max = 100)
    @Column(name = "username", length = 100)
    private String username;

    @Size(max = 255)
    @Column(name = "email")
    private String email;

    @Size(max = 255)
    @Column(name = "nombre_completo")
    private String nombreCompleto;

    @Size(max = 255)
    @Column(name = "rol")
    private String rol;

    @Column(name = "activo")
    private Boolean activo;

    @OneToMany(mappedBy = "idUsuario")
    private Set<CuentaContable> cuentaContables = new LinkedHashSet<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Set<CuentaContable> getCuentaContables() {
        return cuentaContables;
    }

    public void setCuentaContables(Set<CuentaContable> cuentaContables) {
        this.cuentaContables = cuentaContables;
    }

}