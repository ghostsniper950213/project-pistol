package cloud.chenh.bolt.data.dao;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class GalleryBlockTagDao {

    private static final String RECORD_LOCALE = "./data/blocked_tags.json";

    private List<String> read() {
        try {
            File file = new File(RECORD_LOCALE);
            if (!file.exists()) {
                return new ArrayList<>();
            }
            return JSONObject.parseArray(FileUtils.readFileToString(file, Charset.defaultCharset()), String.class);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void write(List<String> tags) {
        try {
            FileUtils.writeStringToFile(
                    new File(RECORD_LOCALE),
                    JSONObject.toJSONString(tags),
                    Charset.defaultCharset()
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> get() {
        return read();
    }

    public void remove(String tag) {
        List<String> allTags = read();
        allTags.remove(tag);
        write(allTags);
    }

    public void remove(Collection<String> tags) {
        List<String> allTags = read();
        allTags.removeAll(tags);
        write(allTags);
    }

    public void add(String tag) {
        List<String> allTags = read();
        if (allTags.contains(tag)) {
            return;
        }
        allTags.add(tag);
        write(allTags);
    }

}
