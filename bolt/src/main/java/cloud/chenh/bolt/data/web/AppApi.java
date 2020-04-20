package cloud.chenh.bolt.data.web;

import cloud.chenh.bolt.data.model.OperationResult;
import cloud.chenh.bolt.data.service.AppService;
import com.alibaba.fastjson.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("api/app")
public class AppApi {

    @Autowired
    private AppService appService;

    @GetMapping("version")
    public OperationResult getVersionInfo() {
        try {
            return OperationResult.success(appService.getVersionInfos());
        } catch (JSONException e) {
            return OperationResult.fail("对 Github 的请求过于频繁，请稍后再试");
        } catch (IOException e) {
            return OperationResult.fail("请求失败，请检查网络是否通畅");
        }
    }

}
