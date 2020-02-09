package cloud.chenh.bolt.data.service;

import cloud.chenh.bolt.data.dao.GalleryBlockTagDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class GalleryTagService {

    @Autowired
    private GalleryBlockTagDao galleryBlockTagDao;

    public List<String> getBlockedTags() {
        return galleryBlockTagDao.get();
    }

    public void blockTag(String tag) {
        galleryBlockTagDao.add(tag);
    }

    public void unblockTags(Collection<String> tags) {
        galleryBlockTagDao.remove(tags);
    }

}
