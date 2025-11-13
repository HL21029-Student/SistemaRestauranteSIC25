package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "detalle_libro_mayor", schema = "public")
@NamedQueries({
        @NamedQuery(name="DetalleLibroMayor.findByLibroMayorId",
                query="SELECT d FROM DetalleLibroMayor d WHERE d.idLibroMayor.id = :libroMayorId")
})
public class DetalleLibroMayor {
    @Id
    @Column(name = "id_detalle_libro_mayor", nullable = false)
    private UUID id;

    @Column(name = "saldo")
    private BigDecimal saldo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_libro_mayor")
    private LibroMayor idLibroMayor;

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

    public LibroMayor getIdLibroMayor() {
        return idLibroMayor;
    }

    public void setIdLibroMayor(LibroMayor idLibroMayor) {
        this.idLibroMayor = idLibroMayor;
    }
}