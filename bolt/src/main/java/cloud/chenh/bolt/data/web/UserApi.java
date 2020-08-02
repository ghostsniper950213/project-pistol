package cloud.chenh.bolt.data.web;

import cloud.chenh.bolt.data.service.UserService;
import cloud.chenh.bolt.exception.LoginFailureException;
import cloud.chenh.bolt.model.OperationResult;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/user")
public class UserApi {

    @Autowired
    private UserService userService;

    @PostMapping("login")
    public OperationResult<?> login(@RequestParam String username, @RequestParam String password) {
        try {
            userService.login(username, password);
            return OperationResult.ok(userService.getCookies());
        } catch (LoginFailureException e) {
            return OperationResult.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return OperationResult.error("请求失败，请检查网络是否通畅");
        }
    }

    @GetMapping("logout")
    public OperationResult<?> logout() {
        userService.logout();
        return OperationResult.ok();
    }

    @GetMapping("cookies")
    public OperationResult<?> getCookie() {
        return OperationResult.ok(userService.getCookies());
    }

    @PostMapping("cookie")
    public OperationResult<?> postCookie(@RequestParam String cookies) {
        JSONObject cookieObj = JSONObject.parseObject(cookies);
        Map<String, String> cookieMap = new HashMap<>();
        cookieObj.keySet().forEach(key -> cookieMap.put(key, cookieObj.getString(key)));
        userService.setCookies(cookieMap);
        return OperationResult.ok();
    }

}
