package cloud.chenh.bolt.data.entity;

import cloud.chenh.bolt.data.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class Gallery extends BaseEntity {

    private Status status;

    private String url;

    private String title;

    private String coverUrl;

    private String category;

    private String uploader;

    private String time;

    private String language;

    private Integer pages;

    private Integer favorites;

    private Double rating;

    private String[] imageUrls;

    private List<Comment> comments;

    private List<TagGroup> tagGroups;

    private Boolean isDownload;

    @Getter
    @Setter
    public static class Comment implements Serializable {

        private String author;

        private String time;

        private String vote;

        private String content;

        private Boolean isUploader;
    }

    @Getter
    @Setter
    public static class TagGroup implements Serializable {

        private String group;

        private String translatedGroup;

        private List<String> tags;

        private List<String> translatedTags;
    }

    public enum Status {

        WAITING,
        DOWNLOADING,
        FAILED,
        PAUSED,
        DOWNLOADED;

    }

}
