package cloud.chenh.bolt.data.repository;

import cloud.chenh.bolt.data.base.BaseRepository;
import cloud.chenh.bolt.data.entity.Config;
import org.springframework.stereotype.Repository;

@Repository
public class ConfigRepository extends BaseRepository<Config> {

    public Config findByKey(String key) {
        return get().parallelStream()
                .filter(config -> key.equals(config.getKey()))
                .findAny()
                .orElse(null);
    }

    public void removeByKey(String key) {
        Config config = findByKey(key);
        if (config != null && config.getId() != null) {
            remove(config.getId());
        }
    }

    public void set(String key, String val) {
        Config config = findByKey(key);
        if (config == null) {
            config = new Config();
            config.setKey(key);
        }
        config.setVal(val);
        save(config);
    }

}
