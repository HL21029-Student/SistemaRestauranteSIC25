package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "libro_mayor", schema = "public")
public class LibroMayor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_libro_mayor", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_libro_diario")
    private LibroDiario idLibroDiario;

    @Lob
    @Column(name = "observacion")
    private String observacion;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LibroDiario getIdLibroDiario() {
        return idLibroDiario;
    }

    public void setIdLibroDiario(LibroDiario idLibroDiario) {
        this.idLibroDiario = idLibroDiario;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }


}