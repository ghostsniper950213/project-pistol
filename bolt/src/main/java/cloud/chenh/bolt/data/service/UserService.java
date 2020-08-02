package cloud.chenh.bolt.data.service;

import cloud.chenh.bolt.constant.GalleryConstants;
import cloud.chenh.bolt.crawl.crawler.CrawlClient;
import cloud.chenh.bolt.crawl.crawler.LoginCrawler;
import cloud.chenh.bolt.exception.LoginFailureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private LoginCrawler loginCrawler;

    @Autowired
    private ConfigService configService;

    @Autowired
    private CrawlClient crawlClient;

    public void login(String username, String password) throws IOException, LoginFailureException {
        loginCrawler.crawl(username, password);
    }

    public void logout() {
        crawlClient.removeCookie();
    }

    public Map<String, String> getCookies() {
        Map<String, String> resultCookies = new HashMap<>();
        Arrays.asList(GalleryConstants.USEFUL_COOKIES).forEach(key -> resultCookies.put(key, configService.findValByKey(key)));
        return resultCookies;
    }

    public void setCookies(Map<String, String> cookies) {
        Arrays.asList(GalleryConstants.USEFUL_COOKIES).forEach(key -> configService.set(key, cookies.get(key)));
    }

}
