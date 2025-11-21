package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.SubTipoCuenta;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Stateless
@LocalBean
public class SubTipoCuentaDAO extends InventarioDefaultDataAccess<SubTipoCuenta, Object> implements Serializable {
    /**
     * Manejador de entidades JPA utilizado para ejecutar operaciones
     * de persistencia sobre la base de datos.
     */
    @PersistenceContext(unitName = "ferreteriaPU")
    private EntityManager em;

    // Constructor por defecto que inicializa la clase base con el tipo de entidad.
    public SubTipoCuentaDAO() {
        super(SubTipoCuenta.class);
    }

    //aquí seguimos el patron de la implementación de los DAO
    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<SubTipoCuenta> getEntityClass() {
        return SubTipoCuenta.class;
    }

    /**
     * Persiste un nuevo registro de {@link SubTipoCuenta} en la base de datos.
     * @param subTipoCuenta Entidad {@link SubTipoCuenta} a persistir.
     * @return La entidad persistida.
     * @throws IllegalArgumentException si {@code subTipoCuenta} es {@code null}.
     */
    public SubTipoCuenta crear(SubTipoCuenta subTipoCuenta) {
        if (subTipoCuenta == null) {
            throw new IllegalArgumentException("El sub-tipo de cuenta no puede ser nulo");
        }
        em.persist(subTipoCuenta);
        return subTipoCuenta;
    }

    /**
     * Obtiene la lista completa de subtipos de cuenta almacenados en la base de datos.
     * @return Lista de entidades {@link SubTipoCuenta}, ordenadas alfabéticamente por nombre.
     */
    public List<SubTipoCuenta> findAll() {
        return em.createQuery(
                        "SELECT s FROM SubTipoCuenta s ORDER BY s.nombre",
                        SubTipoCuenta.class)
                .getResultList();
    }

    /**
     * Busca un {@link SubTipoCuenta} por su identificador único.
     * @param id Identificador del sub-tipo de cuenta.
     * @return Un {@link Optional} que contiene la entidad si existe; vacío en caso contrario.
     */
    public Optional<SubTipoCuenta> findById(Long id) {
        return Optional.ofNullable(em.find(SubTipoCuenta.class, id));
    }

    /**
     * Busca subtipos de cuenta cuyo nombre contenga el texto indicado,
     * sin distinguir entre mayúsculas y minúsculas.
     * @param nombre Texto parcial o completo a buscar en el campo {@code nombre}.
     * @return Lista de entidades {@link SubTipoCuenta} que coinciden con el criterio.
     *         Si no hay coincidencias, devuelve una lista vacía.
     */
    public List<SubTipoCuenta> findByNombre(String nombre) {
        return em.createQuery(
                        "SELECT s FROM SubTipoCuenta s WHERE LOWER(s.nombre) LIKE LOWER(:nombre)",
                        SubTipoCuenta.class)
                .setParameter("nombre", "%" + nombre + "%")
                .getResultList();
    }

    /**
     * Actualiza los datos de un {@link SubTipoCuenta} existente en la base de datos.
     * @param subTipoCuenta Entidad {@link SubTipoCuenta} con los valores actualizados.
     * @return La entidad actualizada.
     * @throws IllegalArgumentException si {@code subTipoCuenta} es {@code null}.
     */
    public SubTipoCuenta actualizar(SubTipoCuenta subTipoCuenta) {
        if (subTipoCuenta == null) {
            throw new IllegalArgumentException("El sub-tipo de cuenta no puede ser nulo");
        }
        return em.merge(subTipoCuenta);
    }

    /**
     * Elimina un {@link SubTipoCuenta} de la base de datos si no tiene registros
     * dependientes en la tabla {@code CuentaContable}.
     * @param id Identificador del sub-tipo de cuenta a eliminar.
     * @return {@code true} si la eliminación fue exitosa;
     *         {@code false} si no existe o tiene dependencias activas.
     */
    public boolean eliminar(Long id) {
        SubTipoCuenta subTipoCuenta = em.find(SubTipoCuenta.class, id);
        if (subTipoCuenta != null) {
            Long count = em.createQuery(
                            "SELECT COUNT(c) FROM CuentaContable c WHERE c.idSubTipoCuenta = :subTipoCuenta",
                            Long.class)
                    .setParameter("subTipoCuenta", subTipoCuenta)
                    .getSingleResult();
            if (count == 0) {
                em.remove(subTipoCuenta);
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica si ya existe un sub-tipo de cuenta con el nombre especificado.
     * @param nombre Nombre del sub-tipo de cuenta a verificar.
     * @return {@code true} si ya existe un registro con ese nombre; {@code false} en caso contrario.
     */
    public boolean existeNombre(String nombre) {
        Long count = em.createQuery(
                        "SELECT COUNT(s) FROM SubTipoCuenta s WHERE s.nombre = :nombre",
                        Long.class)
                .setParameter("nombre", nombre)
                .getSingleResult();
        return count > 0;
    }


}
