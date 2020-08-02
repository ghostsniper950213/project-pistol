package cloud.chenh.bolt.crawl.crawler;

import cloud.chenh.bolt.constant.GalleryConstants;
import cloud.chenh.bolt.exception.LoginFailureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class LoginCrawler {

    @Autowired
    private CrawlClient crawlClient;

    public void crawl(String username, String password) throws IOException, LoginFailureException {
        // clear
        crawlClient.removeCookie();

        // params
        Map<String, String> params = new HashMap<>();
        params.put("UserName", username);
        params.put("PassWord", password);
        params.put("CookieDate", GalleryConstants.DEFAULT_COOKIE_DATE);

        // login
        if (!crawlClient.doPost(GalleryConstants.LOGIN_URL, params).contains(GalleryConstants.LOGIN_SUCCESS_FLAG)) {
            crawlClient.removeCookie();
            throw new LoginFailureException("登录失败，请重试或直接输入Cookie");
        }

        // load important cookie: igneous
        crawlClient.doGet(GalleryConstants.EXHENTAI_HOME_URL, new HashMap<>());

        // save to local
        crawlClient.saveCookie();

        // is all cookie loaded?
        if (!crawlClient.isLogin()) {
            crawlClient.removeCookie();
            throw new LoginFailureException("登录失败，请重试或直接输入Cookie");
        }

        // reload http client
        crawlClient.init();
    }

}
