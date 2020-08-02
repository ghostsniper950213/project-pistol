package cloud.chenh.bolt.data.repository;

import cloud.chenh.bolt.data.base.BaseEntity;
import cloud.chenh.bolt.data.base.BaseRepository;
import cloud.chenh.bolt.data.entity.Image;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ImageRepository extends BaseRepository<Image> {

    public Image findByUrl(String url) {
        return get().parallelStream().filter(image -> url.equals(image.getUrl())).findAny().orElse(null);
    }

    public Image removeByUrl(String url) {
        return removeByUrl(Collections.singletonList(url)).get(0);
    }

    public List<Image> removeByUrl(List<String> urls) {
        return remove(
                get().parallelStream()
                        .filter(image -> urls.contains(image.getUrl()))
                        .map(BaseEntity::getId)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public List<Image> save(List<Image> entities) {
        try {
            rwLock.lock();
            for (Image entity : entities) {
                if (entity.getId() != null) {
                    Image oldOne = get(entity.getId());
                    if (oldOne == null) {
                        entity.setId(null);
                    } else {
                        buffer.set(buffer.indexOf(oldOne), entity);
                    }
                } else if (entity.getUrl() != null) {
                    Image oldOne = findByUrl(entity.getUrl());
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

}
