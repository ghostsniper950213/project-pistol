package cloud.chenh.bolt.data.service;

import cloud.chenh.bolt.constant.GalleryConstants;
import cloud.chenh.bolt.data.model.GalleryOverview;
import cloud.chenh.bolt.data.model.SimplePage;
import cloud.chenh.bolt.data.parser.GalleryOverviewParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class GalleryOverviewService {

    @Autowired
    private HttpClientService httpClientService;

    @Autowired
    private GalleryTagService galleryTagService;

    @Autowired
    private GalleryDownloadService galleryDownloadService;

    public SimplePage<GalleryOverview> getPage(Map<String, String> pageParams) throws IOException {
        Map<String, String> cookies = httpClientService.getCookies();
        String url = cookies.containsKey(GalleryConstants.IMPORTANT_COOKIE)
                ? GalleryConstants.EXHENTAI_HOME_URL : GalleryConstants.EHENTAI_HOME_URL;

        pageParams.put("inline_set", "dm_e");
        String html = httpClientService.doGet(url, pageParams);

        SimplePage<GalleryOverview> page = GalleryOverviewParser.parse(html);
        List<String> blockedTags = galleryTagService.getBlockedTags();
        page.getElements().removeIf(gallery -> {
            List<String> tags = gallery.getTags();
            for (String tag : tags) {
                if (blockedTags.contains(tag)) {
                    return true;
                }
            }
            return false;
        });

        List<String> downloading = galleryDownloadService.keys();
        page.getElements().forEach(gallery -> {
            String detailUrl = gallery.getDetailUrl();
            if (downloading.contains(detailUrl)) {
                gallery.setDownload(true);
            }
        });

        return page;
    }

}
