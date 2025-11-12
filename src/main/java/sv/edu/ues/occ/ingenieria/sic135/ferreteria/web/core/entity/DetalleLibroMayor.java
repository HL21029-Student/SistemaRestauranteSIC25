package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "detalle_libro_mayor", schema = "public")
public class DetalleLibroMayor {
    @Id
    @Column(name = "id_detalle_libro_mayor", nullable = false)
    private UUID id;

    @Column(name = "saldo")
    private BigDecimal saldo;

    @OneToMany(mappedBy = "idDetalleLibroMayor")
    private Set<LibroMayor> libroMayors = new LinkedHashSet<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public Set<LibroMayor> getLibroMayors() {
        return libroMayors;
    }

    public void setLibroMayors(Set<LibroMayor> libroMayors) {
        this.libroMayors = libroMayors;
    }

}