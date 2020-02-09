package cloud.chenh.bolt.runner;

import cloud.chenh.bolt.data.service.GalleryDownloadService;
import cloud.chenh.bolt.data.service.HttpClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class InitRunner implements ApplicationRunner {

    @Autowired
    private GalleryDownloadService galleryDownloadService;

    @Autowired
    private HttpClientService httpClientService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        httpClientService.init();
        galleryDownloadService.downloadNext();
    }

}
