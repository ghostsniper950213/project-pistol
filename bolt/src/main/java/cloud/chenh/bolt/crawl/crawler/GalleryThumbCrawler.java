package cloud.chenh.bolt.crawl.crawler;

import cloud.chenh.bolt.crawl.parser.GalleryThumbParser;
import cloud.chenh.bolt.model.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class GalleryThumbCrawler {

    @Autowired
    private CrawlClient crawlClient;

    public Page<String> crawl(String url, Integer pageNumber) throws IOException {
        boolean isLogin = crawlClient.isLogin();

        Map<String, String> params = new HashMap<>();
        params.put("p", String.valueOf(pageNumber));

        // better thumb quality
        params.put("inline_set", crawlClient.isLogin() ? "ts_l" : "ts_m");

        String html = crawlClient.doGet(url, params);
        return isLogin ? GalleryThumbParser.parseLarge(html) : GalleryThumbParser.parseMedium(html);
    }

}
