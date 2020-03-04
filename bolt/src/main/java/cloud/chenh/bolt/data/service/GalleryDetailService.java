package cloud.chenh.bolt.data.service;

import cloud.chenh.bolt.constant.GalleryConstants;
import cloud.chenh.bolt.data.model.GalleryDetail;
import cloud.chenh.bolt.data.model.GalleryDownload;
import cloud.chenh.bolt.data.model.SimplePage;
import cloud.chenh.bolt.data.parser.GalleryDetailParser;
import cloud.chenh.bolt.data.parser.GalleryImagePaser;
import cloud.chenh.bolt.data.parser.GalleryThumbParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GalleryDetailService {

    @Autowired
    private HttpClientService httpClientService;

    @Autowired
    private GalleryDownloadService galleryDownloadService;

    public GalleryDetail getDetail(String detailUrl) throws IOException {
        GalleryDownload galleryDownload = galleryDownloadService.get(detailUrl);
        if (galleryDownload != null) {
            return galleryDownload.getDetail();
        }

        Map<String, String> cookies = httpClientService.getCookies();
        String domain = cookies.containsKey(GalleryConstants.IMPORTANT_COOKIE)
                ? GalleryConstants.EXHENTAI_DOMAIN : GalleryConstants.EHENTAI_DOMAIN;
        httpClientService.addCookie("nw", "1", domain);

        Map<String, String> params = new HashMap<>();
        params.put("inline_set", "ts_m");
        String html = httpClientService.doGet(detailUrl, params);

        GalleryDetail detail = GalleryDetailParser.parse(html);
        detail.setDetailUrl(detailUrl);

        return detail;
    }

    public List<String> getThumbs(String detailUrl, Integer page) throws IOException {
        Map<String, String> cookies = httpClientService.getCookies();
        boolean isLogin = cookies.containsKey(GalleryConstants.IMPORTANT_COOKIE);

        Map<String, String> params = new HashMap<>();
        params.put("p", String.valueOf(page));
        params.put("inline_set", isLogin ? "ts_l" : "ts_m");
        String http = httpClientService.doGet(detailUrl, params);
        return isLogin ? GalleryThumbParser.parseLarge(http) : GalleryThumbParser.parse(http);
    }

    public SimplePage<String> getImagePages(String detailUrl, Integer page) throws IOException {
        GalleryDownload galleryDownload = galleryDownloadService.get(detailUrl);
        if (galleryDownload != null) {
            SimplePage<String> simplePage = new SimplePage<>(
                    0,
                    1,
                    galleryDownload.getImages().length,
                    Arrays.asList(galleryDownload.getImages())
            );
            simplePage.addMeta("download", true);
            return simplePage;
        }

        Map<String, String> params = new HashMap<>();
        params.put("p", String.valueOf(page));
        params.put("inline_set", "ts_m");
        String detailHtml = httpClientService.doGet(detailUrl, params);
        return GalleryDetailParser.parseImagePages(detailHtml);
    }

    public String getImageUrl(String imagePageUrl) throws IOException {
        String pageHtml = httpClientService.doGet(imagePageUrl, new HashMap<>());
        return GalleryImagePaser.parse(pageHtml);
    }

    public void download(String detailUrl) throws IOException {
        GalleryDetail detail = getDetail(detailUrl);
        GalleryDownload galleryDownload = new GalleryDownload(detail);
        galleryDownloadService.add(galleryDownload);
    }

}
