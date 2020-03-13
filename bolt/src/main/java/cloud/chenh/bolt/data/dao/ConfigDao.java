package cloud.chenh.bolt.data.dao;

import cloud.chenh.bolt.constant.GalleryConstants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class ConfigDao {

    private static final String CONFIG_LOCALE = "./config.txt";

    private AtomicBoolean readWriteLock = new AtomicBoolean(false);

    private void lock() {
        try {
            while (readWriteLock.get()) {
                TimeUnit.MILLISECONDS.sleep(GalleryConstants.LOCK_WAIT);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        readWriteLock.set(true);
    }

    private void unlock() {
        readWriteLock.set(false);
    }

    private Map<String, String> read() {
        lock();
        try {
            File file = new File(CONFIG_LOCALE);
            if (!file.exists()) {
                return new LinkedHashMap<>();
            }
            List<String> lines = FileUtils.readLines(file, Charset.defaultCharset());
            Map<String, String> configs = new LinkedHashMap<>();
            lines.forEach(line -> {
                if (StringUtils.isNotBlank(line) && !line.startsWith("#") && line.contains("=")) {
                    String[] parts = line.split("=");
                    if (parts.length == 2) {
                        String key = parts[0].trim();
                        String val = parts[1].trim();
                        configs.put(key, val);
                    }
                }
            });
            return configs;
        } catch (IOException e) {
            e.printStackTrace();
            return new LinkedHashMap<>();
        } finally {
            unlock();
        }
    }

    private void write(Map<String, String> configs) {
        lock();
        try {
            List<String> lines = new ArrayList<>();
            configs.forEach((k, v) -> lines.add(k + "=" + v));
            FileUtils.writeLines(new File(CONFIG_LOCALE), lines);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            unlock();
        }
    }

    public String get(String key) {
        return read().get(key);
    }

    public void remove(String key) {
        Map<String, String> configs = read();
        configs.remove(key);
        write(configs);
    }

    public void set(String key, String val) {
        Map<String, String> configs = read();
        configs.put(key, val);
        write(configs);
    }

}
