package cloud.chenh.bolt.data.web;

import cloud.chenh.bolt.data.entity.BlockTag;
import cloud.chenh.bolt.model.OperationResult;
import cloud.chenh.bolt.data.service.BlockTagService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/tag")
public class TagApi {

    @Autowired
    private BlockTagService blockTagService;

    @GetMapping("block")
    public OperationResult<?> getBlockedTag() {
        return OperationResult.ok(blockTagService.get());
    }

    @PostMapping("block")
    public OperationResult<?> blockTag(@RequestParam String group, @RequestParam String tag) {
        return OperationResult.ok(blockTagService.block(group, tag));
    }

    @PostMapping("unblock")
    public OperationResult<?> unblockTag(@RequestParam String blockedTags) {
        JSONArray tagArray = JSONObject.parseArray(blockedTags);
        List<BlockTag> tags = new ArrayList<>();
        for (int i = 0; i < tagArray.size(); i++) {
            JSONObject tagObj = tagArray.getJSONObject(i);
            BlockTag blockTag = new BlockTag();
            blockTag.setGroup(tagObj.getString("group"));
            blockTag.setTag(tagObj.getString("tag"));
            tags.add(blockTag);
        }
        return OperationResult.ok(blockTagService.unblock(tags));
    }

}
