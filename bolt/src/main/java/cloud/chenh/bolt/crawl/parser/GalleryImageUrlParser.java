package cloud.chenh.bolt.crawl.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class GalleryImageUrlParser {

    public static String parse(String html) {
        Document document = Jsoup.parse(html);
        return document.selectFirst("#img").attr("src");
    }

}
