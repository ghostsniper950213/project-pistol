package cloud.chenh.bolt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BoltApplication {

    public static void main(String[] args) {
        SpringApplication.run(BoltApplication.class, args);
    }

}
