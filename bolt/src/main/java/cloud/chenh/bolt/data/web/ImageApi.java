package cloud.chenh.bolt.data.web;

import cloud.chenh.bolt.data.model.OperationResult;
import cloud.chenh.bolt.data.service.GalleryImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("api/image")
public class ImageApi {

    @Autowired
    private GalleryImageService galleryImageService;

    @GetMapping
    public void image(@RequestParam String url, HttpServletResponse response) {
        galleryImageService.writeImage(url, response);
    }

    @GetMapping("local")
    public void localImage(@RequestParam String path, HttpServletResponse response) {
        galleryImageService.writeLocalImage(path, response);
    }

    @GetMapping("cache/clear")
    public OperationResult clearCache() {
        galleryImageService.clearCache();
        return OperationResult.success("清理成功");
    }

    @GetMapping("cache/size")
    public OperationResult cacheSize() {
        return OperationResult.success(galleryImageService.getCacheSize());
    }

}
