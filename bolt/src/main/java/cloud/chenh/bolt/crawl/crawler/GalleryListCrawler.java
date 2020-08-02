package cloud.chenh.bolt.crawl.crawler;

import cloud.chenh.bolt.constant.GalleryConstants;
import cloud.chenh.bolt.crawl.parser.GalleryListParser;
import cloud.chenh.bolt.data.entity.Gallery;
import cloud.chenh.bolt.exception.BannedException;
import cloud.chenh.bolt.model.Page;
import cloud.chenh.bolt.model.SearchParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GalleryListCrawler {

    @Autowired
    private CrawlClient crawlClient;

    public Page<Gallery> crawl(SearchParams searchParams) throws IOException, BannedException {
        String url = crawlClient.isLogin() ? GalleryConstants.EXHENTAI_HOME_URL : GalleryConstants.EHENTAI_HOME_URL;
        String html = crawlClient.doGet(url, searchParams.toHttpParams());

        // if ip is banned
        if (html.contains(GalleryConstants.BANNED_FLAG)) {
            throw new BannedException("本IP已被封禁");
        }

        return GalleryListParser.parse(html);
    }

}
