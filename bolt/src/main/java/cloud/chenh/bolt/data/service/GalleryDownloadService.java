package cloud.chenh.bolt.data.service;

import cloud.chenh.bolt.constant.GalleryConstants;
import cloud.chenh.bolt.data.dao.GalleryDownloadDao;
import cloud.chenh.bolt.data.model.GalleryDetail;
import cloud.chenh.bolt.data.model.GalleryDownload;
import cloud.chenh.bolt.data.parser.GalleryDetailParser;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
public class GalleryDownloadService {

    private static final String DOWNLOAD_DIR = "./download/";

    private Thread downloadThread;

    private AtomicBoolean downloading = new AtomicBoolean(false);

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

    public GalleryDownload get(String detailUrl) {
        return galleryDownloadDao.get(detailUrl);
    }

    public void add(GalleryDownload galleryDownload) {
        galleryDownload.getDetail().setDownload(true);
        galleryDownloadDao.save(galleryDownload);
        downloadNext();
    }

    public void save(GalleryDownload galleryDownload) {
        galleryDownloadDao.save(galleryDownload);
    }

    public void remove(List<String> detailUrls) {
        if (downloadThread != null && downloadThread.isAlive() && detailUrls.contains(downloadThread.getName())) {
            downloadThread.interrupt();
        }
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
        galleryDownloadDao.save(galleryDownloads);
        downloadNext();
    }

    public void pause(List<String> detailUrls) {
        if (downloadThread != null && downloadThread.isAlive() && detailUrls.contains(downloadThread.getName())) {
            downloadThread.interrupt();
        }
        List<GalleryDownload> galleryDownloads = galleryDownloadDao.get(detailUrls);
        galleryDownloads.forEach(galleryDownload -> galleryDownload.setStatus(GalleryDownload.StatusEnum.PAUSED));
        galleryDownloadDao.save(galleryDownloads);
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
        if (downloading.compareAndSet(false, true)) {
            GalleryDownload galleryDownload = getDownloadable();
            if (galleryDownload != null) {
                download(galleryDownload);
            } else {
                downloading.set(false);
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

    private void download(GalleryDownload galleryDownload) {
        if (downloadThread != null && downloadThread.isAlive()) {
            downloadThread.interrupt();
        }

        downloadThread = new Thread(() -> {
            GalleryDetail detail = galleryDownload.getDetail();

            String detailUrl = detail.getDetailUrl();
            String coverUrl = detail.getCoverUrl();

            String cover = DOWNLOAD_DIR + "/" + galleryDownload.getTimestamp() + "/cover.jpg";
            for (int i = 0; i < GalleryConstants.DEFAULT_RETRY; i++) {
                if (galleryDownload.getCover() != null) {
                    continue;
                }
                try {
                    InputStream coverStream = httpClientService.doGetStream(coverUrl, new HashMap<>());
                    FileUtils.copyInputStreamToFile(coverStream, new File(cover));
                    galleryDownload.setCover(cover);
                    galleryDownloadDao.save(galleryDownload);
                    break;
                } catch (IOException ignored) {
                    try {
                        TimeUnit.SECONDS.sleep(10);
                    } catch (InterruptedException ignored1) {
                    }
                }
            }

            String[] imagePages = new String[galleryDownload.getImages().length];
            int thumbPages = detail.getThumbPages();
            for (int i = 0; i < thumbPages; i++) {
                for (int j = 0; j < GalleryConstants.DEFAULT_RETRY; j++) {
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
                            TimeUnit.SECONDS.sleep(10);
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
                        String image = DOWNLOAD_DIR + "/" + galleryDownload.getTimestamp() + "/" + i + ".jpg";
                        String imageUrl = galleryDetailService.getImageUrl(imagePage);
                        InputStream imageStream = httpClientService.doGetStream(imageUrl, new HashMap<>());
                        FileUtils.copyInputStreamToFile(imageStream, new File(image));
                        galleryDownload.getImages()[i] = image;
                        galleryDownloadDao.save(galleryDownload);
                        break;
                    } catch (IOException ignored) {
                        try {
                            TimeUnit.SECONDS.sleep(10);
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

            galleryDownloadDao.save(galleryDownload);

            downloading.set(false);

            downloadNext();
        });
        downloadThread.setName(galleryDownload.getDetail().getDetailUrl());
        downloadThread.start();
    }

}
