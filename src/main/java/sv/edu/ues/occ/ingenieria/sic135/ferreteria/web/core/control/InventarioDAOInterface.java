package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control;

import java.util.List;

public interface InventarioDAOInterface<T, ID> {
    void create(T registro) throws IllegalStateException, IllegalArgumentException;
    T findById(Object id) throws IllegalArgumentException, IllegalStateException;
    void delete(T registro) throws IllegalStateException, IllegalArgumentException;
    T update(T registro) throws IllegalStateException, IllegalArgumentException;
    List<T> findRange(int first, int max) throws IllegalArgumentException,IllegalAccessException;
    int count () throws IllegalStateException;
}
