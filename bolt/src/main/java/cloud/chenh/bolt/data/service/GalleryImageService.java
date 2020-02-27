package cloud.chenh.bolt.data.service;

import cloud.chenh.bolt.constant.GalleryConstants;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

@Service
public class GalleryImageService {

    @Autowired
    private HttpClientService httpClientService;

    public void writeImage(String url, HttpServletResponse response) {
        for (int i = 0; i < GalleryConstants.DEFAULT_RETRY; i++) {
            try {
                InputStream inputStream = httpClientService.doGetStream(url, new HashMap<>());
                OutputStream outputStream = response.getOutputStream();
                StreamUtils.copy(inputStream, outputStream);
                outputStream.flush();
                outputStream.close();
                break;
            } catch (IOException ignored) {
            }
        }
    }

    public void writeLocalImage(String localPath, HttpServletResponse response) {
        try {
            File file = new File(localPath);
            if (!file.exists()) {
                return;
            }
            OutputStream outputStream = response.getOutputStream();
            outputStream.write(FileUtils.readFileToByteArray(file));
            outputStream.flush();
            outputStream.close();
        } catch (IOException ignored) {
        }
    }

}
