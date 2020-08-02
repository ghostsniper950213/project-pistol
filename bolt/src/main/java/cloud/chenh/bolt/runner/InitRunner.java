package cloud.chenh.bolt.runner;

import cloud.chenh.bolt.constant.ConfigConstants;
import cloud.chenh.bolt.data.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class InitRunner implements ApplicationRunner {

    @Autowired
    private ConfigService configService;

    @Override
    public void run(ApplicationArguments args) {
        configService.set(ConfigConstants.PROXY_HOST_KEY, "127.0.0.1");
        configService.set(ConfigConstants.PROXY_PORT_KEY, "1088");
    }

}
