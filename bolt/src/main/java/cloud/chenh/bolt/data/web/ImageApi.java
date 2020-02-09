package cloud.chenh.bolt.data.web;

import cloud.chenh.bolt.data.service.GalleryImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
        try {
            galleryImageService.writeLocalImage(path, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
