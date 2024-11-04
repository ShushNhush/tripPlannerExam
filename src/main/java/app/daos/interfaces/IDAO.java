package app.daos.interfaces;

import java.util.Set;

public interface IDAO<T, I> {

    Set<T> getAll();
    T getById(I id);
    T create(T t);
    T update(I id, T t);
    T delete(I id);

}
