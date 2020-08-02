package cloud.chenh.bolt.constant;

import org.springframework.boot.system.ApplicationHome;

public class ProjectConstants {

    public static final String JAR_DIR = new ApplicationHome(ProjectConstants.class)
            .getSource().getParentFile().getAbsolutePath();

}
