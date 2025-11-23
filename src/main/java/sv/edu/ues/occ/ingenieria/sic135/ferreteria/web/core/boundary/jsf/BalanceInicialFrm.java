package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.BalanceInicialDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.LibroDiarioDAO;

import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.DetalleLibroDiario;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.LibroDiario;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Named("balanceInicialFrm")
@ViewScoped
public class BalanceInicialFrm implements Serializable {

    @Inject
    LibroDiarioDAO libroDiarioDAO;

    @Inject
    BalanceInicialDAO balanceDAO;

    private Long idLibroDiarioSeleccionado;
    private List<LibroDiario> listaLibros;

    private List<DetalleLibroDiario> partida1;

    private BigDecimal totalActivoCorriente = BigDecimal.ZERO;
    private BigDecimal totalActivoNoCorriente = BigDecimal.ZERO;
    private BigDecimal totalPasivo = BigDecimal.ZERO;
    private BigDecimal totalPatrimonio = BigDecimal.ZERO;

    private BigDecimal totalActivo = BigDecimal.ZERO;
    private BigDecimal totalPasivoPatrimonio = BigDecimal.ZERO;

    @PostConstruct
    public void init() {
        listaLibros = libroDiarioDAO.findAll();
    }

    public void cargarBalanceInicial() {

        if (idLibroDiarioSeleccionado == null) {
            partida1 = Collections.emptyList();
            resetTotales();
            return;
        }

        partida1 = balanceDAO.obtenerPartida1(idLibroDiarioSeleccionado);

        if (partida1 == null) {
            partida1 = Collections.emptyList();
        }

        calcularTotales();
    }

    private void resetTotales() {
        totalActivoCorriente = BigDecimal.ZERO;
        totalActivoNoCorriente = BigDecimal.ZERO;
        totalPasivo = BigDecimal.ZERO;
        totalPatrimonio = BigDecimal.ZERO;
        totalActivo = BigDecimal.ZERO;
        totalPasivoPatrimonio = BigDecimal.ZERO;
    }

    private void calcularTotales() {

        // ACTIVO CORRIENTE (tipo 1 / subtipo 2 / DEBE = true)
        totalActivoCorriente = partida1.stream()
                .filter(d ->
                        d.getIdCuentaContable().getIdTipoCuenta().getId() == 1 &&
                                d.getIdCuentaContable().getIdSubTipoCuenta().getId() == 2 &&
                                d.getDebe() != null && d.getDebe()
                )
                .map(DetalleLibroDiario::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // ACTIVO NO CORRIENTE (tipo 1 / subtipo 1 / DEBE = true)
        totalActivoNoCorriente = partida1.stream()
                .filter(d ->
                        d.getIdCuentaContable().getIdTipoCuenta().getId() == 1 &&
                                d.getIdCuentaContable().getIdSubTipoCuenta().getId() == 1 &&
                                d.getDebe() != null && d.getDebe()
                )
                .map(DetalleLibroDiario::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // PASIVO (tipo 2 / HABER = false)
        totalPasivo = partida1.stream()
                .filter(d ->
                        d.getIdCuentaContable().getIdTipoCuenta().getId() == 2 &&
                                d.getDebe() != null && !d.getDebe()
                )
                .map(DetalleLibroDiario::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // PATRIMONIO (tipo 3 / HABER = false)
        totalPatrimonio = partida1.stream()
                .filter(d ->
                        d.getIdCuentaContable().getIdTipoCuenta().getId() == 3 &&
                                d.getDebe() != null && !d.getDebe()
                )
                .map(DetalleLibroDiario::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Totales generales
        totalActivo = totalActivoCorriente.add(totalActivoNoCorriente);
        totalPasivoPatrimonio = totalPasivo.add(totalPatrimonio);
    }

    public Long getIdLibroDiarioSeleccionado() {
        return idLibroDiarioSeleccionado;
    }

    public void setIdLibroDiarioSeleccionado(Long idLibroDiarioSeleccionado) {
        this.idLibroDiarioSeleccionado = idLibroDiarioSeleccionado;
    }

    public List<LibroDiario> getListaLibros() {
        return listaLibros;
    }

    public List<DetalleLibroDiario> getPartida1() {
        return partida1;
    }

    public BigDecimal getTotalActivoCorriente() {
        return totalActivoCorriente;
    }

    public BigDecimal getTotalActivoNoCorriente() {
        return totalActivoNoCorriente;
    }

    public BigDecimal getTotalActivo() {
        return totalActivo;
    }

    public BigDecimal getTotalPasivo() {
        return totalPasivo;
    }

    public BigDecimal getTotalPatrimonio() {
        return totalPatrimonio;
    }

    public BigDecimal getTotalPasivoPatrimonio() {
        return totalPasivoPatrimonio;
    }
}
