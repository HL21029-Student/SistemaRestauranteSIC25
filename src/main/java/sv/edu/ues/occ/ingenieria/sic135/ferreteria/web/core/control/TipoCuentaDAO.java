package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.TipoAlmacen;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.TipoCuenta;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Stateless
@LocalBean
public class TipoCuentaDAO extends InventarioDefaultDataAccess<TipoCuenta, Object> implements Serializable {

    @PersistenceContext(unitName = "ferreteriaPU")
    private EntityManager em;

    public TipoCuentaDAO() {
        super(TipoCuenta.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<TipoCuenta> getEntityClass() {
        return TipoCuenta.class;
    }

    /**
     * Crea un nuevo registro de {@link TipoCuenta} en la base de datos.
     * @param tipoCuenta Entidad {@link TipoCuenta} a persistir.
     * @return La entidad persistida.
     */
    public TipoCuenta crear(TipoCuenta tipoCuenta) {
        em.persist(tipoCuenta);
        return tipoCuenta;
    }

    /**
     * Obtiene la lista completa de tipos de cuenta existentes en la base de datos.
     * @return Lista de entidades {@link TipoCuenta}, ordenadas alfabéticamente por nombre.
     */
    public List<TipoCuenta> findAll() {
        return em.createQuery("SELECT t FROM TipoCuenta t ORDER BY t.nombre", TipoCuenta.class)
                .getResultList();
    }

    /**
     * Busca un {@link TipoCuenta} por su identificador único.
     * @param id Identificador del tipo de cuenta.
     * @return Un {@link Optional} que contiene la entidad si se encuentra; vacío en caso contrario.
     */
    public Optional<TipoCuenta> findById(Long id) {
        return Optional.ofNullable(em.find(TipoCuenta.class, id));
    }

    /**
     * Busca tipos de cuenta cuyo nombre contenga el texto especificado (búsqueda parcial e insensible a mayúsculas).
     * @param nombre Texto parcial o completo a buscar en el campo "nombre".
     * @return Lista de entidades {@link TipoCuenta} que coinciden con el criterio.
     */
    public List<TipoCuenta> findByNombre(String nombre) {
        return em.createQuery(
                        "SELECT t FROM TipoCuenta t WHERE LOWER(t.nombre) LIKE LOWER(:nombre)",
                        TipoCuenta.class)
                .setParameter("nombre", "%" + nombre + "%")
                .getResultList();
    }

    /**
     * Actualiza un registro existente de {@link TipoCuenta} en la base de datos.
     *
     * @param tipoCuenta Entidad {@link TipoCuenta} con los nuevos valores a actualizar.
     * @return La entidad actualizada.
     */
    public TipoCuenta actualizar(TipoCuenta tipoCuenta) {
        return em.merge(tipoCuenta);
    }

    /**
     * Elimina un tipo de cuenta de la base de datos si no está siendo utilizado por ninguna cuenta contable.
     * @param id Identificador del tipo de cuenta a eliminar.
     * @return {@code true} si la eliminación fue exitosa; {@code false} si no existe o tiene dependencias.
     */
    public boolean eliminar(Long id) {
        TipoCuenta tipoCuenta = em.find(TipoCuenta.class, id);
        if (tipoCuenta != null) {
            Long count = em.createQuery(
                            "SELECT COUNT(c) FROM CuentaContable c WHERE c.idTipoCuenta = :tipoCuenta",
                            Long.class)
                    .setParameter("tipoCuenta", tipoCuenta)
                    .getSingleResult();
            if (count == 0) {
                em.remove(tipoCuenta);
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica si ya existe un tipo de cuenta con el nombre especificado.
     * @param nombre Nombre del tipo de cuenta a verificar.
     * @return {@code true} si ya existe un registro con ese nombre; {@code false} en caso contrario.
     */
    public boolean existeNombre(String nombre) {
        Long count = em.createQuery(
                        "SELECT COUNT(t) FROM TipoCuenta t WHERE t.nombre = :nombre",
                        Long.class)
                .setParameter("nombre", nombre)
                .getSingleResult();
        return count > 0;
    }


}
