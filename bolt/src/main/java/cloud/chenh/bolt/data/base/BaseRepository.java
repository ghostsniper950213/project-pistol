package cloud.chenh.bolt.data.base;

import cloud.chenh.bolt.constant.ProjectConstants;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Slf4j
public abstract class BaseRepository<T extends BaseEntity> {

    public List<T> buffer = new CopyOnWriteArrayList<>();
    public ExecutorService pool = Executors.newCachedThreadPool();

    public final Lock rwLock = new ReentrantLock();

    public static final String DATA_DIR = ProjectConstants.JAR_DIR + File.separator + "data";
    public static final String DATA_EXT = "json";

    @SuppressWarnings("unchecked")
    public Class<T> getEntityClass() {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @PostConstruct
    public void read() {
        try {
            rwLock.lock();
            buffer = new CopyOnWriteArrayList<>();
            File file = new File(DATA_DIR + File.separator + getEntityClass().getSimpleName() + "." + DATA_EXT);
            if (!file.exists()) {
                return;
            }
            String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            if (StringUtils.isBlank(content)) {
                return;
            }
            List<T> entityStore = JSONObject.parseArray(content, getEntityClass());
            if (CollectionUtils.isEmpty(entityStore)) {
                return;
            }
            entityStore.sort(Comparator.comparingInt(BaseEntity::getId));
            buffer.addAll(entityStore);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rwLock.unlock();
        }
    }

    public void write() {
        try {
            rwLock.lock();
            File file = new File(DATA_DIR + File.separator + getEntityClass().getSimpleName() + "." + DATA_EXT);
            String content = JSONObject.toJSONString(buffer);
            FileUtils.writeStringToFile(file, content, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rwLock.unlock();
        }
    }

    public List<T> get() {
        try {
            rwLock.lock();
            return buffer;
        } finally {
            rwLock.unlock();
        }
    }

    public T get(Integer id) {
        try {
            rwLock.lock();
            return buffer.parallelStream().filter(entity -> id.equals(entity.getId())).findAny().orElse(null);
        } finally {
            rwLock.unlock();
        }
    }

    public T remove(Integer id) {
        try {
            return remove(Collections.singletonList(id)).get(0);
        } finally {
            rwLock.unlock();
        }
    }

    public List<T> remove(List<Integer> ids) {
        try {
            rwLock.lock();
            List<T> entities = buffer.parallelStream().filter(entity -> ids.contains(entity.getId())).collect(Collectors.toList());
            buffer.removeAll(entities);
            pool.submit(this::write);
            return entities;
        } finally {
            rwLock.unlock();
        }
    }

    public T save(T entity) {
        try {
            rwLock.lock();
            return save(Collections.singletonList(entity)).get(0);
        } finally {
            rwLock.unlock();
        }
    }

    public List<T> save(List<T> entities) {
        try {
            rwLock.lock();
            for (T entity : entities) {
                if (entity.getId() != null) {
                    T oldOne = get(entity.getId());
                    if (oldOne == null) {
                        entity.setId(null);
                    } else {
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
