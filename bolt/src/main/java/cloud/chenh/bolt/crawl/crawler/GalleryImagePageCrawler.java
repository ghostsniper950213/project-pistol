package cloud.chenh.bolt.crawl.crawler;

import cloud.chenh.bolt.crawl.parser.GalleryImagePageParser;
import cloud.chenh.bolt.model.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class GalleryImagePageCrawler {

    @Autowired
    private CrawlClient crawlClient;

    public Page<String> crawl(String url, Integer pageNumber) throws IOException {
        // same as the thumb crawler
        Map<String, String> params = new HashMap<>();
        params.put("p", String.valueOf(pageNumber));
        params.put("inline_set", "ts_m");
        String html = crawlClient.doGet(url, params);
        return GalleryImagePageParser.parse(html);
    }

}
