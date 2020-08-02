package cloud.chenh.bolt.crawl.parser;

import cloud.chenh.bolt.model.Page;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class GalleryImagePageParser {

    public static Page<String> parse(String html) {
        Document document = Jsoup.parse(html);
        if (document.selectFirst(".ptt td") == null) {
            return new Page<>(0, 0, new ArrayList<>());
        }
        Elements thumbTds = document.select(".ptt td");
        int thumbPages = Integer.parseInt(thumbTds.get(thumbTds.size() - 2).selectFirst("a").text());
        int pageNumber = Integer.parseInt(document.selectFirst(".ptds a").text()) - 1;
        List<String> urls = document.select(".gdtm a").eachAttr("href");
        return new Page<>(pageNumber, thumbPages, urls);
    }

}
