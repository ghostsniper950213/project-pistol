package cloud.chenh.bolt.crawl.crawler;

import cloud.chenh.bolt.constant.ConfigConstants;
import cloud.chenh.bolt.constant.GalleryConstants;
import cloud.chenh.bolt.data.service.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class CrawlClient {

    private static final Integer CONNECT_OVERTIME = 10000;
    private static final Integer HTML_SOCKET_OVERTIME = 10000;
    private static final Integer IMAGE_SOCKET_OVERTIME = 60000;

    private HttpClient httpClient;

    private final CookieStore cookieStore = new BasicCookieStore();

    @Autowired
    private ConfigService configService;

    @PostConstruct
    public void init() {
        // clear
        cookieStore.clear();

        // load cookie
        String domain = isLogin() ? GalleryConstants.EXHENTAI_DOMAIN : GalleryConstants.EHENTAI_DOMAIN;
        Arrays.stream(GalleryConstants.USEFUL_COOKIES).forEach(k -> {
            String v = configService.findValByKey(k);
            if (StringUtils.isNotBlank(v)) {
                BasicClientCookie cookie = new BasicClientCookie(k, v);
                cookie.setDomain(domain);
                cookieStore.addCookie(cookie);
            }
        });

        // never warn
        BasicClientCookie cookie = new BasicClientCookie("nw", "1");
        cookie.setDomain(domain);
        cookieStore.addCookie(cookie);

        // builder
        HttpClientBuilder builder = HttpClients.custom().setDefaultCookieStore(cookieStore);

        // proxy
        String proxyHost = configService.findValByKey(ConfigConstants.PROXY_HOST_KEY);
        String proxyPort = configService.findValByKey(ConfigConstants.PROXY_PORT_KEY);
        if (StringUtils.isNotBlank(proxyHost) && StringUtils.isNotBlank(proxyPort)) {
            try {
                builder.setProxy(new HttpHost(proxyHost, Integer.parseInt(proxyPort)));
            } catch (NumberFormatException e) {
                log.error("不正确的代理格式：" + proxyHost + ":" + proxyPort);
            }
        }

        // build
        httpClient = builder.build();
    }

    /**
     * clear and remove all user cookie
     */
    public void removeCookie() {
        cookieStore.clear();
        Arrays.asList(GalleryConstants.USEFUL_COOKIES).parallelStream().forEach(configService::removeByKey);
    }

    /**
     * check if is login by local config
     */
    public Boolean isLogin() {
        for (String key : GalleryConstants.USEFUL_COOKIES) {
            if (StringUtils.isBlank(configService.findValByKey(key))) {
                return false;
            }
        }
        return true;
    }

    /**
     * save cookie to local config
     */
    public void saveCookie() {
        cookieStore.getCookies().parallelStream()
                .filter(cookie -> Arrays.asList(GalleryConstants.USEFUL_COOKIES).contains(cookie.getName()))
                .forEach(cookie -> configService.set(cookie.getName(), cookie.getValue()));
    }

    public String doGet(String url, Map<String, String> params) throws IOException {
        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            uriBuilder.addParameters(buildParams(params));

            HttpGet httpGet = new HttpGet(uriBuilder.build());
            httpGet.addHeader("User-Agent", GalleryConstants.USER_AGENT);
            RequestConfig requestConfig = RequestConfig
                    .custom()
                    .setConnectTimeout(CONNECT_OVERTIME)
                    .setSocketTimeout(HTML_SOCKET_OVERTIME)
                    .build();
            httpGet.setConfig(requestConfig);
            HttpResponse response = httpClient.execute(httpGet);
            return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
    }

    public String doPost(String url, Map<String, String> params) throws IOException {
        UrlEncodedFormEntity paramsEntity = new UrlEncodedFormEntity(buildParams(params), Charset.defaultCharset());
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(paramsEntity);
        httpPost.addHeader("User-Agent", GalleryConstants.USER_AGENT);
        RequestConfig requestConfig = RequestConfig
                .custom()
                .setConnectTimeout(CONNECT_OVERTIME)
                .setSocketTimeout(HTML_SOCKET_OVERTIME)
                .build();
        httpPost.setConfig(requestConfig);
        HttpResponse response = httpClient.execute(httpPost);
        return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
    }

    public InputStream doGetImage(String url) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("User-Agent", GalleryConstants.USER_AGENT);
        RequestConfig requestConfig = RequestConfig
                .custom()
                .setConnectTimeout(CONNECT_OVERTIME)
                .setSocketTimeout(IMAGE_SOCKET_OVERTIME)
                .build();
        httpGet.setConfig(requestConfig);
        HttpResponse response = httpClient.execute(httpGet);
        return response.getEntity().getContent();
    }

    private List<NameValuePair> buildParams(Map<String, String> params) {
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        params.forEach((k, v) -> nameValuePairs.add(new BasicNameValuePair(k, v)));
        return nameValuePairs;
    }

}
