package cloud.chenh.bolt.data.dao;

import cloud.chenh.bolt.constant.GalleryConstants;
import cloud.chenh.bolt.data.model.GalleryCache;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class GalleryCacheDao {

    private static final String RECORD_LOCALE = "./data/cache.json";

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

    private List<GalleryCache> read() {
        lock();
        try {
            File file = new File(RECORD_LOCALE);
            if (!file.exists()) {
                return new ArrayList<>();
            }
            return JSONObject.parseArray(FileUtils.readFileToString(file, Charset.defaultCharset()), GalleryCache.class);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            unlock();
        }
    }

    private void write(List<GalleryCache> galleryCaches) {
        lock();
        try {
            FileUtils.writeStringToFile(new File(RECORD_LOCALE), JSONObject.toJSONString(galleryCaches), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            unlock();
        }
    }

    public void save(GalleryCache galleryCache) {
        List<GalleryCache> allCaches = read();
        allCaches.removeIf(cache ->
                cache.getUrl().equals(galleryCache.getUrl())
        );
        allCaches.add(galleryCache);
        write(allCaches);
    }

    public List<GalleryCache> get() {
        return read();
    }

    public GalleryCache get(String url) {
        return read()
                .stream()
                .filter(cache -> cache.getUrl().equals(url))
                .findAny()
                .orElse(null);
    }

    public void remove(String url) {
        List<GalleryCache> allCaches = read();
        allCaches.removeIf(cache ->
                cache.getUrl().equals(url)
        );
        write(allCaches);
    }

    public void clear() {
        write(new ArrayList<>());
    }

}
