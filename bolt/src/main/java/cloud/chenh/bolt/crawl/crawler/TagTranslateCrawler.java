package cloud.chenh.bolt.crawl.crawler;

import cloud.chenh.bolt.constant.GalleryConstants;
import cloud.chenh.bolt.crawl.parser.TagTranslateParser;
import cloud.chenh.bolt.data.entity.TagTranslate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@Component
public class TagTranslateCrawler {

    @Autowired
    private CrawlClient crawlClient;

    public List<TagTranslate> crawl() throws IOException {
        String response = crawlClient.doGet(GalleryConstants.TAG_TRANSLATE_URL, new HashMap<>());
        return TagTranslateParser.parse(response);
    }

}
