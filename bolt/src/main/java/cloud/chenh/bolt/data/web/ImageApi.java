package cloud.chenh.bolt.data.web;

import cloud.chenh.bolt.data.service.GalleryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.awt.image.BufferedImage;

@RestController
@RequestMapping("api/image")
public class ImageApi {

    @Autowired
    private GalleryService galleryService;

    @GetMapping(produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_PNG_VALUE})
    public BufferedImage image(
            @RequestParam String galleryUrl,
            @RequestParam(required = false) Integer index,
            @RequestParam(required = false) String imagePage
    ) {
        return galleryService.fetchImage(galleryUrl, index, imagePage);
    }

    @GetMapping(value = "thumb", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_PNG_VALUE})
    public BufferedImage thumb(
            @RequestParam String galleryUrl,
            @RequestParam String thumbUrl
    ) {
        return galleryService.fetchThumb(galleryUrl, thumbUrl);
    }

    @GetMapping(value = "cover", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_PNG_VALUE})
    public BufferedImage cover(@RequestParam String galleryUrl, @RequestParam String coverUrl) {
        return galleryService.fetchCover(galleryUrl, coverUrl);
    }

}
