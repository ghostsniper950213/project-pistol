package cloud.chenh.bolt.crawl.parser;

import cloud.chenh.bolt.data.entity.Gallery;
import cloud.chenh.bolt.util.GalleryUtils;
import cloud.chenh.bolt.util.TimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GalleryDetailParser {

    public static Gallery parse(String html) {
        Document document = Jsoup.parse(html);

        String title = document.selectFirst("#gn").text();
        String coverUrl = GalleryUtils.getUrlFromStyle(document.selectFirst("#gd1 div").attr("style"));
        String category = document.selectFirst("#gdc div").text();
        String uploader = document.selectFirst("#gdn a").text();
        String time = document.select(".gdt2").get(0).text();
        String language = document.select(".gdt2").get(3).text();
        if (StringUtils.isNoneBlank(language)) {
            if (language.contains("\"")) {
                language = language.substring(language.indexOf("\""), language.indexOf(" "));
            } else if (language.contains(" ")) {
                language = language.substring(0, language.indexOf(" "));
            }
        }
        int pages = Integer.parseInt(
                document
                        .select(".gdt2")
                        .get(5)
                        .text()
                        .replace("pages", "")
                        .replace("page", "")
                        .trim()
        );
        String favorited = document.select(".gdt2").get(6).text().trim();
        int favorites = Integer.parseInt(
                favorited
                        .replace("None", "0")
                        .replace("Once", "1")
                        .replace("times", "")
                        .trim()
        );
        double rating = Double.parseDouble(
                document
                        .selectFirst("#rating_label")
                        .text()
                        .replace("Not Yet Rated", "0")
                        .replace("Average:", "")
                        .trim()
        );

        Elements tagTrs = document.select("#taglist tr");

        List<Gallery.TagGroup> tagGroups = new ArrayList<>();
        for (Element tagTr : tagTrs) {
            String group = tagTr.selectFirst(".tc").text().replace(":", "").trim();
            List<String> tags = new ArrayList<>(tagTr.select("a").eachText());
            tags = tags.stream().map(tagVal -> tagVal.split("\\|")[0].trim()).distinct().collect(Collectors.toList());

            Gallery.TagGroup tagGroup = new Gallery.TagGroup();
            tagGroup.setGroup(group);
            tagGroup.setTags(tags);
            tagGroups.add(tagGroup);
        }

        Elements c1s = document.select(".c1");
        List<Gallery.Comment> comments = new ArrayList<>();
        for (Element c1 : c1s) {
            String postTimeInfo = c1.selectFirst(".c1").text().trim();
            postTimeInfo = postTimeInfo
                    .substring(postTimeInfo.indexOf("Posted on") + 9, postTimeInfo.indexOf("UTC by:"))
                    .trim();
            String[] postTimeParts = postTimeInfo.split(",");
            String[] dateParts = postTimeParts[0].trim().split(" ");
            int date = Integer.parseInt(dateParts[0].trim());
            int month = TimeUtils.mounthToInt(dateParts[1].trim());
            int year = Integer.parseInt(dateParts[2].trim());

            String postTime = String.format("%04d-%02d-%02d %s", year, month, date, postTimeParts[1].trim());

            Element c5 = c1.selectFirst(".c5");
            String vote = c5 != null ? c5.selectFirst("span").text() : "";
            String content = c1.selectFirst(".c6").html();
            String author = c1.selectFirst(".c3 a").text();
            boolean isUploader = c1.selectFirst("*[name=ulcomment]") != null;

            Gallery.Comment comment = new Gallery.Comment();
            comment.setTime(postTime);
            comment.setVote(vote);
            comment.setContent(content);
            comment.setAuthor(author);
            comment.setIsUploader(isUploader);
            comments.add(comment);
        }

        Gallery gallery = new Gallery();
        gallery.setTitle(title);
        gallery.setCoverUrl(coverUrl);
        gallery.setCategory(category);
        gallery.setUploader(uploader);
        gallery.setTime(time);
        gallery.setLanguage(language);
        gallery.setPages(pages);
        gallery.setFavorites(favorites);
        gallery.setRating(rating);
        gallery.setTagGroups(tagGroups);
        gallery.setComments(comments);

        return gallery;
    }

}
