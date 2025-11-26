package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.TipoAlmacen;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.Usuario;

@Stateless
@LocalBean
public class UsuarioDAO extends InventarioDefaultDataAccess<Usuario, Object> implements Serializable {

    @PersistenceContext(unitName = "SistemaRestaurantePU")
    private EntityManager em;

    public UsuarioDAO() {
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

    /**
     * Crea un nuevo usuario en la base de datos.
     * @param usuario objeto {@link Usuario} a persistir.
     * @return el usuario creado y persistido.
     */
    public Usuario crear(Usuario usuario) {
        if (usuario.getId() == null) {
            usuario.setId(UUID.randomUUID());
        }
        if (usuario.getActivo() == null) {
            usuario.setActivo(true);
        }
        em.persist(usuario);
        return usuario;
    }

    /**
     * Obtiene todos los usuarios registrados en la base de datos.
     * @return lista de todos los usuarios, ordenados por nombre completo.
     */
    public List<Usuario> findAll() {
        return em.createQuery("SELECT u FROM Usuario u ORDER BY u.nombreCompleto", Usuario.class)
                .getResultList();
    }

    /**
     * Obtiene todos los usuarios que se encuentran activos.
     * @return lista de usuarios activos, ordenados por nombre completo.
     */
    public List<Usuario> findActivos() {
        return em.createQuery("SELECT u FROM Usuario u WHERE u.activo = true ORDER BY u.nombreCompleto", Usuario.class)
                .getResultList();
    }

    /**
     * Busca un usuario por su identificador único.
     * @param id identificador único del usuario (UUID).
     * @return un {@link Optional} con el usuario encontrado, o vacío si no existe.
     */
    public Optional<Usuario> findById(UUID id) {
        return Optional.ofNullable(em.find(Usuario.class, id));
    }

    /**
     * Busca un usuario por su nombre de usuario (username).
     * @param username nombre de usuario a buscar.
     * @return un {@link Optional} con el usuario correspondiente, o vacío si no existe.
     */
    public Optional<Usuario> findByUsername(String username) {
        try {
            Usuario usuario = em.createQuery("SELECT u FROM Usuario u WHERE u.username = :username", Usuario.class)
                    .setParameter("username", username)
                    .getSingleResult();
            return Optional.of(usuario);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Busca un usuario por su dirección de correo electrónico.
     * @param email correo electrónico a buscar.
     * @return un {@link Optional} con el usuario correspondiente, o vacío si no existe.
     */
    public Optional<Usuario> findByEmail(String email) {
        try {
            Usuario usuario = em.createQuery("SELECT u FROM Usuario u WHERE u.email = :email", Usuario.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return Optional.of(usuario);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Obtiene los usuarios activos que poseen un rol específico.
     * @param rol nombre del rol a filtrar.
     * @return lista de usuarios activos con el rol indicado.
     */
    public List<Usuario> findByRol(String rol) {
        return em.createQuery("SELECT u FROM Usuario u WHERE u.rol = :rol AND u.activo = true", Usuario.class)
                .setParameter("rol", rol)
                .getResultList();
    }

    /**
     * Actualiza la información de un usuario existente.
     * @param usuario objeto {@link Usuario} con los datos actualizados.
     * @return el usuario actualizado y sincronizado con la base de datos.
     */
    public Usuario actualizar(Usuario usuario) {
        return em.merge(usuario);
    }

    /**
     * Desactiva un usuario estableciendo su campo {@code activo} en {@code false}.
     * @param id identificador del usuario a desactivar.
     * @return {@code true} si el usuario fue encontrado y desactivado,
     *         {@code false} en caso contrario.
     */
    public boolean desactivar(UUID id) {
        Usuario usuario = em.find(Usuario.class, id);
        if (usuario != null) {
            usuario.setActivo(false);
            em.merge(usuario);
            return true;
        }
        return false;
    }

    /**
     * Activa un usuario estableciendo su campo {@code activo} en {@code true}.
     * @param id identificador del usuario a activar.
     * @return {@code true} si el usuario fue encontrado y activado,
     *         {@code false} en caso contrario.
     */
    public boolean activar(UUID id) {
        Usuario usuario = em.find(Usuario.class, id);
        if (usuario != null) {
            usuario.setActivo(true);
            em.merge(usuario);
            return true;
        }
        return false;
    }

    /**
     * Verifica si ya existe un usuario con el nombre de usuario especificado.
     * @param username nombre de usuario a verificar.
     * @return {@code true} si ya existe, {@code false} en caso contrario.
     */
    public boolean existeUsername(String username) {
        Long count = em.createQuery("SELECT COUNT(u) FROM Usuario u WHERE u.username = :username", Long.class)
                .setParameter("username", username)
                .getSingleResult();
        return count > 0;
    }

    /**
     * Verifica si ya existe un usuario con el correo electrónico especificado.
     * @param email correo electrónico a verificar.
     * @return {@code true} si ya existe, {@code false} en caso contrario.
     */
    public boolean existeEmail(String email) {
        Long count = em.createQuery("SELECT COUNT(u) FROM Usuario u WHERE u.email = :email", Long.class)
                .setParameter("email", email)
                .getSingleResult();
        return count > 0;
    }


}
