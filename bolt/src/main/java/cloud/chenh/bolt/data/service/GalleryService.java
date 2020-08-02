package cloud.chenh.bolt.data.service;

import cloud.chenh.bolt.constant.GalleryConstants;
import cloud.chenh.bolt.constant.ProjectConstants;
import cloud.chenh.bolt.crawl.crawler.*;
import cloud.chenh.bolt.data.base.BaseService;
import cloud.chenh.bolt.data.entity.BlockTag;
import cloud.chenh.bolt.data.entity.Gallery;
import cloud.chenh.bolt.data.entity.Image;
import cloud.chenh.bolt.data.repository.GalleryRepository;
import cloud.chenh.bolt.exception.BannedException;
import cloud.chenh.bolt.model.Page;
import cloud.chenh.bolt.model.SearchParams;
import cloud.chenh.bolt.util.GalleryUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GalleryService extends BaseService<Gallery> {

    public static final String DOWNLOAD_DIR = ProjectConstants.JAR_DIR + File.separator + "download";
    public static final String TEMP_DIR = ProjectConstants.JAR_DIR + File.separator + "temp";

    private final ReentrantLock downloadLock = new ReentrantLock();

    @Autowired
    private GalleryListCrawler galleryListCrawler;

    @Autowired
    private GalleryDetailCrawler galleryDetailCrawler;

    @Autowired
    private GalleryThumbCrawler galleryThumbCrawler;

    @Autowired
    private GalleryImagePageCrawler galleryImagePageCrawler;

    @Autowired
    private GalleryImageUrlCrawler galleryImageUrlCrawler;

    @Autowired
    private ImageService imageService;

    @Autowired
    private BlockTagService blockTagService;

    @Autowired
    private TagTranslateService tagTranslateService;

    @Autowired
    private GalleryRepository galleryRepository;


    @Override
    public GalleryRepository getRepository() {
        return galleryRepository;
    }

    public Gallery findByUrl(String url) {
        return getRepository().findByUrl(url);
    }

    public Page<Gallery> fetchGalleryList(SearchParams searchParams) throws IOException, BannedException {
        Page<Gallery> page = galleryListCrawler.crawl(searchParams);

        List<BlockTag> blockedTags = blockTagService.findAll();
        page.getElements().removeIf(gallery -> {
            List<Gallery.TagGroup> tagGroups = gallery.getTagGroups();
            for (Gallery.TagGroup tagGroup : tagGroups) {
                for (String tag : tagGroup.getTags()) {
                    for (BlockTag blockTag : blockedTags) {
                        if (blockTag.getGroup().equals(tagGroup.getGroup()) && blockTag.getTag().equals(tag)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        });

        List<Gallery> galleries = get();
        page.getElements().parallelStream().forEach(gallery -> {
            if (galleries.parallelStream().anyMatch(g -> g.getUrl().equals(gallery.getUrl()))) {
                gallery.setIsDownload(true);
            }
        });

        return page;
    }

    public Gallery fetchGalleryDetail(String url) throws IOException {
        Gallery gallery = galleryDetailCrawler.crawl(url);
        List<Gallery> galleries = get();
        if (galleries.parallelStream().anyMatch(g -> g.getUrl().equals(gallery.getUrl()))) {
            gallery.setIsDownload(true);
        }

        // translate
        List<Gallery.TagGroup> tagGroups = gallery.getTagGroups();
        tagGroups.parallelStream().forEach(tagGroup -> {
            String group = tagGroup.getGroup();
            tagGroup.setTranslatedGroup(tagTranslateService.translateGroup(group));

            List<String> tags = tagGroup.getTags();
            List<String> translatedTags = tags.stream()
                    .map(t -> tagTranslateService.translateTag(group, t)).collect(Collectors.toList());
            tagGroup.setTranslatedTags(translatedTags);
        });

        return gallery;
    }

    public Page<String> fetchThumbs(String url, Integer pageNumber) throws IOException {
        return galleryThumbCrawler.crawl(url, pageNumber);
    }

    public Page<String> fetchImagePages(String url, Integer pageNumber) throws IOException {
        return galleryImagePageCrawler.crawl(url, pageNumber);
    }

    public String fetchImageUrl(String url) throws IOException {
        return galleryImageUrlCrawler.crawl(url);
    }

    public List<Gallery> getAllReversed() {
        return get().stream().sorted((p, n) -> n.getId() - p.getId()).collect(Collectors.toList());
    }

    public void addToDownload(String url) {
        Gallery gallery = new Gallery();
        gallery.setUrl(url);
        gallery.setStatus(Gallery.Status.WAITING);
        save(gallery);

        log.info("已加入下载队列：" + url);

        new Thread(this::downloadNext).start();
    }

    @PostConstruct
    public void downloadNext() {
        cleanCache();
        if (!downloadLock.isLocked()) {
            get().stream()
                    .filter(g -> g.getStatus() == Gallery.Status.WAITING)
                    .findFirst()
                    .ifPresent(gallery -> download(gallery.getUrl()));
        }
    }

    public void download(String url) {
        try {
            downloadLock.lock();

            log.info("开始下载：" + url);

            Gallery gallery = findByUrl(url);
            if (gallery == null) {
                gallery = new Gallery();
                gallery.setUrl(url);
                gallery.setStatus(Gallery.Status.WAITING);
                gallery = save(gallery);
            }

            // if the first step is failed
            try {
                gallery = fetchGalleryDetail(url);
                gallery.setStatus(Gallery.Status.DOWNLOADING);
                gallery = save(gallery);
            } catch (Exception e) {
                gallery.setStatus(Gallery.Status.FAILED);
                save(gallery);

                log.info("下载失败（无法读取详情页）：" + url);
                return;
            }

            try {
                // download the cover
                String coverUrl = gallery.getCoverUrl();
                String coverPath = DOWNLOAD_DIR + File.separator +
                        GalleryUtils.getGid(url) + File.separator +
                        "cover." + FilenameUtils.getExtension(coverUrl);
                if (!downloadImage(coverUrl, coverPath)) {
                    gallery.setStatus(Gallery.Status.FAILED);
                    save(gallery);

                    log.info("下载失败（无法下载封面）：" + url);
                    return;
                }

                // multi threads
                ExecutorService downloadPool = Executors.newFixedThreadPool(GalleryConstants.IMAGE_DOWNLOAD_THREAD);
                CountDownLatch downloadLatch = new CountDownLatch(gallery.getPages());

                // fetch the image page urls, not the image url
                Page<String> imagePages = fetchImagePages(url, 0);
                // the download index, keep the sort
                int downloadIndex = 0;
                for (int i = 0; i < imagePages.getTotalPages(); i++) {
                    if (i > 0) {
                        imagePages = fetchImagePages(url, i);
                    }
                    List<String> elements = imagePages.getElements();
                    for (String page : elements) {
                        // fetch the image url
                        String imageUrl = fetchImageUrl(page);
                        String imagePath = DOWNLOAD_DIR + File.separator +
                                GalleryUtils.getGid(url) + File.separator +
                                downloadIndex + "." + FilenameUtils.getExtension(imageUrl);
                        int finalDownloadIndex = downloadIndex;

                        // add to pool
                        downloadPool.submit(() -> {
                            try {
                                // if continue?
                                if (!isActive(url)) {
                                    return;
                                }
                                if (downloadImage(imageUrl, imagePath)) {
                                    Gallery temp = findByUrl(url);
                                    if (temp != null) {
                                        temp.getImageUrls()[finalDownloadIndex] = url;
                                        save(temp);
                                    }
                                }
                            } finally {
                                downloadLatch.countDown();
                            }
                        });
                        downloadIndex++;
                    }
                }

                // wait
                try {
                    downloadLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // if continue?
                if (!isActive(url)) {
                    return;
                }

                // is succeed?
                boolean succeed = true;
                gallery = findByUrl(url);
                for (String imageUrl : gallery.getImageUrls()) {
                    if (imageUrl == null) {
                        succeed = false;
                        break;
                    }
                }

                // ok
                gallery.setStatus(succeed ? Gallery.Status.DOWNLOADED : Gallery.Status.FAILED);
                save(gallery);

                log.info((succeed ? "下载成功：" : "下载失败（部分图片下载失败）：") + url);
            } catch (Exception e) {
                gallery.setStatus(Gallery.Status.FAILED);
                save(gallery);

                log.info("下载失败（" + e.getMessage() + "）：" + url);
            }
        } finally {
            downloadLock.unlock();
            downloadNext();
        }
    }

    private Boolean isActive(String url) {
        Gallery gallery = findByUrl(url);
        return gallery != null && gallery.getStatus() == Gallery.Status.DOWNLOADING;
    }

    private Boolean downloadImage(String url, String path) {
        // if it has been downloaded, return
        Image existImage = imageService.findByUrl(url);
        if (existImage != null && new File(existImage.getPath()).exists()) {
            return true;
        }

        // try more times
        for (int i = 0; i < GalleryConstants.DEFAULT_RETRY; i++) {
            try {
                FileUtils.copyInputStreamToFile(imageService.fetchImage(url), new File(path));

                // save image information
                Image image = new Image();
                image.setUrl(url);
                image.setPath(path);
                imageService.save(image);

                return true;
            } catch (IOException e) {
                // do not hurry
                try {
                    TimeUnit.SECONDS.sleep(GalleryConstants.RETRY_AFTER_SECOND);
                } catch (InterruptedException ignored) {
                }
                e.printStackTrace();
            }
        }
        return false;
    }

    public void pause(String url) {
        Gallery gallery = findByUrl(url);
        if (gallery == null) {
            return;
        }
        gallery.setStatus(Gallery.Status.PAUSED);
        save(gallery);
    }

    public void delete(String url) {
        Gallery gallery = findByUrl(url);
        if (gallery == null) {
            return;
        }
        remove(gallery.getId());
        imageService.removeByUrl(gallery.getCoverUrl());
        imageService.removeByUrl(Arrays.asList(gallery.getImageUrls()));
    }

    public BufferedImage fetchCover(String galleryUrl, String coverUrl) {
        try {
            String coverPath = DOWNLOAD_DIR + File.separator +
                    GalleryUtils.getGid(galleryUrl) + File.separator +
                    "cover." + FilenameUtils.getExtension(coverUrl);
            if (imageService.findByUrl(coverUrl) != null && new File(coverPath).exists()) {
                return ImageIO.read(new File(coverPath));
            }
            if (downloadImage(coverUrl, coverPath)) {
                return ImageIO.read(new File(coverPath));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public BufferedImage fetchThumb(String galleryUrl, String thumbUrl) {
        try {
            String coverPath = TEMP_DIR + File.separator +
                    GalleryUtils.getGid(galleryUrl) + File.separator +
                    FilenameUtils.getName(thumbUrl);
            if (imageService.findByUrl(thumbUrl) != null && new File(coverPath).exists()) {
                return ImageIO.read(new File(coverPath));
            }
            if (downloadImage(thumbUrl, coverPath)) {
                return ImageIO.read(new File(coverPath));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public BufferedImage fetchImage(String galleryUrl, Integer index, String imagePage) {
        try {
            Gallery gallery = findByUrl(galleryUrl);
            String imageUrl = gallery.getImageUrls()[index];
            if (imageUrl != null) {
                Image image = imageService.findByUrl(imageUrl);
                if (image != null && new File(image.getPath()).exists()) {
                    return ImageIO.read(new File(image.getPath()));
                }
            }
            imageUrl = fetchImageUrl(imagePage);
            String imagePath = DOWNLOAD_DIR + File.separator +
                    GalleryUtils.getGid(galleryUrl) + File.separator +
                    index + "." + FilenameUtils.getExtension(imageUrl);
            if (downloadImage(imageUrl, imagePath)) {
                return ImageIO.read(new File(imagePath));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void cleanCache() {
        List<Gallery> galleries = get();

        // clean image
        List<String> images = imageService.get().parallelStream().map(Image::getUrl).collect(Collectors.toList());
        for (Gallery gallery : galleries) {
            images.removeIf(image -> image.equals(gallery.getCoverUrl()));
            for (String imageUrl : gallery.getImageUrls()) {
                images.removeIf(image -> image.equals(imageUrl));
            }
        }
        imageService.removeByUrl(images);

        // clean empty dir
        File downloadDir = new File(DOWNLOAD_DIR);
        File[] files = downloadDir.listFiles();
        if (files != null) {
            for (File file : files) {
                File[] innerFiles = file.listFiles();
                if (innerFiles == null || innerFiles.length == 0) {
                    FileUtils.deleteQuietly(file);
                }
            }
        }

        // clean temp dir
        File tempDir = new File(TEMP_DIR);
        if (tempDir.exists()) {
            FileUtils.deleteQuietly(tempDir);
        }

        log.info("已清理缓存");
    }

}
