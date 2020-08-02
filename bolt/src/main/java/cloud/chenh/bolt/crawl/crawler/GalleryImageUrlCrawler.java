package cloud.chenh.bolt.crawl.crawler;

import cloud.chenh.bolt.crawl.parser.GalleryImageUrlParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;

@Component
public class GalleryImageUrlCrawler {

    @Autowired
    private CrawlClient crawlClient;

    public String crawl(String url) throws IOException {
        String html = crawlClient.doGet(url, new HashMap<>());
        return GalleryImageUrlParser.parse(html);
    }

}
