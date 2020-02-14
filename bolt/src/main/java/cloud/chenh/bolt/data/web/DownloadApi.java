package cloud.chenh.bolt.data.web;

import cloud.chenh.bolt.data.model.OperationResult;
import cloud.chenh.bolt.data.service.GalleryDetailService;
import cloud.chenh.bolt.data.service.GalleryDownloadService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/download")
public class DownloadApi {

    @Autowired
    private GalleryDownloadService galleryDownloadService;

    @Autowired
    private GalleryDetailService galleryDetailService;

    @PostMapping
    public OperationResult download(@RequestParam String detailUrl) {
        try {
            galleryDetailService.download(detailUrl);
            return OperationResult.success();
        } catch (IOException e) {
            e.printStackTrace();
            return OperationResult.fail(e.getMessage());
        }
    }

    @GetMapping
    public OperationResult downloads() {
        return OperationResult.success(galleryDownloadService.getAllSortedReversed());
    }

    @PostMapping("remove")
    public OperationResult remove(@RequestParam String detailUrls) {
        galleryDownloadService.remove(
                Arrays
                        .stream(detailUrls.split(","))
                        .filter(StringUtils::isNotBlank)
                        .map(String::trim)
                        .collect(Collectors.toList())
        );
        return OperationResult.success();
    }

    @PostMapping("resume")
    public OperationResult resume(@RequestParam String detailUrls) {
        galleryDownloadService.resume(
                Arrays
                        .stream(detailUrls.split(","))
                        .filter(StringUtils::isNotBlank)
                        .map(String::trim)
                        .collect(Collectors.toList())
        );
        return OperationResult.success();
    }

    @PostMapping("pause")
    public OperationResult pause(@RequestParam String detailUrls) {
        galleryDownloadService.pause(
                Arrays
                        .stream(detailUrls.split(","))
                        .filter(StringUtils::isNotBlank)
                        .map(String::trim)
                        .collect(Collectors.toList())
        );
        return OperationResult.success();
    }

    @PostMapping("update")
    public OperationResult update(@RequestParam String detailUrls) {
        galleryDetailService.update(
                Arrays
                        .stream(detailUrls.split(","))
                        .filter(StringUtils::isNotBlank)
                        .map(String::trim)
                        .collect(Collectors.toList())
        );
        return OperationResult.success();
    }

}
