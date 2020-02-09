package cloud.chenh.bolt.data.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.List;
import java.util.stream.Collectors;

public class GalleryThumbParser {

    public static List<String> parse(String html) {
        Document document = Jsoup.parse(html);
        return document
                .select(".gdtm div")
                .eachAttr("style")
                .stream()
                .map(style -> style.substring(style.indexOf("url(") + 4, style.indexOf(")")))
                .distinct()
                .collect(Collectors.toList());
    }

    public static List<String> parseLarge(String html) {
        Document document = Jsoup.parse(html);
        return document.select(".gdtl img").eachAttr("src");
    }

}
