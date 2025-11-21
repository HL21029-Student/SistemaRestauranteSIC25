package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.CuentaContable;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.TipoAlmacen;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.Usuario;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.ManualCuenta;

@Stateless
@LocalBean
public class CuentaContableDAO extends InventarioDefaultDataAccess<CuentaContable, Object> implements Serializable {

    @PersistenceContext(unitName = "FerreteriaPU")
    private EntityManager em;

    public CuentaContableDAO() {
        super(CuentaContable.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<CuentaContable> getEntityClass() {
        return CuentaContable.class;
    }

    /**
     * Crea una nueva cuenta contable y registra los datos de auditoría
     * @param cuenta Entidad {@link CuentaContable} a persistir.
     * @param idUsuarioCreador Identificador del usuario que realiza la creación.
     * @return La entidad {@link CuentaContable} persistida.
     */
    public CuentaContable crear(CuentaContable cuenta, UUID idUsuarioCreador) {
        Usuario usuario = em.find(Usuario.class, idUsuarioCreador);
        OffsetDateTime ahora = OffsetDateTime.now();

        cuenta.setCreateAt(ahora);
        cuenta.setUpdateAt(ahora);
        cuenta.setIdUsuario(usuario);

        em.persist(cuenta);
        return cuenta;
    }

    /**
     * Obtiene todas las cuentas contables junto con sus relaciones relevantes
     * @return Lista de todas las cuentas contables existentes.
     */
    public List<CuentaContable> findAll() {
        return em.createQuery("SELECT c FROM CuentaContable c " +
                        "LEFT JOIN FETCH c.idTipoCuenta " +
                        "LEFT JOIN FETCH c.idSubTipoCuenta " +
                        "LEFT JOIN FETCH c.idUsuario " +
                        "ORDER BY c.codigo", CuentaContable.class)
                .getResultList();
    }

    /**
     * Busca una cuenta contable por su ID.
     * @param id Identificador único de la cuenta contable.
     * @return {@link Optional} que puede contener la cuenta encontrada.
     */
    public Optional<CuentaContable> findById(Long id) {
        return Optional.ofNullable(em.find(CuentaContable.class, id));
    }

    /**
     * Busca una cuenta contable por ID, cargando además sus relaciones
     * @param id Identificador de la cuenta contable.
     * @return {@link Optional} con la cuenta encontrada si existe.
     */
    public Optional<CuentaContable> findByIdWithAuditoria(Long id) {
        try {
            CuentaContable cuenta = em.createQuery("SELECT c FROM CuentaContable c " +
                            "LEFT JOIN FETCH c.idTipoCuenta " +
                            "LEFT JOIN FETCH c.idSubTipoCuenta " +
                            "LEFT JOIN FETCH c.idUsuario " +
                            "WHERE c.id = :id", CuentaContable.class)
                    .setParameter("id", id)
                    .getSingleResult();
            return Optional.of(cuenta);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Busca una cuenta contable por su código único.
     * @param codigo Código contable a buscar.
     * @return {@link Optional} con la cuenta contable encontrada si existe.
     */
    public Optional<CuentaContable> findByCodigo(String codigo) {
        try {
            CuentaContable cuenta = em.createQuery("SELECT c FROM CuentaContable c " +
                            "LEFT JOIN FETCH c.idTipoCuenta " +
                            "LEFT JOIN FETCH c.idSubTipoCuenta " +
                            "LEFT JOIN FETCH c.idUsuario " +
                            "WHERE c.codigo = :codigo", CuentaContable.class)
                    .setParameter("codigo", codigo)
                    .getSingleResult();
            return Optional.of(cuenta);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Actualiza una cuenta contable y registra el usuario y la fecha de
     * @param cuenta Entidad {@link CuentaContable} a actualizar.
     * @param idUsuarioModificador Identificador del usuario que realiza la modificación.
     * @return La entidad actualizada.
     */
    public CuentaContable actualizar(CuentaContable cuenta, UUID idUsuarioModificador) {
        Usuario usuario = em.find(Usuario.class, idUsuarioModificador);
        cuenta.setUpdateAt(OffsetDateTime.now());
        cuenta.setIdUsuario(usuario);
        return em.merge(cuenta);
    }

    /**
     * Elimina una cuenta contable si no tiene dependencias (subcuentas, movimientos o manuales).
     * @param idCuenta ID de la cuenta a eliminar.
     * @return {@code true} si la eliminación fue exitosa, {@code false} en caso contrario.
     */
    public boolean eliminar(Long idCuenta) {
        Optional<CuentaContable> cuentaOpt = findById(idCuenta);
        if (cuentaOpt.isPresent() && puedeSerEliminada(cuentaOpt.get())) {
            em.remove(cuentaOpt.get());
            return true;
        }
        return false;
    }

    /**
     * Lista las cuentas creadas por un usuario específico.
     * @param idUsuario Identificador del usuario.
     * @return Lista de cuentas creadas por ese usuario.
     */
    public List<CuentaContable> findCuentasCreadasPorUsuario(UUID idUsuario) {
        return em.createQuery("SELECT c FROM CuentaContable c " +
                        "WHERE c.idUsuario.id = :idUsuario " +
                        "ORDER BY c.createAt DESC", CuentaContable.class)
                .setParameter("idUsuario", idUsuario)
                .getResultList();
    }

    /**
     * Obtiene las cuentas modificadas dentro de los últimos "n" días.
     * @param dias Número de días a retroceder.
     * @return Lista de cuentas modificadas recientemente.
     */
    public List<CuentaContable> findCuentasModificadasRecientemente(int dias) {
        OffsetDateTime fechaLimite = OffsetDateTime.now().minusDays(dias);
        return em.createQuery("SELECT c FROM CuentaContable c " +
                        "WHERE c.updateAt >= :fechaLimite " +
                        "ORDER BY c.updateAt DESC", CuentaContable.class)
                .setParameter("fechaLimite", fechaLimite)
                .getResultList();
    }

    /**
     * Obtiene las cuentas creadas dentro de los últimos "n" días.
     * @param dias Número de días a retroceder.
     * @return Lista de cuentas creadas recientemente.
     */
    public List<CuentaContable> findCuentasCreadasRecientemente(int dias) {
        OffsetDateTime fechaLimite = OffsetDateTime.now().minusDays(dias);
        return em.createQuery("SELECT c FROM CuentaContable c " +
                        "WHERE c.createAt >= :fechaLimite " +
                        "ORDER BY c.createAt DESC", CuentaContable.class)
                .setParameter("fechaLimite", fechaLimite)
                .getResultList();
    }

    /**
     * Devuelve todas las cuentas que no tienen cuenta padre (cuentas raíz).
     * @return Lista de cuentas contables principales.
     */
    public List<CuentaContable> findCuentasPadre() {
        return em.createQuery("SELECT c FROM CuentaContable c " +
                        "WHERE c.cuentaPadre IS NULL " +
                        "ORDER BY c.codigo", CuentaContable.class)
                .getResultList();
    }

    /**
     * Busca las subcuentas asociadas a una cuenta padre.
     * @param idCuentaPadre Identificador de la cuenta padre.
     * @return Lista de subcuentas.
     */
    public List<CuentaContable> findSubCuentas(Long idCuentaPadre) {
        return em.createQuery("SELECT c FROM CuentaContable c " +
                        "WHERE c.cuentaPadre.id = :idPadre " +
                        "ORDER BY c.codigo", CuentaContable.class)
                .setParameter("idPadre", idCuentaPadre)
                .getResultList();
    }

    /**
     * Obtiene todo el árbol contable, incluyendo subcuentas.
     * @return Lista jerárquica de cuentas contables.
     */
    public List<CuentaContable> getArbolContableCompleto() {
        return em.createQuery("SELECT c FROM CuentaContable c " +
                        "LEFT JOIN FETCH c.cuentaContables " +
                        "WHERE c.cuentaPadre IS NULL " +
                        "ORDER BY c.codigo", CuentaContable.class)
                .getResultList();
    }

    /**
     * Busca todas las cuentas por un tipo de cuenta específico.
     * @param idTipoCuenta ID del tipo de cuenta.
     * @return Lista de cuentas que pertenecen a ese tipo.
     */
    public List<CuentaContable> findByTipoCuenta(Long idTipoCuenta) {
        return em.createQuery("SELECT c FROM CuentaContable c " +
                        "WHERE c.idTipoCuenta.id = :idTipoCuenta " +
                        "ORDER BY c.codigo", CuentaContable.class)
                .setParameter("idTipoCuenta", idTipoCuenta)
                .getResultList();
    }

    /**
     * Busca todas las cuentas por un subtipo de cuenta específico.
     * @param idSubTipoCuenta ID del subtipo de cuenta.
     * @return Lista de cuentas que pertenecen a ese subtipo.
     */
    public List<CuentaContable> findBySubTipoCuenta(Long idSubTipoCuenta) {
        return em.createQuery("SELECT c FROM CuentaContable c " +
                        "WHERE c.idSubTipoCuenta.id = :idSubTipoCuenta " +
                        "ORDER BY c.codigo", CuentaContable.class)
                .setParameter("idSubTipoCuenta", idSubTipoCuenta)
                .getResultList();
    }

    /**
     * Verifica si existe una cuenta con el código especificado.
     * @param codigo Código contable a verificar.
     * @return {@code true} si el código ya existe, {@code false} si es nuevo.
     */
    public boolean existeCodigo(String codigo) {
        Long count = em.createQuery("SELECT COUNT(c) FROM CuentaContable c WHERE c.codigo = :codigo", Long.class)
                .setParameter("codigo", codigo)
                .getSingleResult();
        return count > 0;
    }

    /**
     * Verifica si un código contable ya existe en otra cuenta (distinta a la actual).
     * @param codigo Código contable a verificar.
     * @param id     ID de la cuenta actual (para excluirla de la búsqueda).
     * @return {@code true} si el código está en uso por otra cuenta.
     */
    public boolean existeCodigoConDiferenteId(String codigo, Long id) {
        Long count = em.createQuery("SELECT COUNT(c) FROM CuentaContable c WHERE c.codigo = :codigo AND c.id <> :id", Long.class)
                .setParameter("codigo", codigo)
                .setParameter("id", id)
                .getSingleResult();
        return count > 0;
    }

    /**
     * Verifica si una cuenta puede ser eliminada. No puede eliminarse si:
     * @param cuenta Cuenta contable a evaluar.
     * @return {@code true} si puede eliminarse, {@code false} si tiene dependencias.
     */
    private boolean puedeSerEliminada(CuentaContable cuenta) {
        Long countSubcuentas = em.createQuery("SELECT COUNT(sc) FROM CuentaContable sc WHERE sc.cuentaPadre.id = :idCuenta", Long.class)
                .setParameter("idCuenta", cuenta.getId())
                .getSingleResult();
        if (countSubcuentas > 0) return false;

        Long countMovimientos = em.createQuery("SELECT COUNT(d) FROM DetalleLibroDiario d WHERE d.idCuentaContable.id = :idCuenta", Long.class)
                .setParameter("idCuenta", cuenta.getId())
                .getSingleResult();

        // Ya no se bloquea por manuales; se eliminarán en cascada.

        return (countMovimientos == 0);
    }

    /**
     * Obtiene un reporte general de auditoría de todas las cuentas contables,
     * mostrando información básica y los usuarios asociados.
     * @return Lista de arreglos de objetos con los campos:
     */
    public List<Object[]> getReporteAuditoriaCompleto() {
        return em.createQuery(
                        "SELECT c.codigo, c.nombre, c.createAt, u.username, " +
                                "c.updateAt, u.username " +
                                "FROM CuentaContable c " +
                                "LEFT JOIN c.idUsuario u " +
                                "ORDER BY c.createAt DESC", Object[].class)
                .getResultList();
    }

    //para poder buscar por nombre en el libro diario
    public List<CuentaContable> findByNombreLike(final String nombre, int first, int max) {
        try{
            if(nombre!=null && !nombre.isBlank() && first>=0 && max>0){
                TypedQuery<CuentaContable> q= em.createNamedQuery("CuentaContable.findByNombreLike", CuentaContable.class);
                q.setParameter("nombre", "%" + nombre.trim().toUpperCase()+"%");
                q.setFirstResult(first);
                q.setMaxResults(max);
                return q.getResultList();
            }
        } catch (Exception e) {
            Logger.getLogger(CuentaContableDAO.class.getName()).log(Level.SEVERE, e.getMessage(), e);
        }
        return List.of();
    }

    public CuentaContable findbyId(Long id) {
        try {
            if (id == null) {
                return null;
            }
            return em.find(CuentaContable.class, id);
        } catch (Exception ex) {
            Logger.getLogger(CuentaContableDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return null;
        }
    }

    public List<CuentaContable> findByIdCuentaContable(Long idCuentaContable, int first, int max) {
        if(idCuentaContable != null){
            try{
                TypedQuery<CuentaContable> q = em.createNamedQuery("CuentaContable.findByIdCuentaContable", CuentaContable.class);
                q.setParameter("id", idCuentaContable);
                q.setFirstResult(first);
                q.setMaxResults(max);
                return q.getResultList();
            } catch (Exception e) {
                Logger.getLogger(CuentaContableDAO.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            }
        }
        return List.of();
    }

    public List<ManualCuenta> listarManualCuentas() {
        return em.createQuery("SELECT m FROM ManualCuenta m ORDER BY m.idCuentaContable.codigo", ManualCuenta.class)
                .getResultList();
    }


}
