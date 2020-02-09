package cloud.chenh.bolt.data.web;

import cloud.chenh.bolt.data.model.OperationResult;
import cloud.chenh.bolt.data.service.GalleryUserService;
import cloud.chenh.bolt.data.service.HttpClientService;
import cloud.chenh.bolt.exception.LoginFailureException;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/user")
public class UserApi {

    @Autowired
    private GalleryUserService galleryUserService;

    @Autowired
    private HttpClientService httpClientService;

    @PostMapping("login")
    public OperationResult login(@RequestParam String username, @RequestParam String password) {
        try {
            return OperationResult.success(galleryUserService.login(username, password));
        } catch (LoginFailureException e) {
            return OperationResult.fail(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            return OperationResult.fail("请求失败，请检查网络是否通畅");
        }
    }

    @GetMapping("logout")
    public OperationResult logout() {
        galleryUserService.logout();
        return OperationResult.success();
    }

    @GetMapping("cookie")
    public OperationResult getCookie() {
        return OperationResult.success(httpClientService.getCookies());
    }

    @PostMapping("cookie")
    public OperationResult postCookie(@RequestParam String cookies) {
        JSONObject cookieObj = JSONObject.parseObject(cookies);
        Map<String, String> cookieMap = new HashMap<>();
        cookieObj.keySet().forEach(key -> cookieMap.put(key, cookieObj.getString(key)));
        httpClientService.postCookies(cookieMap);
        return OperationResult.success();
    }

}
