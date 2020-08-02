package cloud.chenh.bolt.crawl.crawler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class GalleryImageCrawler {

    @Autowired
    private CrawlClient crawlClient;

    public InputStream crawl(String url) throws IOException {
        return crawlClient.doGetImage(url);
    }


}
