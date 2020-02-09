package cloud.chenh.bolt.data.service;

import cloud.chenh.bolt.constant.ConfigConstants;
import cloud.chenh.bolt.constant.GalleryConstants;
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
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.*;

@Service
public class HttpClientService {

    private static final Integer DEFAUL_OVERTIME = 6000;
    private static final Integer STREAM_OVERTIME = 120000;

    private HttpClient httpClient;

    private CookieStore cookieStore = new BasicCookieStore();

    @Autowired
    private ConfigService configService;

    public void init() {
        loadCookies();

        HttpClientBuilder builder = HttpClients.custom();

        builder.setDefaultCookieStore(cookieStore);

        String proxyHost = configService.get(ConfigConstants.PROXY_HOST_KEY);
        String proxyPort = configService.get(ConfigConstants.PROXY_PORT_KEY);
        if (StringUtils.isNotBlank(proxyHost) && StringUtils.isNotBlank(proxyHost)) {
            builder.setProxy(new HttpHost(proxyHost, Integer.parseInt(proxyPort)));
        }

        httpClient = builder.build();
    }

    public void loadCookies() {
        Arrays.asList(GalleryConstants.USEFUL_COOKIES).forEach(k -> {
            String v = configService.get(k);
            if (StringUtils.isNotBlank(v)) {
                addCookie(k, v, GalleryConstants.EXHENTAI_DOMAIN);
            }
        });
    }

    public void postCookies(Map<String, String> cookies) {
        clear();
        Arrays.asList(GalleryConstants.USEFUL_COOKIES).forEach(k -> {
            String v = cookies.get(k);
            if (StringUtils.isNotBlank(v)) {
                addCookie(k, v, GalleryConstants.EXHENTAI_DOMAIN);
                configService.set(k, v);
            }
        });
    }

    public Map<String, String> getCookies() {
        Map<String, String> cookies = new HashMap<>();
        cookieStore.getCookies().forEach(cookie -> cookies.put(cookie.getName(), cookie.getValue()));
        return cookies;
    }

    public synchronized String doGet(String url, Map<String, String> params) throws IOException {
        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            uriBuilder.addParameters(buildParams(params));
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            httpGet.addHeader("User-Agent", GalleryConstants.USER_AGENT);
            RequestConfig requestConfig = RequestConfig
                    .custom()
                    .setSocketTimeout(DEFAUL_OVERTIME)
                    .setConnectTimeout(DEFAUL_OVERTIME)
                    .build();
            httpGet.setConfig(requestConfig);
            HttpResponse response = httpClient.execute(httpGet);
            return EntityUtils.toString(response.getEntity());
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
    }

    public InputStream doGetStream(String url, Map<String, String> params) throws IOException {
        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            uriBuilder.addParameters(buildParams(params));
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            httpGet.addHeader("User-Agent", GalleryConstants.USER_AGENT);
            RequestConfig requestConfig = RequestConfig
                    .custom()
                    .setSocketTimeout(STREAM_OVERTIME)
                    .setConnectTimeout(STREAM_OVERTIME)
                    .build();
            httpGet.setConfig(requestConfig);
            HttpResponse response = httpClient.execute(httpGet);
            return response.getEntity().getContent();
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
    }

    public synchronized String doPost(String url, Map<String, String> params) throws IOException {
        UrlEncodedFormEntity paramsEntity = new UrlEncodedFormEntity(buildParams(params), Charset.defaultCharset());
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(paramsEntity);
        httpPost.addHeader("User-Agent", GalleryConstants.USER_AGENT);
        RequestConfig requestConfig = RequestConfig
                .custom()
                .setSocketTimeout(DEFAUL_OVERTIME)
                .setConnectTimeout(DEFAUL_OVERTIME)
                .build();
        httpPost.setConfig(requestConfig);
        HttpResponse response = httpClient.execute(httpPost);
        return EntityUtils.toString(response.getEntity());
    }

    public List<NameValuePair> buildParams(Map<String, String> params) {
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        params.forEach((k, v) -> nameValuePairs.add(new BasicNameValuePair(k, v)));
        return nameValuePairs;
    }

    public void addCookie(String key, String value, String domain) {
        BasicClientCookie cookie = new BasicClientCookie(key, value);
        cookie.setDomain(domain);
        cookieStore.addCookie(cookie);
    }

    public void clear() {
        cookieStore.clear();
        Arrays.asList(GalleryConstants.USEFUL_COOKIES).forEach(configService::remove);
    }

}
