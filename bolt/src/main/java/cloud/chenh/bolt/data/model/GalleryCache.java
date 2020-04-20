package cloud.chenh.bolt.data.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class GalleryCache implements Serializable {

    private String url;

    private String path;

    public GalleryCache() {
    }

    public GalleryCache(String url, String path) {
        this.url = url;
        this.path = path;
    }

}
