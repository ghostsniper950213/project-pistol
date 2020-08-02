package cloud.chenh.bolt.data.entity;

import cloud.chenh.bolt.data.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class TagTranslate extends BaseEntity {

    private String group;

    private List<Mapper> mappers;

    @Getter
    @Setter
    public static class Mapper implements Serializable {

        private String raw;

        private String result;

    }


}
