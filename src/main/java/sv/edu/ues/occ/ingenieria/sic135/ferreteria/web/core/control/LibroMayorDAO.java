package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.LibroMayor;

import java.io.Serializable;

@Stateless
@LocalBean
public class LibroMayorDAO extends InventarioDefaultDataAccess<LibroMayor, Object> implements Serializable {
    @PersistenceContext(unitName = "FerreteriaPU")
    private  EntityManager em;

    public LibroMayorDAO() {
        super(LibroMayor.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<LibroMayor> getEntityClass() {
        return LibroMayor.class;
    }
}
