package cloud.chenh.bolt.data.service;

import cloud.chenh.bolt.data.base.BaseEntity;
import cloud.chenh.bolt.data.base.BaseService;
import cloud.chenh.bolt.data.entity.BlockTag;
import cloud.chenh.bolt.data.repository.BlockTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlockTagService extends BaseService<BlockTag> {

    @Autowired
    private BlockTagRepository blockTagRepository;

    @Override
    public BlockTagRepository getRepository() {
        return blockTagRepository;
    }

    public List<BlockTag> findAll() {
        return getRepository().get();
    }

    public BlockTag block(String group, String tag) {
        BlockTag blockTag = getRepository().findByGroupAndTag(group, tag);
        if (blockTag != null) {
            return blockTag;
        }
        blockTag = new BlockTag();
        blockTag.setGroup(group);
        blockTag.setTag(tag);
        return save(blockTag);
    }

    public List<BlockTag> unblock(List<BlockTag> blockTags) {
        List<Integer> removeIds = get().parallelStream()
                .filter(tagGroup -> blockTags
                        .parallelStream()
                        .anyMatch(g -> tagGroup.getGroup().equals(g.getGroup()) && tagGroup.getTag().equals(g.getTag()))
                )
                .map(BaseEntity::getId)
                .collect(Collectors.toList());

        return remove(removeIds);
    }

}
