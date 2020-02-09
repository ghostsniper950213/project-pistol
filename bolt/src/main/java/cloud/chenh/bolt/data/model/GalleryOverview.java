package cloud.chenh.bolt.data.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GalleryOverview implements Serializable {

    private String detailUrl;

    private String title;

    private String coverUrl;

    private String uploader;

    private String time;

    private String category;

    private String rating;

    private Integer pages;

    private List<String> tags;

    private Boolean download;

}
