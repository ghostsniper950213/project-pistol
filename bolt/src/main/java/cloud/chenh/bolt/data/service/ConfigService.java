package cloud.chenh.bolt.data.service;

import cloud.chenh.bolt.data.base.BaseService;
import cloud.chenh.bolt.data.entity.Config;
import cloud.chenh.bolt.data.repository.ConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfigService extends BaseService<Config> {

    @Autowired
    private ConfigRepository configRepository;

    @Override
    public ConfigRepository getRepository() {
        return configRepository;
    }

    public String findValByKey(String key) {
        Config config = getRepository().findByKey(key);
        return config == null ? null : config.getVal();
    }

    public void removeByKey(String key) {
        getRepository().removeByKey(key);
    }

    public void set(String key, String val) {
        getRepository().set(key, val);
    }

}
