package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.EstadoResultadoDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.LibroMayorDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.dto.EstadoResultadoDTO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.LibroMayor;

@Named("estadoResultadoFrm")
@ViewScoped
public class EstadoResultadoFrm implements Serializable {

    @Inject
    private EstadoResultadoDAO estadoDAO;

    @Inject
    private LibroMayorDAO libroMayorDAO;

    private Long idLibroMayorSeleccionado;
    private EstadoResultadoDTO estado;

    // Tasa ISR por defecto (25%)
    private BigDecimal tasaISR;

    // -----------------------------------------------------------
    // INIT: valores iniciales
    // -----------------------------------------------------------
    @PostConstruct
    public void init() {
        estado = null;
        tasaISR = new BigDecimal("0.25");
    }

    // -----------------------------------------------------------
    // GENERAR ESTADO DE RESULTADOS
    // -----------------------------------------------------------
    public void generar() {
        if (idLibroMayorSeleccionado == null) {
            estado = null;
            return;
        }

        estado = estadoDAO.generarPorLibroMayor(idLibroMayorSeleccionado, tasaISR);
    }

    // -----------------------------------------------------------
    // LISTA DE LIBROS MAYOR PARA EL <selectOneMenu>
    // -----------------------------------------------------------
    public List<LibroMayor> getLibrosMayor() {
        return libroMayorDAO.findAll();
    }

    // -----------------------------------------------------------
    // GETTERS & SETTERS
    // -----------------------------------------------------------
    public Long getIdLibroMayorSeleccionado() {
        return idLibroMayorSeleccionado;
    }

    public void setIdLibroMayorSeleccionado(Long idLibroMayorSeleccionado) {
        this.idLibroMayorSeleccionado = idLibroMayorSeleccionado;
    }

    public EstadoResultadoDTO getEstado() {
        return estado;
    }

    public void setEstado(EstadoResultadoDTO estado) {
        this.estado = estado;
    }

    public BigDecimal getTasaISR() {
        return tasaISR;
    }

    public void setTasaISR(BigDecimal tasaISR) {
        this.tasaISR = tasaISR;
    }
}
