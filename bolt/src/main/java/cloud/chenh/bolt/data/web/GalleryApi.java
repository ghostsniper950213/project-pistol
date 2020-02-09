package cloud.chenh.bolt.data.web;

import cloud.chenh.bolt.data.model.GalleryOverview;
import cloud.chenh.bolt.data.model.OperationResult;
import cloud.chenh.bolt.data.service.GalleryDetailService;
import cloud.chenh.bolt.data.service.GalleryImageService;
import cloud.chenh.bolt.data.service.GalleryOverviewService;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/gallery")
public class GalleryApi {

    @Autowired
    private GalleryOverviewService galleryOverviewService;

    @Autowired
    private GalleryDetailService galleryDetailService;

    @Autowired
    private GalleryImageService galleryImageService;

    @GetMapping("page")
    public OperationResult page(@RequestParam String searchParams) {
        Map<String, String> searchParamMap = new HashMap<>();
        if (StringUtils.isNoneBlank(searchParams)) {
            JSONObject searchObj = JSONObject.parseObject(searchParams);
            searchObj.keySet().forEach(key -> searchParamMap.put(key, searchObj.getString(key)));
        }
        try {
            return OperationResult.success(galleryOverviewService.getPage(searchParamMap));
        } catch (IOException e) {
            e.printStackTrace();
            return OperationResult.fail("请求失败，请检查网络是否通畅");
        }
    }

    @GetMapping("detail")
    public OperationResult detail(@RequestParam String detailUrl) {
        try {
            return OperationResult.success(galleryDetailService.getDetail(detailUrl));
        } catch (IOException e) {
            e.printStackTrace();
            return OperationResult.fail("请求失败，请检查网络是否通畅");
        }
    }

    @GetMapping("thumbs")
    public OperationResult thumbs(@RequestParam String detailUrl, @RequestParam Integer page) {
        try {
            return OperationResult.success(galleryDetailService.getThumbs(detailUrl, page));
        } catch (IOException e) {
            e.printStackTrace();
            return OperationResult.fail("请求失败，请检查网络是否通畅");
        }
    }

    @GetMapping("image/page")
    public OperationResult imagePages(@RequestParam String detailUrl, @RequestParam Integer page) {
        try {
            return OperationResult.success(galleryDetailService.getImagePages(detailUrl, page));
        } catch (IOException e) {
            e.printStackTrace();
            return OperationResult.fail("请求失败，请检查网络是否通畅");
        }
    }

    @GetMapping("image/view")
    public void viewImage(@RequestParam String url, HttpServletResponse response) {
        try {
            galleryImageService.writeImage(galleryDetailService.getImageUrl(url), response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
