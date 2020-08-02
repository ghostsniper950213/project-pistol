package cloud.chenh.bolt.data.service;

import cloud.chenh.bolt.constant.GalleryConstants;
import cloud.chenh.bolt.crawl.crawler.TagTranslateCrawler;
import cloud.chenh.bolt.data.base.BaseEntity;
import cloud.chenh.bolt.data.base.BaseService;
import cloud.chenh.bolt.data.entity.TagTranslate;
import cloud.chenh.bolt.data.repository.TagTranslateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagTranslateService extends BaseService<TagTranslate> {

    @Autowired
    private TagTranslateCrawler tagTranslateCrawler;

    @Autowired
    private TagTranslateRepository tagTranslateRepository;

    @Override
    public TagTranslateRepository getRepository() {
        return tagTranslateRepository;
    }

    public String translateGroup(String group) {
        return translateTag(GalleryConstants.GROUP_NAMESPACE, group);
    }

    public String translateTag(String group, String tag) {
        TagTranslate tagTranslate = get().parallelStream().filter(tt -> group.equals(tt.getGroup())).findAny().orElse(null);
        if (tagTranslate == null) {
            return tag;
        }
        List<TagTranslate.Mapper> mappers = tagTranslate.getMappers();
        TagTranslate.Mapper mapper = mappers.parallelStream().filter(m -> tag.equals(m.getRaw())).findAny().orElse(null);
        if (mapper == null) {
            return tag;
        }
        return mapper.getResult();
    }

    @PostConstruct
    public void fetchAndSave() {
        try {
            List<TagTranslate> tagTranslates = tagTranslateCrawler.crawl();
            remove(get().stream().map(BaseEntity::getId).collect(Collectors.toList()));
            getRepository().save(tagTranslates);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
