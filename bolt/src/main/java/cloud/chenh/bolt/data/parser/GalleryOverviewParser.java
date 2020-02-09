package cloud.chenh.bolt.data.parser;

import cloud.chenh.bolt.data.model.GalleryOverview;
import cloud.chenh.bolt.data.model.SimplePage;
import cloud.chenh.bolt.util.GalleryUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class GalleryOverviewParser {

    public static SimplePage<GalleryOverview> parse(String html) {
        Document document = Jsoup.parse(html);

        if (document.selectFirst(".ptt") == null || document.selectFirst(".ptds") == null) {
            return new SimplePage<>(0, 0, new ArrayList<>());
        }

        Elements tds = document.select(".ptt td");
        int totalPages = Integer.parseInt(tds.get(tds.size() - 2).selectFirst("a").text());
        int pageNumber = Integer.parseInt(document.selectFirst(".ptds a").text()) - 1;

        Elements trs = document.selectFirst(".itg").child(0).children();
        trs.removeIf(tr -> tr.selectFirst(".gl1e") == null);
        List<GalleryOverview> overviews = new ArrayList<>();
        for (Element tr : trs) {
            overviews.add(parseOverview(tr));
        }

        return new SimplePage<>(pageNumber, totalPages, overviews);
    }

    private static GalleryOverview parseOverview(Element root) {
        String detailUrl = root.selectFirst(".gl1e a").attr("href");
        String coverUrl = root.selectFirst(".gl1e img").attr("src");
        String category = root.selectFirst(".gl3e").child(0).text();
        String time = root.selectFirst(".gl3e").child(1).text();
        String ratingStyle = root.selectFirst(".gl3e").child(2).attr("style");
        String uploader = root.selectFirst(".gl3e").child(3).selectFirst("a").text();
        String pages = root
                .selectFirst(".gl3e")
                .child(4)
                .text()
                .replace("pages", "")
                .replace("page", "")
                .trim();
        String title = root.selectFirst(".glink").text();

        List<String> tags = root.select(".gt, gtl").eachAttr("title");

        GalleryOverview overview = new GalleryOverview();
        overview.setCategory(category);
        overview.setCoverUrl(coverUrl);
        overview.setTime(time);
        overview.setTitle(title);
        overview.setUploader(uploader);
        overview.setPages(Integer.parseInt(pages));
        overview.setDetailUrl(detailUrl);
        double rating = GalleryUtils.calcRating(ratingStyle);
        overview.setRating((int) rating + (rating % 1 > 0 ? "+" : ""));
        overview.setTags(tags);

        return overview;
    }

}
