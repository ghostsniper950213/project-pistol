package cloud.chenh.bolt.data.service;

import cloud.chenh.bolt.crawl.crawler.AppVersionCrawler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class AppService {

    @Autowired
    private AppVersionCrawler appVersionCrawler;

    public Map<String, String> getVersionInfos() throws IOException {
        return appVersionCrawler.crawl();
    }

}
