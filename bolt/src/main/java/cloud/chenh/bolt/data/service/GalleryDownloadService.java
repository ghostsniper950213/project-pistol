package cloud.chenh.bolt.data.service;

import cloud.chenh.bolt.constant.GalleryConstants;
import cloud.chenh.bolt.data.dao.GalleryDownloadDao;
import cloud.chenh.bolt.data.model.GalleryDetail;
import cloud.chenh.bolt.data.model.GalleryDownload;
import cloud.chenh.bolt.data.parser.GalleryDetailParser;
import cloud.chenh.bolt.exception.DownloadStopException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class GalleryDownloadService {

    private static final String DOWNLOAD_DIR = "./download/";

    private String downloadingUrl;

    @Autowired
    private GalleryDownloadDao galleryDownloadDao;

    @Autowired
    private GalleryDetailService galleryDetailService;

    @Autowired
    private HttpClientService httpClientService;

    public List<GalleryDownload> getAllSorted() {
        List<GalleryDownload> galleryDownloads = galleryDownloadDao.get();
        galleryDownloads.sort(Comparator.comparingLong(GalleryDownload::getTimestamp));
        return galleryDownloads;
    }

    public List<GalleryDownload> getAllSortedReversed() {
        List<GalleryDownload> galleryDownloads = getAllSorted();
        Collections.reverse(galleryDownloads);
        return galleryDownloads;
    }

    public GalleryDownload get(String detailUrl) {
        return galleryDownloadDao.get(detailUrl);
    }

    public void add(GalleryDownload galleryDownload) {
        galleryDownload.getDetail().setDownload(true);
        save(galleryDownload);
        downloadNext();
    }

    public void save(GalleryDownload galleryDownload) {
        galleryDownloadDao.save(galleryDownload);
    }

    public void save(Collection<GalleryDownload> galleryDownloads) {
        galleryDownloadDao.save(galleryDownloads);
    }

    public void remove(List<String> detailUrls) {
        if (detailUrls.contains(downloadingUrl)) {
            downloadingUrl = null;
        }
        List<GalleryDownload> removeDownloads = galleryDownloadDao.get(detailUrls);
        removeDownloads.forEach(download -> {
            try {
                FileUtils.deleteDirectory(new File(DOWNLOAD_DIR + "/" + download.getTimestamp()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        galleryDownloadDao.remove(detailUrls);
        downloadNext();
    }

    public void resume(List<String> detailUrls) {
        List<GalleryDownload> galleryDownloads = galleryDownloadDao.get(detailUrls);
        galleryDownloads.forEach(download -> {
            download.setStatus(GalleryDownload.StatusEnum.DOWNLOADING);
            String cover = download.getCover();
            if (cover != null && !new File(cover).exists()) {
                download.setCover(null);
            }
            String[] images = download.getImages();
            for (int i = 0; i < images.length; i++) {
                if (images[i] != null && !new File(images[i]).exists()) {
                    images[i] = null;
                }
            }
        });
        save(galleryDownloads);
        downloadNext();
    }

    public void pause(List<String> detailUrls) {
        if (detailUrls.contains(downloadingUrl)) {
            downloadingUrl = null;
        }
        List<GalleryDownload> galleryDownloads = galleryDownloadDao.get(detailUrls);
        galleryDownloads.forEach(galleryDownload -> galleryDownload.setStatus(GalleryDownload.StatusEnum.PAUSED));
        save(galleryDownloads);
        downloadNext();
    }

    public List<String> keys() {
        return galleryDownloadDao
                .get()
                .stream()
                .map(download -> download.getDetail().getDetailUrl())
                .collect(Collectors.toList());
    }

    public synchronized void downloadNext() {
        if (StringUtils.isBlank(downloadingUrl)) {
            GalleryDownload galleryDownload = getDownloadable();
            if (galleryDownload != null) {
                downloadingUrl = galleryDownload.getDetail().getDetailUrl();
                new Thread(() -> {
                    try {
                        download(galleryDownload);
                    } catch (DownloadStopException ignored) {
                    }
                }).start();
            }
        }
    }

    private GalleryDownload getDownloadable() {
        return galleryDownloadDao
                .get()
                .stream()
                .filter(info -> info.getStatus() == GalleryDownload.StatusEnum.DOWNLOADING)
                .findFirst()
                .orElse(null);
    }

    private void checkAlive(GalleryDownload galleryDownload) throws DownloadStopException {
        if (!galleryDownload.getDetail().getDetailUrl().equals(downloadingUrl)) {
            throw new DownloadStopException();
        }
    }

    private void saveAfterCheck(GalleryDownload galleryDownload) throws DownloadStopException {
        checkAlive(galleryDownload);
        save(galleryDownload);
    }

    private void writeStreamAfterCheck(GalleryDownload galleryDownload, InputStream inputStream, File file)
            throws DownloadStopException, IOException {

        checkAlive(galleryDownload);
        FileUtils.copyInputStreamToFile(inputStream, file);
    }


    private void download(GalleryDownload galleryDownload) throws DownloadStopException {
        GalleryDetail detail = galleryDownload.getDetail();

        String detailUrl = detail.getDetailUrl();
        String coverUrl = detail.getCoverUrl();

        String cover = String.format(
                "%s/%d/cover.%s",
                DOWNLOAD_DIR,
                galleryDownload.getTimestamp(),
                FilenameUtils.getExtension(coverUrl)
        );
        for (int i = 0; i < GalleryConstants.DEFAULT_RETRY; i++) {
            if (galleryDownload.getCover() != null) {
                continue;
            }
            try {
                InputStream coverStream = httpClientService.doGetStream(coverUrl, new HashMap<>());
                writeStreamAfterCheck(galleryDownload, coverStream, new File(cover));
                galleryDownload.setCover(cover);
                saveAfterCheck(galleryDownload);
                break;
            } catch (IOException ignored) {
                try {
                    TimeUnit.SECONDS.sleep(GalleryConstants.RETRY_AFTER_SECOND);
                } catch (InterruptedException ignored1) {
                }
            }
        }

        String[] imagePages = new String[galleryDownload.getImages().length];
        int thumbPages = detail.getThumbPages();
        for (int i = 0; i < thumbPages; i++) {
            for (int j = 0; j < GalleryConstants.DEFAULT_RETRY; j++) {
                checkAlive(galleryDownload);
                try {
                    Map<String, String> params = new HashMap<>();
                    params.put("p", String.valueOf(i));
                    params.put("inline_set", "ts_m");
                    String detailHtml = httpClientService.doGet(detailUrl, params);
                    List<String> imagePagesOfPage = GalleryDetailParser.parseImagePages(detailHtml).getElements();
                    for (int k = 0; k < imagePagesOfPage.size(); k++) {
                        imagePages[k + i * 40] = imagePagesOfPage.get(k);
                    }
                    break;
                } catch (IOException ignored) {
                    try {
                        TimeUnit.SECONDS.sleep(GalleryConstants.RETRY_AFTER_SECOND);
                    } catch (InterruptedException ignored1) {
                    }
                }
            }
        }

        for (int i = 0; i < imagePages.length; i++) {
            if (galleryDownload.getImages()[i] != null) {
                continue;
            }
            String imagePage = imagePages[i];
            for (int j = 0; j < GalleryConstants.DEFAULT_RETRY; j++) {
                try {
                    if (imagePage == null) {
                        continue;
                    }
                    String imageUrl = galleryDetailService.getImageUrl(imagePage);
                    String image = String.format(
                            "%s/%d/%d.%s",
                            DOWNLOAD_DIR,
                            galleryDownload.getTimestamp(),
                            i,
                            FilenameUtils.getExtension(imageUrl)
                    );
                    InputStream imageStream = httpClientService.doGetStream(imageUrl, new HashMap<>());
                    writeStreamAfterCheck(galleryDownload, imageStream, new File(image));
                    galleryDownload.getImages()[i] = image;
                    saveAfterCheck(galleryDownload);
                    break;
                } catch (IOException ignored) {
                    try {
                        TimeUnit.SECONDS.sleep(GalleryConstants.RETRY_AFTER_SECOND);
                    } catch (InterruptedException ignored1) {
                    }
                }
            }
        }

        galleryDownload.setStatus(GalleryDownload.StatusEnum.FINISHED);
        if (galleryDownload.getCover() == null) {
            galleryDownload.setStatus(GalleryDownload.StatusEnum.FAILED);
        }
        String[] imgs = galleryDownload.getImages();
        for (String img : imgs) {
            if (img == null) {
                galleryDownload.setStatus(GalleryDownload.StatusEnum.FAILED);
                break;
            }
        }

        saveAfterCheck(galleryDownload);

        downloadingUrl = null;

        downloadNext();
    }

}
