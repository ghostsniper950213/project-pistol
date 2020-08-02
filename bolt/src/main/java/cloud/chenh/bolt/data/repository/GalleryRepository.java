package cloud.chenh.bolt.data.repository;

import cloud.chenh.bolt.data.base.BaseRepository;
import cloud.chenh.bolt.data.entity.Gallery;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Repository
public class GalleryRepository extends BaseRepository<Gallery> {

    @Override
    public List<Gallery> save(List<Gallery> entities) {
        try {
            rwLock.lock();
            for (Gallery entity : entities) {
                if (entity.getId() != null) {
                    Gallery oldOne = get(entity.getId());
                    if (oldOne == null) {
                        entity.setId(null);
                    } else {
                        buffer.set(buffer.indexOf(oldOne), entity);
                    }
                } else if (entity.getUrl() != null) {
                    Gallery oldOne = findByUrl(entity.getUrl());
                    if (oldOne == null) {
                        entity.setId(null);
                    } else {
                        entity.setId(oldOne.getId());
                        buffer.set(buffer.indexOf(oldOne), entity);
                    }
                }
                if (entity.getId() == null) {
                    if (!CollectionUtils.isEmpty(buffer)) {
                        entity.setId(buffer.get(buffer.size() - 1).getId() + 1);
                    } else {
                        entity.setId(0);
                    }
                    buffer.add(entity);
                }
            }
            pool.submit(this::write);
            return entities;
        } finally {
            rwLock.unlock();
        }
    }

    public Gallery findByUrl(String url) {
        return get().parallelStream().filter(gallery -> gallery.getUrl().equals(url)).findAny().orElse(null);
    }

}
