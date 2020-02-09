package cloud.chenh.bolt.data.service;

import cloud.chenh.bolt.data.dao.ConfigDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfigService {

    @Autowired
    private ConfigDao configDao;

    public String get(String key) {
        return configDao.get(key);
    }

    public void set(String key, String val) {
        configDao.set(key, val);
    }

    public void remove(String key) {
        set(key, "");
    }

}
