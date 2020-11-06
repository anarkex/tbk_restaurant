package cl.tbk.test.restaurant;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Entry point of the whole test app
 * @author manuelpinto
 */
@SpringBootApplication
@EnableAutoConfiguration(exclude = {org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration.class})
@EnableConfigurationProperties
@EnableEncryptableProperties
@EnableScheduling
public class App {
    // This should have been an enum
    public static final String M_PARAMETERS     = "parameters";
    public static final String P_SECRET_KEY     = "secretkey";
    public static final String M_LOGIN_ATTEMPTS = "loginAttempts";
    public static final String M_VENTAS         = "ventas";
    public static final String Q_VENTAS_STORAGE = "saveventas";
    
    public static final String M_RESUMERESULT   = "resumeresult";
    public static final String M_RESUMEQUERY    = "resumequery";
    
    public static void main(String[] args) {
        SpringApplication.run(cl.tbk.test.restaurant.App.class, args);
    }
    
}
