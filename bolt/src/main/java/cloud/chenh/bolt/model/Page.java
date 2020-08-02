package cloud.chenh.bolt.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Page<T> implements Serializable {

    public Page(Integer pageNumber, Integer totalPages, List<T> elements) {
        this.pageNumber = pageNumber;
        this.totalPages = totalPages;
        this.elements = elements;
    }

    private Integer pageNumber;

    private Integer totalPages;

    private List<T> elements;

    private Map<String, Object> meta = new HashMap<>();

    public void addMeta(String key, Object val) {
        meta.put(key, val);
    }

}
