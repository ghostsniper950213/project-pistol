package cloud.chenh.bolt.data.repository;

import cloud.chenh.bolt.data.base.BaseRepository;
import cloud.chenh.bolt.data.entity.BlockTag;
import org.springframework.stereotype.Repository;

@Repository
public class BlockTagRepository extends BaseRepository<BlockTag> {

    public BlockTag findByGroupAndTag(String group, String tag) {
        return get().parallelStream()
                .filter(blockTag -> blockTag.getGroup().equals(group) && blockTag.getTag().equals(tag))
                .findAny()
                .orElse(null);
    }

}
