package cloud.chenh.bolt.data.service;

import cloud.chenh.bolt.constant.GalleryConstants;
import cloud.chenh.bolt.data.dao.GalleryCacheDao;
import cloud.chenh.bolt.data.model.GalleryCache;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

@Service
public class GalleryImageService {

    private static final String CACHE_DIR = "./cache/";

    @Autowired
    private GalleryCacheDao galleryCacheDao;

    @Autowired
    private HttpClientService httpClientService;

    public void writeImage(String url, HttpServletResponse response) {
        GalleryCache galleryCache = getCache(url);
        if (url.contains(GalleryConstants.IMAGE_509)) {
            for (int i = 0; i < GalleryConstants.DEFAULT_RETRY; i++) {
                try {
                    InputStream inputStream = httpClientService.doGetStream(url, new HashMap<>());
                    OutputStream outputStream = response.getOutputStream();
                    StreamUtils.copy(inputStream, outputStream);
                    outputStream.flush();
                    outputStream.close();
                    break;
                } catch (IOException ignored) {
                }
            }
            return;
        }
        if (galleryCache == null) {
            for (int i = 0; i < GalleryConstants.DEFAULT_RETRY; i++) {
                try {
                    InputStream inputStream = httpClientService.doGetStream(url, new HashMap<>());
                    galleryCache = newCache(url, inputStream);
                    break;
                } catch (IOException ignored) {
                }
            }
        }
        if (galleryCache != null) {
            writeLocalImage(galleryCache.getPath(), response);
        }
    }

    public void writeLocalImage(String localPath, HttpServletResponse response) {
        try {
            File file = new File(localPath);
            if (!file.exists()) {
                return;
            }
            OutputStream outputStream = response.getOutputStream();
            outputStream.write(FileUtils.readFileToByteArray(file));
            outputStream.flush();
            outputStream.close();
        } catch (IOException ignored) {
        }
    }

    public GalleryCache newCache(String url, InputStream imageStream) throws IOException {
        String path = String.format(
                "%s/%d_%s.%s",
                CACHE_DIR,
                new Date().getTime(),
                UUID.randomUUID().toString(),
                FilenameUtils.getExtension(url)
        );
        FileUtils.copyInputStreamToFile(imageStream, new File(path));
        GalleryCache galleryCache = new GalleryCache(url, path);
        galleryCacheDao.save(galleryCache);
        return galleryCache;
    }

    public GalleryCache getCache(String url) {
        GalleryCache galleryCache = galleryCacheDao.get(url);
        if (galleryCache == null) {
            return null;
        }
        if (!new File(galleryCache.getPath()).exists()) {
            galleryCacheDao.remove(url);
            return null;
        }
        return galleryCache;
    }

    public void clearCache() {
        galleryCacheDao.clear();
        FileUtils.deleteQuietly(new File(CACHE_DIR));
    }

    public String getCacheSize() {
        File cacheDir = new File(CACHE_DIR);
        if (!cacheDir.exists() || !cacheDir.isDirectory()) {
            return "0";
        }
        long size = FileUtils.sizeOfDirectory(new File(CACHE_DIR));
        return FileUtils.byteCountToDisplaySize(size);
    }

}
