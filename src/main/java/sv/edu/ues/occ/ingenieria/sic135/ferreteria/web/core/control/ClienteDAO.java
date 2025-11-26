package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.Cliente;

import java.io.Serializable;
import java.util.List;

@Stateless
@LocalBean
public class ClienteDAO extends InventarioDefaultDataAccess<Cliente, Object> implements Serializable {
    @PersistenceContext(unitName="SistemaRestaurantePU")
    private EntityManager em;

    public ClienteDAO() {
        super(Cliente.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<Cliente> getEntityClass() {
        return Cliente.class;
    }

    public List<Cliente> buscarClientePorNombre(String nombre, int first, int max) {
        if (nombre != null && !nombre.isBlank()) {
            try {
                TypedQuery<Cliente> q = em.createQuery(
                        "SELECT c FROM Cliente c WHERE LOWER(c.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) AND c.activo = true",
                        Cliente.class);
                q.setParameter("nombre", nombre);
                q.setFirstResult(first);
                q.setMaxResults(max);
                return q.getResultList();
            } catch (Exception ex) {
                throw new IllegalStateException("Error al buscar clientes activos por nombre", ex);
            }
        }
        return List.of();
    }

}