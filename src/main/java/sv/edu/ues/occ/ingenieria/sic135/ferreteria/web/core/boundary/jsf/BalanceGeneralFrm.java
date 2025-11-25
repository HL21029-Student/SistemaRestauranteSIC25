package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Named("balanceGeneralFrm")
@ViewScoped
public class BalanceGeneralFrm implements Serializable {

    // En el futuro esto va a venir del libro mayor
    public static class CuentaSaldoDTO {

        private String nombre;
        private BigDecimal saldo;

        public CuentaSaldoDTO(String nombre, BigDecimal saldo) {
            this.nombre = nombre;
            this.saldo = saldo;
        }

        public String getNombre() {
            return nombre;
        }

        public BigDecimal getSaldo() {
            return saldo;
        }
    }

    // Listas para tablas
    private List<CuentaSaldoDTO> activoCorrienteList = new ArrayList<>();
    private List<CuentaSaldoDTO> activoNoCorrienteList = new ArrayList<>();
    private List<CuentaSaldoDTO> pasivoCorrienteList = new ArrayList<>();
    private List<CuentaSaldoDTO> pasivoNoCorrienteList = new ArrayList<>();
    private List<CuentaSaldoDTO> pasivoNoCorrienteList2 = new ArrayList<>(); // por si se separa más adelante
    private List<CuentaSaldoDTO> patrimonioList = new ArrayList<>();

    // Totales
    private BigDecimal totalActivoCorriente = BigDecimal.ZERO;
    private BigDecimal totalActivoNoCorriente = BigDecimal.ZERO;
    private BigDecimal totalActivo = BigDecimal.ZERO;

    private BigDecimal totalPasivoCorriente = BigDecimal.ZERO;
    private BigDecimal totalPasivoNoCorriente = BigDecimal.ZERO;
    private BigDecimal totalPasivo = BigDecimal.ZERO;

    private BigDecimal totalPatrimonio = BigDecimal.ZERO;
    private BigDecimal totalPasivoPatrimonio = BigDecimal.ZERO;

    @PostConstruct
    public void init() {
        // Aquí por ahora solo dejamos todo en 0,
        // cuando haya mayorización se llenarán las listas y totales.
        reset();
    }

    // Lo llamaremos cuando se quiera recalcular el balance general
    public void recalcular() {
        // TODO: cuando esté listo el Libro Mayor,
        // aquí se llenan las listas y los totales según el libro diario seleccionado.
        reset();
    }

    private void reset() {
        activoCorrienteList.clear();
        activoNoCorrienteList.clear();
        pasivoCorrienteList.clear();
        pasivoNoCorrienteList.clear();
        pasivoNoCorrienteList2.clear();
        patrimonioList.clear();

        totalActivoCorriente = BigDecimal.ZERO;
        totalActivoNoCorriente = BigDecimal.ZERO;
        totalActivo = BigDecimal.ZERO;

        totalPasivoCorriente = BigDecimal.ZERO;
        totalPasivoNoCorriente = BigDecimal.ZERO;
        totalPasivo = BigDecimal.ZERO;

        totalPatrimonio = BigDecimal.ZERO;
        totalPasivoPatrimonio = BigDecimal.ZERO;
    }

    // GETTERS

    public List<CuentaSaldoDTO> getActivoCorrienteList() {
        return activoCorrienteList;
    }

    public List<CuentaSaldoDTO> getActivoNoCorrienteList() {
        return activoNoCorrienteList;
    }

    public List<CuentaSaldoDTO> getPasivoCorrienteList() {
        return pasivoCorrienteList;
    }

    public List<CuentaSaldoDTO> getPasivoNoCorrienteList() {
        return pasivoNoCorrienteList;
    }

    public List<CuentaSaldoDTO> getPatrimonioList() {
        return patrimonioList;
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

    public BigDecimal getTotalPasivoCorriente() {
        return totalPasivoCorriente;
    }

    public BigDecimal getTotalPasivoNoCorriente() {
        return totalPasivoNoCorriente;
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
