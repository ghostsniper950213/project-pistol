package cloud.chenh.bolt.crawl.crawler;

import cloud.chenh.bolt.crawl.parser.GalleryDetailParser;
import cloud.chenh.bolt.data.entity.Gallery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;

@Component
public class GalleryDetailCrawler {

    @Autowired
    private CrawlClient crawlClient;

    public Gallery crawl(String url) throws IOException {
        // inline_set for keeping same format
        String html = crawlClient.doGet(url, Collections.singletonMap("inline_set", "ts_m"));

        Gallery gallery = GalleryDetailParser.parse(html);

        // pre set
        gallery.setUrl(url);
        gallery.setImageUrls(new String[gallery.getPages()]);

        return gallery;
    }

}
