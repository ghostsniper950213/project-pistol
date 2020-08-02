package cloud.chenh.bolt.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class SearchParams implements Serializable {

    /**
     * page
     */
    private Integer page = 0;

    /**
     * category
     */
    private Integer f_cats = 0;

    /**S
     * search value
     */
    private String f_search = "";

    /**
     * min pages
     */
    private Integer f_spf;

    /**
     * max pages
     */
    private Integer f_spt;

    /**
     * min rate
     */
    private Integer f_srdd;

    /**
     * advance search
     */
    private Integer advsearch = 1;
    private String f_sdesc = "on";
    private String f_sname = "on";
    private String f_sp = "on";
    private String f_sr = "on";
    private String f_stags = "on";

    // for home page showing more info
    private String inline_set = "dm_e";

    public Map<String, String> toHttpParams() {
        Map<String, String> params = new HashMap<>();
        Method[] methods = this.getClass().getMethods();
        try {
            for (Method method : methods) {
                if (method.getName().startsWith("get") && !method.getName().equals("getClass")) {
                    Object value = method.invoke(this);
                    if (value != null) {
                        String key = StringUtils.uncapitalize(method.getName().substring(3));
                        params.put(key, value.toString());
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return params;
    }

}
