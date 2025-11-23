package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.BalanceInicialDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.LibroDiarioDAO;

import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.CuentaContable;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.DetalleLibroDiario;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.LibroDiario;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

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

    // saldos para tablas
    private List<CuentaSaldoDTO> activoCorrienteList = new ArrayList<>();
    private List<CuentaSaldoDTO> activoNoCorrienteList = new ArrayList<>();
    private List<CuentaSaldoDTO> pasivoCorrienteList = new ArrayList<>();
    private List<CuentaSaldoDTO> pasivoNoCorrienteList = new ArrayList<>();
    private List<CuentaSaldoDTO> patrimonioList = new ArrayList<>();

    // totales
    private BigDecimal totalActivoCorriente = BigDecimal.ZERO;
    private BigDecimal totalActivoNoCorriente = BigDecimal.ZERO;
    private BigDecimal totalPasivoCorriente = BigDecimal.ZERO;
    private BigDecimal totalPasivoNoCorriente = BigDecimal.ZERO;
    private BigDecimal totalPatrimonio = BigDecimal.ZERO;

    private BigDecimal totalActivo = BigDecimal.ZERO;
    private BigDecimal totalPasivo = BigDecimal.ZERO;
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
        totalPasivoCorriente = BigDecimal.ZERO;
        totalPasivoNoCorriente = BigDecimal.ZERO;
        totalPatrimonio = BigDecimal.ZERO;

        totalActivo = BigDecimal.ZERO;
        totalPasivo = BigDecimal.ZERO;
        totalPasivoPatrimonio = BigDecimal.ZERO;

        activoCorrienteList.clear();
        activoNoCorrienteList.clear();
        pasivoCorrienteList.clear();
        pasivoNoCorrienteList.clear();
        patrimonioList.clear();
    }

    // para mostrar en las tablas
    public static class CuentaSaldoDTO {

        private String codigo;
        private String nombre;
        private BigDecimal saldo;

        public CuentaSaldoDTO(String codigo, String nombre, BigDecimal saldo) {
            this.codigo = codigo;
            this.nombre = nombre;
            this.saldo = saldo;
        }

        public String getCodigo() {
            return codigo;
        }

        public String getNombre() {
            return nombre;
        }

        public BigDecimal getSaldo() {
            return saldo;
        }
    }


    private Long deducirSubtipoDesdePadre(CuentaContable cuenta) {

        CuentaContable padre = cuenta.getCuentaPadre();

        while (padre != null) {

            if (padre.getIdSubTipoCuenta() != null &&
                    padre.getIdSubTipoCuenta().getId() != 1) {

                return padre.getIdSubTipoCuenta().getId();
            }

            padre = padre.getCuentaPadre();
        }

        return 2L; // Corriente por defecto si no se logra deducir
    }

    private void calcularTotales() {

        resetTotales();

        // Agrupar saldos por cuenta debe-haber
        Map<CuentaContable, BigDecimal> saldos = partida1.stream()
                .collect(Collectors.groupingBy(
                        d -> d.getIdCuentaContable(),
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                d -> (Boolean.TRUE.equals(d.getDebe()))
                                        ? d.getMonto()
                                        : d.getMonto().negate(),
                                BigDecimal::add
                        )
                ));

        for (Map.Entry<CuentaContable, BigDecimal> entry : saldos.entrySet()) {

            CuentaContable cuenta = entry.getKey();
            BigDecimal saldo = entry.getValue();

            if (saldo.compareTo(BigDecimal.ZERO) == 0) continue;

            if (saldo.compareTo(BigDecimal.ZERO) < 0) {
                saldo = saldo.negate();
            }

            Long tipo = cuenta.getIdTipoCuenta().getId();

            Long subtipo = cuenta.getIdSubTipoCuenta() != null
                    ? cuenta.getIdSubTipoCuenta().getId()
                    : 1L;

            if (subtipo == 1) {
                subtipo = deducirSubtipoDesdePadre(cuenta);
            }

            // activo
            if (tipo == 1) {

                if (subtipo == 2) {
                    totalActivoCorriente = totalActivoCorriente.add(saldo);
                    activoCorrienteList.add(new CuentaSaldoDTO(
                            cuenta.getCodigo(), cuenta.getNombre(), saldo
                    ));
                } else if (subtipo == 3) {
                    totalActivoNoCorriente = totalActivoNoCorriente.add(saldo);
                    activoNoCorrienteList.add(new CuentaSaldoDTO(
                            cuenta.getCodigo(), cuenta.getNombre(), saldo
                    ));
                } else {
                    totalActivoCorriente = totalActivoCorriente.add(saldo);
                    activoCorrienteList.add(new CuentaSaldoDTO(
                            cuenta.getCodigo(), cuenta.getNombre(), saldo
                    ));
                }

                continue;
            }

            // pasivo
            if (tipo == 2) {

                // corriente
                if (subtipo == 2) {
                    totalPasivoCorriente = totalPasivoCorriente.add(saldo);
                    pasivoCorrienteList.add(new CuentaSaldoDTO(
                            cuenta.getCodigo(), cuenta.getNombre(), saldo
                    ));
                }

                // no corriente (largo plazo)
                else if (subtipo == 4) {
                    totalPasivoNoCorriente = totalPasivoNoCorriente.add(saldo);
                    pasivoNoCorrienteList.add(new CuentaSaldoDTO(
                            cuenta.getCodigo(), cuenta.getNombre(), saldo
                    ));
                }

                else {
                    totalPasivoCorriente = totalPasivoCorriente.add(saldo);
                    pasivoCorrienteList.add(new CuentaSaldoDTO(
                            cuenta.getCodigo(), cuenta.getNombre(), saldo
                    ));
                }

                continue;
            }

            // patrimonio
            if (tipo == 3 && subtipo == 5) {
                totalPatrimonio = totalPatrimonio.add(saldo);
                patrimonioList.add(new CuentaSaldoDTO(
                        cuenta.getCodigo(), cuenta.getNombre(), saldo
                ));
            }

            // otros tipos se ignoran
        }

        // Totales finales
        totalActivo = totalActivoCorriente.add(totalActivoNoCorriente);
        totalPasivo = totalPasivoCorriente.add(totalPasivoNoCorriente);
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
