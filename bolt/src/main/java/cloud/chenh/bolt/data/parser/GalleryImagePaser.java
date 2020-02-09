package cloud.chenh.bolt.data.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class GalleryImagePaser {

    public static String parse(String html) {
        Document document = Jsoup.parse(html);
        return document.selectFirst("#img").attr("src");
    }

}
