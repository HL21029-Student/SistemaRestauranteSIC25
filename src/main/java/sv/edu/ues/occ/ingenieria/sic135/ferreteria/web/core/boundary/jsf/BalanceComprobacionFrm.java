package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.BalanceComprobacionDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.LibroMayorDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.dto.BalanceComprobacionFila;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.LibroMayor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Named("balanceComprobacionFrm")
@ViewScoped
public class BalanceComprobacionFrm implements Serializable {

    @Inject
    private BalanceComprobacionDAO balanceDAO;
    @Inject
    private LibroMayorDAO libroMayorDAO;


    private Long idLibroMayorSeleccionado;
    private List<BalanceComprobacionFila> balance;

    @PostConstruct
    public void init() {
        balance = new ArrayList<>();
    }

    public void generarBalance() {
        balance = new ArrayList<>();

        if (idLibroMayorSeleccionado == null) {
            return;
        }

        List<Object[]> resultados = balanceDAO.obtenerBalanceAgrupado(idLibroMayorSeleccionado);

        for (Object[] fila : resultados) {
            String cuenta = (String) fila[0];
            BigDecimal saldo = (BigDecimal) fila[1];

            BigDecimal debe = BigDecimal.ZERO;
            BigDecimal haber = BigDecimal.ZERO;

            if (saldo.compareTo(BigDecimal.ZERO) > 0) {
                debe = saldo;
            } else {
                haber = saldo.abs();
            }

            balance.add(new BalanceComprobacionFila(cuenta, debe, haber));
        }
    }

    public List<LibroMayor> getLibrosMayor() {
        return libroMayorDAO.findAll();
    }


    // totales
    public BigDecimal getTotalDebe() {
        return balance.stream()
                .map(BalanceComprobacionFila::getDebe)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalHaber() {
        return balance.stream()
                .map(BalanceComprobacionFila::getHaber)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Long getIdLibroMayorSeleccionado() {
        return idLibroMayorSeleccionado;
    }

    public void setIdLibroMayorSeleccionado(Long idLibroMayorSeleccionado) {
        this.idLibroMayorSeleccionado = idLibroMayorSeleccionado;
    }

    public List<BalanceComprobacionFila> getBalance() { return balance; }


}
