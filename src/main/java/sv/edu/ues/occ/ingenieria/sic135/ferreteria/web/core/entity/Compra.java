package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Date;

@Entity
@Table(name = "compra", schema = "public")
public class Compra {
    @Id
    @Column(name = "id_compra", nullable = false)
    private Long id;

    @NotNull // 10 de nov
    @Temporal(TemporalType.TIMESTAMP) // Necesario para almacenar fecha y hora
    @Column(name = "fecha", nullable = false)
    private Date fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "id_proveedor")
    private Proveedor idProveedor;

    @Size(max = 10)
    @Column(name = "estado", length = 10)
    private String estado;

    @Column(name = "observaciones", length = Integer.MAX_VALUE)
    private String observaciones;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Proveedor getIdProveedor() {
        return idProveedor;
    }
    public void setIdProveedor(Proveedor idProveedor) {
        this.idProveedor = idProveedor;
    }
}