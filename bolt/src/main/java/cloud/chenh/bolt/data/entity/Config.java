package cloud.chenh.bolt.data.entity;

import cloud.chenh.bolt.data.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Config extends BaseEntity {

    private String key;

    private String val;

}
