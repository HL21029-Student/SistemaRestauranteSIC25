package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.BalanceDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.dto.BalanceDTO;

import java.io.Serializable;
import java.util.List;

@Named("balanceFrm")
@ViewScoped
public class BalanceFrm implements Serializable {

    @Inject
    BalanceDAO balanceDAO;

    private List<BalanceDTO> lista;

    private double totalDeudor;
    private double totalAcreedor;

    @PostConstruct
    public void init() {
        lista = balanceDAO.obtenerBalance();

        totalDeudor = lista.stream()
                .filter(BalanceDTO::isDeudor)
                .mapToDouble(BalanceDTO::getSaldo)
                .sum();

        totalAcreedor = lista.stream()
                .filter(BalanceDTO::isAcreedor)
                .mapToDouble(BalanceDTO::getSaldo)
                .sum();
    }

    public List<BalanceDTO> getLista() {
        return lista;
    }

    public double getTotalDeudor() {
        return totalDeudor;
    }

    public double getTotalAcreedor() {
        return totalAcreedor;
    }
}
