package cloud.chenh.bolt.data.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GalleryDetail implements Serializable {

    private String detailUrl;

    private String title;

    private String coverUrl;

    private String uploader;

    private String time;

    private String category;

    private Double rating;

    private String language;

    private Integer pages;

    private Integer favorites;

    private Integer thumbPages;

    private Boolean download;

    private List<GalleryTag> tags;

    private List<GalleryComment> comments;

}
