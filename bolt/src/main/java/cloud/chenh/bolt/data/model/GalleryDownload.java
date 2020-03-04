package cloud.chenh.bolt.data.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class GalleryDownload implements Serializable {

    private GalleryDetail detail;

    private Long timestamp = new Date().getTime();

    private String cover;

    private String[] images;

    private StatusEnum status = StatusEnum.DOWNLOADING;

    public enum StatusEnum {

        DOWNLOADING,
        FINISHED,
        FAILED,
        PAUSED

    }

    public GalleryDownload() {
    }

    public GalleryDownload(GalleryDetail detail) {
        this.detail = detail;
        this.images = new String[detail.getPages()];
    }

}
