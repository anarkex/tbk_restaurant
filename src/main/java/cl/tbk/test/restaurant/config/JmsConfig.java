package cl.tbk.test.restaurant.config;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.ConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.spring.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;

/**
 *
 * @author manuelpinto
 */
@Configuration
@EnableJms
public class JmsConfig {


    @Value("${spring.activemq.broker-url}")
    private String brokerUrl;
    
    @Bean
    public BrokerService brokerService(){
        try {
            BrokerService brokerService=new BrokerService();
            brokerService.addConnector(brokerUrl);
            brokerService.start();
            return brokerService;
        } catch (Exception ex) {
            Logger.getLogger(JmsConfig.class.getName()).log(Level.SEVERE, null, ex);
        }   
        return null;
    }
    
    @Bean
    public ConnectionFactory connectionFactory(){
        brokerService();
        ActiveMQConnectionFactory activeMQConnectionFactory  = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setBrokerURL(brokerUrl);
        // Esto lo hago por hacerlo rápido, pero en prod tienen que poner cada uno de los que podrían ser serializados
        //activeMQConnectionFactory.setTrustedPackages(Arrays.asList("cl.tbk.test.restaurant","java.util", "java.lang", "java.math"));
        activeMQConnectionFactory.setTrustAllPackages(true);
        return activeMQConnectionFactory;
    }

    @Bean
    public JmsTemplate jmsTemplate(){
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(connectionFactory());
        jmsTemplate.setPubSubDomain(true);  // enable for Pub Sub to topic. Not Required for Queue.
        return jmsTemplate;
    }

    @Bean
    public JmsMessagingTemplate jmsMessagingTemplate(){
        JmsMessagingTemplate jmt=new JmsMessagingTemplate(jmsTemplate());
        return jmt;
    }
    
    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(){
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setPubSubDomain(true);
        return factory;
    }    
}
