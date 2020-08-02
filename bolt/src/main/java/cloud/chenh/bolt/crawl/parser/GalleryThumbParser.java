package cloud.chenh.bolt.crawl.parser;

import cloud.chenh.bolt.model.Page;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.stream.Collectors;

public class GalleryThumbParser {

    public static Page<String> parseMedium(String html) {
        Document document = Jsoup.parse(html);
        List<String> elements = document
                .select(".gdtm div")
                .eachAttr("style")
                .stream()
                .map(style -> style.substring(style.indexOf("url(") + 4, style.indexOf(")")))
                .distinct()
                .collect(Collectors.toList());
        int pageNumber = Integer.parseInt(document.selectFirst(".ptds a").text()) - 1;
        Elements tds = document.selectFirst(".ptt tr").select("td");
        int totalPages = Integer.parseInt(tds.get(tds.size() - 2).text());
        return new Page<>(pageNumber, totalPages, elements);
    }

    public static Page<String> parseLarge(String html) {
        Document document = Jsoup.parse(html);
        List<String> elements = document.select(".gdtl img").eachAttr("src");
        int pageNumber = Integer.parseInt(document.selectFirst(".ptds a").text()) - 1;
        Elements tds = document.selectFirst(".ptt tr").select("td");
        int totalPages = Integer.parseInt(tds.get(tds.size() - 2).text());
        return new Page<>(pageNumber, totalPages, elements);
    }

}
