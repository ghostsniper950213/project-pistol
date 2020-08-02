package cloud.chenh.bolt.data.base;

import java.util.List;

public abstract class BaseService<T extends BaseEntity> {

    public abstract BaseRepository<T> getRepository();

    public List<T> get() {
        return getRepository().get();
    }

    public T get(Integer id) {
        return getRepository().get(id);
    }

    public T remove(Integer id) {
        return getRepository().remove(id);
    }

    public List<T> remove(List<Integer> ids) {
        return getRepository().remove(ids);
    }

    public T save(T entity) {
        return getRepository().save(entity);
    }

    public List<T> save(List<T> entities) {
        return getRepository().save(entities);
    }

}
