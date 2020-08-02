package cloud.chenh.bolt.crawl.parser;

import cloud.chenh.bolt.data.entity.TagTranslate;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TagTranslateParser {

    public static List<TagTranslate> parse(String response) {
        JSONObject jsonObject = JSONObject.parseObject(response);
        JSONArray jsonArray = jsonObject.getJSONArray("data");

        List<TagTranslate> tagTranslates = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject data = jsonArray.getJSONObject(i);
            String namespace = data.getString("namespace");

            List<TagTranslate.Mapper> mappers = new ArrayList<>();
            JSONObject translateData = data.getJSONObject("data");
            for (String key : translateData.keySet()) {
                JSONObject dataContent = translateData.getJSONObject(key);
                String val = dataContent.getString("name");
                TagTranslate.Mapper mapper = new TagTranslate.Mapper();
                mapper.setRaw(key);
                mapper.setResult(val);
                mappers.add(mapper);
            }

            TagTranslate tagTranslate = new TagTranslate();
            tagTranslate.setGroup(namespace);
            tagTranslate.setMappers(mappers);
            tagTranslates.add(tagTranslate);
        }

        return tagTranslates;
    }

}
