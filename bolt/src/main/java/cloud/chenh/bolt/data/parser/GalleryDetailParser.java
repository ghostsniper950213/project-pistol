package cloud.chenh.bolt.data.parser;

import cloud.chenh.bolt.data.model.GalleryComment;
import cloud.chenh.bolt.data.model.GalleryDetail;
import cloud.chenh.bolt.data.model.GalleryTag;
import cloud.chenh.bolt.data.model.SimplePage;
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

    public static GalleryDetail parse(String html) {
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

        List<GalleryTag> tags = new ArrayList<>();
        for (Element tagTr : tagTrs) {
            String tagName = tagTr.selectFirst(".tc").text().replace(":", "").trim();
            List<String> tagVals = tagTr.select("a").eachText();
            tagVals = tagVals.stream().map(tagVal -> tagVal.split("\\|")[0].trim()).collect(Collectors.toList());

            GalleryTag tag = new GalleryTag();
            tag.setName(tagName);
            tag.setVals(tagVals);
            tags.add(tag);
        }

        Elements thumbTds = document.select(".ptt td");
        int thumbPages = Integer.parseInt(thumbTds.get(thumbTds.size() - 2).selectFirst("a").text());

        Elements c1s = document.select(".c1");
        List<GalleryComment> comments = new ArrayList<>();
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

            GalleryComment comment = new GalleryComment();
            comment.setPostTime(postTime);
            comment.setVote(vote);
            comment.setContent(content);
            comment.setAuthor(author);
            comment.setIsUploader(isUploader);
            comments.add(comment);
        }

        GalleryDetail detail = new GalleryDetail();
        detail.setTitle(title);
        detail.setCoverUrl(coverUrl);
        detail.setCategory(category);
        detail.setUploader(uploader);
        detail.setTime(time);
        detail.setLanguage(language);
        detail.setPages(pages);
        detail.setFavorites(favorites);
        detail.setRating(rating);
        detail.setTags(tags);
        detail.setThumbPages(thumbPages);
        detail.setComments(comments);

        return detail;
    }

    public static SimplePage<String> parseImagePages(String html) {
        Document document = Jsoup.parse(html);
        Elements thumbTds = document.select(".ptt td");
        int thumbPages = Integer.parseInt(thumbTds.get(thumbTds.size() - 2).selectFirst("a").text());
        int pageNumber = Integer.parseInt(document.selectFirst(".ptds a").text()) - 1;
        int pages = Integer.parseInt(
                document
                        .select(".gdt2")
                        .get(5)
                        .text()
                        .replace("pages", "")
                        .replace("page", "")
                        .trim());
        List<String> urls = document.select(".gdtm a").eachAttr("href");
        return new SimplePage<>(pageNumber, thumbPages, pages, urls);
    }

}
