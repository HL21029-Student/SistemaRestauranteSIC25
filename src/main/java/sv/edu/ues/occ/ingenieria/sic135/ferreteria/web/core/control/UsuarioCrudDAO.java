package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.Usuario;

import java.io.Serializable;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class UsuarioCrudDAO extends InventarioDefaultDataAccess<Usuario, Object> implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(UsuarioCrudDAO.class.getName());

    @PersistenceContext(unitName = "SistemaRestaurantePU")
    private EntityManager em;

    public UsuarioCrudDAO() {
        super(Usuario.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<Usuario> getEntityClass() {
        return Usuario.class;
    }

    @Override
    public void create(Usuario entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Usuario nulo");
        }
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());
        }
        if (entity.getActivo() == null) {
            entity.setActivo(true);
        }
        try {
            em.persist(entity);
            em.flush(); // forzar SQL y detectar errores temprano
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error persistiendo Usuario", ex);
            throw new IllegalStateException("Error al persistir Usuario: " + ex.getMessage(), ex);
        }
    }

    public Usuario findById(UUID id) {
        if (id == null) {
            return null;
        }
        return em.find(Usuario.class, id);
    }
}
