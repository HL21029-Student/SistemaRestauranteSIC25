package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;

public abstract class InventarioDefaultDataAccess<T, ID> implements InventarioDAOInterface<T, ID>{

    public abstract EntityManager getEntityManager();//obliga a usar em

    protected abstract Class<T> getEntityClass();

    //variable para almacenar la informacion del tipo en tiempo de ejecucion
    private final Class<T> TipoDato;

    public InventarioDefaultDataAccess(Class<T> TipoDato){
        this.TipoDato = TipoDato;
    }

// refactorizacion de los metodos comunes
    public void create (final T registro) throws  IllegalStateException, IllegalArgumentException {
        EntityManager em = null;

        if(registro==null){
            throw new IllegalArgumentException("Parametro no valido: entity is null");
        }
        try {
            em = getEntityManager();
            if(em == null){
                throw new IllegalStateException("Error al acceder al repositorio");
            }
            em.persist(registro);

        }catch (Exception ex){
            throw new  IllegalStateException("Error al acceder al repositorio",ex);
        }

    }

    public T findById(final Object id) throws IllegalArgumentException, IllegalStateException{
        EntityManager em = null;

        if(id==null){
            throw new IllegalArgumentException("Parametro no valido: ID");
        }
        try {
            em = getEntityManager();
            if(em == null){
                throw new IllegalStateException("Error al acceder al repositorio");
            }

        }catch (Exception ex){
            throw new  IllegalStateException("Error al acceder al repositorio",ex);
        }
        return em.find(TipoDato, id);
    }


    public void delete(final T registro) throws IllegalArgumentException, IllegalStateException {
        EntityManager em = null;
        if (registro == null){
            throw new IllegalArgumentException("Parametro no valido: registro is null");
        }
        try{
            em=getEntityManager();
            if(em == null){
                throw new IllegalStateException("Error al acceder al repositorio");
            }
            T managedEntity = em.merge(registro);//gestion de entidad antes de eliminar
            em.remove(managedEntity);
        } catch (Exception ex){
            throw new IllegalStateException("Error al acceder al repositorio", ex);
        }
    }

    public T update(final T registro) throws IllegalArgumentException, IllegalStateException {
        EntityManager em =null;
        if (registro ==null){
            throw new IllegalArgumentException("Parametro  no valido: registro is null");
        }
        try {
            em = getEntityManager();
            if (em == null){
                throw new IllegalStateException("Error al acceder al repositorio");
            }
            return em.merge(registro);
        }catch(Exception ex){
            throw new IllegalStateException("Error al acceder al repositorio", ex);
        }
    }

public List<T> findRange(int first, int max)throws IllegalArgumentException, IllegalStateException {
    if (first <0 || max < 1) {
        throw new IllegalArgumentException("Parametros no validos");
    }
    try {
        EntityManager em = getEntityManager();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(getEntityClass());
        Root<T> root = cq.from(getEntityClass());
        CriteriaQuery<T> all = cq.select(root);
        TypedQuery<T> allQuery = em.createQuery(cq);
        allQuery.setFirstResult(first);
        allQuery.setMaxResults(max);
        return allQuery.getResultList();
    } catch (Exception e){
        throw  new IllegalStateException("No se pudo acceder al repositorio");
    }
}

public int count() throws IllegalStateException {
    EntityManager em = null;
    try {
        em = getEntityManager();
        if (em == null) {
            throw new IllegalStateException("Error al acceder al repositorio");
        }

    } catch (Exception ex) {
        throw new IllegalStateException("No se pudo acceder al repositorio", ex);
    }
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
    Root<T> root = cq.from(getEntityClass());
    cq.select(cb.count(root));
    return em.createQuery(cq).getSingleResult().intValue();
}
}
