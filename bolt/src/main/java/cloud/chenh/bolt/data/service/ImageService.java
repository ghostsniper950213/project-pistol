package cloud.chenh.bolt.data.service;

import cloud.chenh.bolt.crawl.crawler.GalleryImageCrawler;
import cloud.chenh.bolt.data.base.BaseService;
import cloud.chenh.bolt.data.entity.Image;
import cloud.chenh.bolt.data.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class ImageService extends BaseService<Image> {

    @Autowired
    private GalleryImageCrawler galleryImageCrawler;

    @Autowired
    private ImageRepository imageRepository;

    @Override
    public ImageRepository getRepository() {
        return imageRepository;
    }

    public Image findByUrl(String url) {
        return getRepository().findByUrl(url);
    }

    public Image removeByUrl(String url) {
        return getRepository().removeByUrl(url);
    }

    public List<Image> removeByUrl(List<String> urls) {
        return getRepository().removeByUrl(urls);
    }

    public InputStream fetchImage(String url) throws IOException {
        return galleryImageCrawler.crawl(url);
    }

}
