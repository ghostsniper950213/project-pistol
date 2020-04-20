package cloud.chenh.bolt.data.service;

import cloud.chenh.bolt.constant.AppConstants;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class AppService {

    @Value("${app.version}")
    private String appVersion;

    @Autowired
    private HttpClientService httpClientService;

    public Map<String, String> getVersionInfos() throws IOException, JSONException {
        String apiResponse = httpClientService.doGet(AppConstants.GITHUB_RELEASES_API_URL, new HashMap<>());
        JSONObject apiObj = JSONObject.parseArray(apiResponse).getJSONObject(0);
        String name = apiObj.getString("name");
        String url = apiObj.getString("html_url");

        Map<String, String> result = new HashMap<>();
        result.put("latest", name);
        result.put("url", url);
        result.put("current", appVersion);

        return result;
    }

}
