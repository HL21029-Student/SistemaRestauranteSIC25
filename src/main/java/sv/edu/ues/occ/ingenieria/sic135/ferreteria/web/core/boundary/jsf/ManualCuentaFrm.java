package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.ManualCuenta;

import java.io.Serializable;
import java.util.List;

@Named("manualCuentaFrm")
@ViewScoped
public class ManualCuentaFrm implements Serializable {

    @PersistenceContext(unitName = "SistemaRestaurantePU")
    private EntityManager em;

    private List<ManualCuenta> lista;

    @PostConstruct
    public void init() {
        lista = em.createQuery(
                "SELECT m FROM ManualCuenta m ORDER BY m.idCuentaContable.codigo",
                ManualCuenta.class
        ).getResultList();
    }

    public List<ManualCuenta> getLista() {
        return lista;
    }
}

// Clase antigua usada solo para lectura directa ya no es necesaria con el CRUD basado en DefaultFrm.
// Puede eliminarse o dejarse sin usar; por ahora, no se referencia desde la vista ManualCuentas.xhtml.
