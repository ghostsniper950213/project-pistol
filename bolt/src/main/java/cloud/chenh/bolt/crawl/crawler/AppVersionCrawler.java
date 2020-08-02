package cloud.chenh.bolt.crawl.crawler;

import cloud.chenh.bolt.constant.AppConstants;
import cloud.chenh.bolt.crawl.parser.AppVersionParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class AppVersionCrawler {

    @Autowired
    private CrawlClient crawlClient;

    @Value("${app.version}")
    private String appVersion;

    public Map<String, String> crawl() throws IOException {
        String response = crawlClient.doGet(AppConstants.GITHUB_RELEASES_API_URL, new HashMap<>());
        Map<String, String> result = AppVersionParser.parse(response);
        result.put("current", appVersion);
        return result;
    }

}
