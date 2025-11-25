package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
@LocalBean
public class BalanceComprobacionDAO {

    @PersistenceContext(unitName = "FerreteriaPU")
    private EntityManager em;

    // Obtiene las cuentas mayorizadas agrupadas por nombre de cuenta sumando el saldo total final (necesario para el balance de comprobacion)
    public List<Object[]> obtenerBalanceAgrupado(Long idLibroMayor) {
        return em.createQuery(
                        "SELECT d.nombreCuenta, SUM(d.saldo) " +
                                "FROM DetalleLibroMayor d " +
                                "WHERE d.idLibroMayor.id = :idLibroMayor " +
                                "GROUP BY d.nombreCuenta " +
                                "ORDER BY d.nombreCuenta",
                        Object[].class
                )
                .setParameter("idLibroMayor", idLibroMayor)
                .getResultList();
    }
}
