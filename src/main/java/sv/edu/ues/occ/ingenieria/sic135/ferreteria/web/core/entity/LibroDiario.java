package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity;

import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "libro_diario", schema = "public")
@NamedQueries({
    @NamedQuery(name = "LibroDiario.findDiarioAjustePadre", query = "SELECT l FROM LibroDiario l WHERE l.diarioAjustePadre IS NULL ORDER BY l.nombre")
})
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
    @JoinColumn(name = "diario_ajuste_padre")
    private LibroDiario diarioAjustePadre;


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

    public LibroDiario getDiarioAjustePadre() {
        return diarioAjustePadre;
    }

    public void setDiarioAjustePadre(LibroDiario diarioAjustePadre) {
        this.diarioAjustePadre = diarioAjustePadre;
    }


}