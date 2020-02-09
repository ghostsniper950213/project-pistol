package cloud.chenh.bolt.data.model;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class SimplePage<T> implements Serializable {

    public SimplePage(Integer pageNumber, Integer totalPages, List<T> elements) {
        this.pageNumber = pageNumber;
        this.totalPages = totalPages;
        this.elements = elements;
    }

    public SimplePage(Integer pageNumber, Integer totalPages, Integer totalElements, List<T> elements) {
        this.pageNumber = pageNumber;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.elements = elements;
    }

    private Integer pageNumber;

    private Integer totalPages;

    private Integer totalElements;

    private List<T> elements;

    private Map<String, Object> meta = new HashMap<>();

    public void addMeta(String key, Object val) {
        meta.put(key, val);
    }

}
