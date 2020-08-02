package cloud.chenh.bolt.crawl.parser;

import cloud.chenh.bolt.data.entity.Gallery;
import cloud.chenh.bolt.model.Page;
import cloud.chenh.bolt.util.GalleryUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

public class GalleryListParser {

    public static Page<Gallery> parse(String html) {
        Document document = Jsoup.parse(html);

        if (document.selectFirst(".ptt") == null || document.selectFirst(".ptds") == null) {
            return new Page<>(0, 0, new ArrayList<>());
        }

        Elements tds = document.select(".ptt td");
        int totalPages = Integer.parseInt(tds.get(tds.size() - 2).selectFirst("a").text());
        int pageNumber = Integer.parseInt(document.selectFirst(".ptds a").text().split("-")[0]) - 1;

        Elements trs = document.selectFirst(".itg").child(0).children();
        trs.removeIf(tr -> tr.selectFirst(".gl1e") == null);
        List<Gallery> overviews = new ArrayList<>();
        for (Element tr : trs) {
            overviews.add(parseSingle(tr));
        }

        return new Page<>(pageNumber, totalPages, overviews);
    }

    private static Gallery parseSingle(Element root) {
        String url = root.selectFirst(".gl1e a").attr("href");
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

        Gallery gallery = new Gallery();
        gallery.setCategory(category);
        gallery.setCoverUrl(coverUrl);
        gallery.setTime(time);
        gallery.setTitle(title);
        gallery.setUploader(uploader);
        gallery.setPages(Integer.parseInt(pages));
        gallery.setUrl(url);
        double rating = GalleryUtils.calcRating(ratingStyle);
        gallery.setRating(rating);
        gallery.setTagGroups(parseTagGroup(tags));

        return gallery;
    }

    private static List<Gallery.TagGroup> parseTagGroup(List<String> tagStrs) {
        Map<String, List<String>> tagMap = new LinkedHashMap<>();

        for (String tagStr : tagStrs) {
            if (!tagStr.contains(":")) {
                continue;
            }
            String[] parts = tagStr.split(":");
            if (parts.length != 2) {
                continue;
            }
            String group = parts[0].trim();
            String tag = parts[1].trim();
            List<String> tags = tagMap.computeIfAbsent(group, k -> new ArrayList<>());
            tags.add(tag);
        }

        List<Gallery.TagGroup> groups = new ArrayList<>();
        tagMap.forEach((group, tags) -> {
            Gallery.TagGroup tagGroup = new Gallery.TagGroup();
            tagGroup.setGroup(group);
            tagGroup.setTags(tags);
            groups.add(tagGroup);
        });

        return groups;
    }
}
