package cloud.chenh.bolt.data.web;

import cloud.chenh.bolt.data.service.GalleryService;
import cloud.chenh.bolt.exception.BannedException;
import cloud.chenh.bolt.model.OperationResult;
import cloud.chenh.bolt.model.SearchParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/gallery")
public class GalleryApi {

    @Autowired
    private GalleryService galleryService;

    @GetMapping("page")
    public OperationResult<?> page(@ModelAttribute SearchParams searchParams) {
        try {
            return OperationResult.ok(galleryService.fetchGalleryList(searchParams));
        } catch (BannedException e) {
            return OperationResult.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return OperationResult.error("请求失败，请检查网络是否通畅");
        }
    }

    @GetMapping("detail")
    public OperationResult<?> detail(@RequestParam String url) {
        try {
            return OperationResult.ok(galleryService.fetchGalleryDetail(url));
        } catch (Exception e) {
            e.printStackTrace();
            return OperationResult.error("请求失败，请检查网络是否通畅");
        }
    }

    @GetMapping("thumbs")
    public OperationResult<?> thumbs(@RequestParam String url, @RequestParam Integer pageNumber) {
        try {
            return OperationResult.ok(galleryService.fetchThumbs(url, pageNumber));
        } catch (Exception e) {
            e.printStackTrace();
            return OperationResult.error("请求失败，请检查网络是否通畅");
        }
    }

    @GetMapping("image/page")
    public OperationResult<?> imagePages(@RequestParam String url, @RequestParam Integer pageNumber) {
        try {
            return OperationResult.ok(galleryService.fetchImagePages(url, pageNumber));
        } catch (Exception e) {
            e.printStackTrace();
            return OperationResult.error("请求失败，请检查网络是否通畅");
        }
    }

//    @GetMapping("image/url")
//    public OperationResult<?> imageUrl(@RequestParam String url) {
//        try {
//            return OperationResult.ok(galleryService.fetchImageUrl(url));
//        } catch (Exception e) {
//            e.printStackTrace();
//            return OperationResult.error("请求失败，请检查网络是否通畅");
//        }
//    }

    @GetMapping("download")
    public OperationResult<?> download() {
        return OperationResult.ok(galleryService.getAllReversed());
    }

    @PostMapping("download")
    public OperationResult<?> download(@RequestParam String url) {
        galleryService.addToDownload(url);
        return OperationResult.ok();
    }

    @PostMapping("pause")
    public OperationResult<?> pause(@RequestParam String... urls) {
        for (String url : urls) {
            galleryService.pause(url);
        }
        return OperationResult.ok();
    }

    @DeleteMapping("delete")
    public OperationResult<?> delete(@RequestParam String... urls) {
        for (String url : urls) {
            galleryService.delete(url);
        }
        return OperationResult.ok();
    }

}
