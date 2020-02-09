package cloud.chenh.bolt.data.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GalleryTag implements Serializable {

    private String name;

    private List<String> vals;

}
