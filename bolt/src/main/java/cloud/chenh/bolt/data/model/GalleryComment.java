package cloud.chenh.bolt.data.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class GalleryComment implements Serializable {

    private String postTime;

    private String author;

    private String vote;

    private String content;

    private Boolean isUploader;

}
