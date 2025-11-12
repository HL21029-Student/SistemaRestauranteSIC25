package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity;

import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "libro_diario", schema = "public")
public class LibroDiario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_libro_diario", nullable = false)
    private Long id;

    @Lob
    @Column(name = "nombre")
    private String nombre;

    @Lob
    @Column(name = "comentario")
    private String comentario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_detalle_libro_diario")
    private DetalleLibroDiario idDetalleLibroDiario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diario_ajuste_padre")
    private LibroDiario diarioAjustePadre;

    @OneToMany(mappedBy = "diarioAjustePadre")
    private Set<LibroDiario> libroDiarios = new LinkedHashSet<>();

    @OneToMany(mappedBy = "idLibroDiario")
    private Set<LibroMayor> libroMayors = new LinkedHashSet<>();

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

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public DetalleLibroDiario getIdDetalleLibroDiario() {
        return idDetalleLibroDiario;
    }

    public void setIdDetalleLibroDiario(DetalleLibroDiario idDetalleLibroDiario) {
        this.idDetalleLibroDiario = idDetalleLibroDiario;
    }

    public LibroDiario getDiarioAjustePadre() {
        return diarioAjustePadre;
    }

    public void setDiarioAjustePadre(LibroDiario diarioAjustePadre) {
        this.diarioAjustePadre = diarioAjustePadre;
    }

    public Set<LibroDiario> getLibroDiarios() {
        return libroDiarios;
    }

    public void setLibroDiarios(Set<LibroDiario> libroDiarios) {
        this.libroDiarios = libroDiarios;
    }

    public Set<LibroMayor> getLibroMayors() {
        return libroMayors;
    }

    public void setLibroMayors(Set<LibroMayor> libroMayors) {
        this.libroMayors = libroMayors;
    }

}