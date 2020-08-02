package cloud.chenh.bolt.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GalleryUtils {

    public static Double calcRating(String ratingStyle) {
        // background-position:0px -21px;opacity:1
        // f = 5 + x / 16 + (y + 1) / 40.0
        ratingStyle = ratingStyle.substring(ratingStyle.indexOf(":") + 1, ratingStyle.indexOf(";"));
        int x = Integer.parseInt(ratingStyle.substring(0, ratingStyle.indexOf("px")));
        int y = Integer.parseInt(ratingStyle.substring(ratingStyle.indexOf(" ") + 1, ratingStyle.lastIndexOf("px")));
        return 5 + x / 16.0 + (y + 1) / 40.0;
    }

    public static String getUrlFromStyle(String style) {
        style = style.substring(style.indexOf("url(") + 4);
        return style.substring(0, style.indexOf(")"));
    }

    public static String getGid(String url) {
        // https://exhentai.org/g/1559821/afa245de62/
        List<String> parts = Arrays
                .stream(url.split("/"))
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
        return parts.get(parts.size() - 2) + "_" + parts.get(parts.size() - 1);
    }

}
