package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.VentaDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.Venta;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Named("reporteVentasDiariasFrm")
@RequestScoped
public class ReporteVentasDiariasFrm implements Serializable {

    @Inject
    private VentaDAO ventaDAO;

    private LocalDate fecha;

    private List<Venta> ventas;

    @PostConstruct
    public void init() {
        fecha = LocalDate.now();
        cargarVentas();
    }

    public void cargarVentas() {
        // Implementación sencilla: usar findRange para recuperar todas las ventas
        // En una implementación real, aquí se filtraría por fecha.
        ventas = ventaDAO.findRange(0, 100);
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public List<Venta> getVentas() {
        return ventas;
    }
}

