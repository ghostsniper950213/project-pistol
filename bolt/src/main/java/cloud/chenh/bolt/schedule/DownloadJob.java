package cloud.chenh.bolt.schedule;

import cloud.chenh.bolt.data.service.GalleryDownloadService;
import cloud.chenh.bolt.data.ws.DownloadWs;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DownloadJob {

    @Autowired
    private GalleryDownloadService galleryDownloadService;

    @Autowired
    private DownloadWs downloadWs;

    @Scheduled(fixedRate = 1000)
    public void sendWs() {
        downloadWs.sendMessage(JSONObject.toJSONString(galleryDownloadService.getAllSortedReversed()));
    }

}
