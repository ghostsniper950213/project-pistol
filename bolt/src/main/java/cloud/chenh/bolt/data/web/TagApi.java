package cloud.chenh.bolt.data.web;

import cloud.chenh.bolt.data.model.OperationResult;
import cloud.chenh.bolt.data.service.GalleryTagService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/tag")
public class TagApi {

    @Autowired
    private GalleryTagService galleryTagService;


    @GetMapping("blocked")
    public OperationResult blockedTags() {
        return OperationResult.success(galleryTagService.getBlockedTags());
    }

    @PostMapping("block")
    public OperationResult blockTag(@RequestParam String tag) {
        galleryTagService.blockTag(tag);
        return OperationResult.success();
    }

    @PostMapping("unblock")
    public OperationResult unblockTag(@RequestParam String tags) {
        galleryTagService.unblockTags(
                Arrays
                        .stream(tags.split(","))
                        .filter(StringUtils::isNotBlank)
                        .map(String::trim)
                        .collect(Collectors.toList()));
        return OperationResult.success();
    }

}
