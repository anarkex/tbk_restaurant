package cl.tbk.test.restaurant.config;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * WebServer configuration, only define the Tomcat Port (application.properties)
 * @author manuelpinto
 */
@Component
public class TomcatConfig implements
        WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    private final Integer port;
    
    public TomcatConfig(
            @Value("${tc.port}") Integer port
    ) {
        this.port = port;
    }
    
    /**
     * Levanta el tomcat que manejará los endpoint rest
     * @return 
     */
    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        return tomcat;
    }

    /**
     * Fija los parametros de contextPath y puerto del tomcat
     * @param factory 
     */
    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        factory.setContextPath("");
        factory.setPort(port);
    }

}
