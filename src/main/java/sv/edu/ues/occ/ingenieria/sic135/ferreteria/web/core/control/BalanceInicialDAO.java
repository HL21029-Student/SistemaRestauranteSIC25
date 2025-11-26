package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.DetalleLibroDiario;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class BalanceInicialDAO implements Serializable {

    @PersistenceContext(unitName = "SistemaRestaurantePU")
    private EntityManager em;

    //la partida 1 del libro diario elegido
    public List<DetalleLibroDiario> obtenerPartida1(Long idLibro) {

        if (idLibro == null) {
            return Collections.emptyList();
        }

        try {
            TypedQuery<DetalleLibroDiario> query = em.createQuery(
                    "SELECT d FROM DetalleLibroDiario d " +
                            "WHERE d.libroDiario.id = :idLibro " +
                            "AND d.numeroPartida = 1 " +
                            "ORDER BY d.idCuentaContable.codigo ASC",
                    DetalleLibroDiario.class
            );

            query.setParameter("idLibro", idLibro);
            return query.getResultList();

        } catch (Exception e) {
            Logger.getLogger(BalanceInicialDAO.class.getName())
                    .log(Level.SEVERE, e.getMessage(), e);

            return Collections.emptyList();
        }
    }
}
