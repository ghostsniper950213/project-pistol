package cloud.chenh.bolt.data.entity;

import cloud.chenh.bolt.data.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Image extends BaseEntity {

    private String url;

    private String path;

}
