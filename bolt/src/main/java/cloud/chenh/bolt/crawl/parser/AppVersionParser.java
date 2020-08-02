package cloud.chenh.bolt.crawl.parser;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AppVersionParser {

    public static Map<String, String> parse(String response) {
        JSONObject apiObj = JSONObject.parseArray(response).getJSONObject(0);
        String name = apiObj.getString("name");
        String url = apiObj.getString("html_url");

        Map<String, String> result = new HashMap<>();
        result.put("latest", name);
        result.put("url", url);

        return result;
    }

}
