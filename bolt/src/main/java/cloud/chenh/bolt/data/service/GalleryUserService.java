package cloud.chenh.bolt.data.service;

import cloud.chenh.bolt.constant.GalleryConstants;
import cloud.chenh.bolt.exception.LoginFailureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class GalleryUserService {

    @Autowired
    private HttpClientService httpClientService;

    @Autowired
    private ConfigService configService;

    public Map<String, String> login(String username, String password) throws IOException {
        httpClientService.clear();

        Map<String, String> params = new HashMap<>();
        params.put("UserName", username);
        params.put("PassWord", password);
        params.put("CookieDate", GalleryConstants.DEFAULT_COOKIE_DATE);

        if (
                !httpClientService
                        .doPost(GalleryConstants.LOGIN_URL, params)
                        .contains(GalleryConstants.LOGIN_SUCCESS_FLAG)
        ) {
            httpClientService.clear();
            throw new LoginFailureException("登录失败，请重试或直接输入Cookie");
        }

        httpClientService.doGet(GalleryConstants.EXHENTAI_HOME_URL, new HashMap<>());

        Map<String, String> cookies = httpClientService.getCookies();
        if (!cookies.containsKey(GalleryConstants.IMPORTANT_COOKIE)) {
            httpClientService.clear();
            throw new LoginFailureException("登录失败，请重试或直接输入Cookie");
        }

        Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, String> cookie : cookies.entrySet()) {
            String name = cookie.getKey();
            String value = cookie.getValue();

            if (Arrays.asList(GalleryConstants.USEFUL_COOKIES).contains(name)) {
                result.put(name, value);
            }
        }
        result.forEach(configService::set);

        return result;
    }

    public void logout() {
        httpClientService.clear();
    }

}
