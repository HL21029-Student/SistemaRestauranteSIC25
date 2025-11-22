package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.LibroMayor;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

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


    public void create(LibroMayor entity) {
        getEntityManager().persist(entity);
    }

    public void edit(LibroMayor entity) {
        getEntityManager().merge(entity);
    }

    public void remove(LibroMayor entity) {
        try {
            if (entity != null) {
                // Asegurarse de que la entidad esté en el contexto de persistencia
                LibroMayor entityToRemove = getEntityManager().contains(entity) ?
                        entity : getEntityManager().merge(entity);
                getEntityManager().remove(entityToRemove);
            }
        } catch (Exception e) {
            Logger.getLogger(LibroMayorDAO.class.getName())
                    .log(Level.SEVERE, "Error al eliminar LibroMayor", e);
            throw e;
        }
    }
}
