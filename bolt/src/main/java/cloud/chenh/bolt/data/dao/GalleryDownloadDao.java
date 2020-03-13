package cloud.chenh.bolt.data.dao;

import cloud.chenh.bolt.constant.GalleryConstants;
import cloud.chenh.bolt.data.model.GalleryDownload;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
public class GalleryDownloadDao {

    private static final String RECORD_LOCALE = "./data/download.json";

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

    private List<GalleryDownload> read() {
        lock();
        try {
            File file = new File(RECORD_LOCALE);
            if (!file.exists()) {
                return new ArrayList<>();
            }
            return JSONObject.parseArray(FileUtils.readFileToString(file, Charset.defaultCharset()), GalleryDownload.class);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            unlock();
        }
    }

    private void write(List<GalleryDownload> galleryDownloads) {
        lock();
        try {
            FileUtils.writeStringToFile(new File(RECORD_LOCALE), JSONObject.toJSONString(galleryDownloads), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            unlock();
        }
    }

    public void save(GalleryDownload galleryDownload) {
        List<GalleryDownload> allDownloads = read();
        allDownloads.removeIf(download ->
                download.getDetail().getDetailUrl().equals(galleryDownload.getDetail().getDetailUrl())
        );
        allDownloads.add(galleryDownload);
        allDownloads.sort(Comparator.comparingLong(GalleryDownload::getTimestamp));
        write(allDownloads);
    }

    public void save(Collection<GalleryDownload> galleryDownloads) {
        List<GalleryDownload> allDownloads = read();
        allDownloads.removeIf(download ->
                galleryDownloads
                        .stream()
                        .anyMatch(d ->
                                d.getDetail().getDetailUrl().equals(download.getDetail().getDetailUrl())
                        )
        );
        allDownloads.addAll(galleryDownloads);
        allDownloads.sort(Comparator.comparingLong(GalleryDownload::getTimestamp));
        write(allDownloads);
    }

    public List<GalleryDownload> get() {
        List<GalleryDownload> galleryDownloads = read();
        galleryDownloads.sort(Comparator.comparingLong(GalleryDownload::getTimestamp));
        return galleryDownloads;
    }

    public GalleryDownload get(String detailUrl) {
        return read()
                .stream()
                .filter(download -> download.getDetail().getDetailUrl().equals(detailUrl))
                .findAny()
                .orElse(null);
    }

    public List<GalleryDownload> get(Collection<String> detailUrls) {
        return read()
                .stream()
                .filter(download -> detailUrls.contains(download.getDetail().getDetailUrl()))
                .collect(Collectors.toList());
    }

    public void remove(String detailUrl) {
        List<GalleryDownload> galleryDownloads = read();
        galleryDownloads.removeIf(download -> download.getDetail().getDetailUrl().equals(detailUrl));
        write(galleryDownloads);
    }

    public void remove(Collection<String> detailUrls) {
        List<GalleryDownload> galleryDownloads = read();
        galleryDownloads.removeIf(download -> detailUrls.contains(download.getDetail().getDetailUrl()));
        write(galleryDownloads);
    }

}
