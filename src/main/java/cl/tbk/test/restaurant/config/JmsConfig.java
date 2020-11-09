package cl.tbk.test.restaurant.config;

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
 * Configuración JMS
 * @author manuelpinto
 */
@Configuration
@EnableJms
public class JmsConfig {


    @Value("${spring.activemq.broker-url}")
    private String brokerUrl;
    
    private static BrokerService brokerService;
    
    /**
     * Levanta el activemq
     * @return 
     */
    @Bean
    public BrokerService brokerService(){
        try {
            synchronized(JmsConfig.class){ // por is a caso
                if(brokerService==null){
                    brokerService=new BrokerService();
                    brokerService.addConnector(brokerUrl);
                    brokerService.start();
                }
            }
            return brokerService;
        } catch (Exception ex) {
            Logger.getLogger(JmsConfig.class.getName()).log(Level.SEVERE, null, ex);
        }   
        return null;
    }
    
    /**
     * connection factory
     * @return 
     */
    @Bean
    public ConnectionFactory connectionFactory(){
        brokerService();
        ActiveMQConnectionFactory activeMQConnectionFactory  = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setBrokerURL(brokerUrl);
        // Esto lo hago por hacerlo rápido, pero en prod tienen que poner cada uno de los que podrían ser serializados
        //activeMQConnectionFactory.setTrustedPackages(Arrays.asList("cl.tbk.test.restaurant","java.util", "java.lang", "java.math", ... ));
        activeMQConnectionFactory.setTrustAllPackages(true);
        return activeMQConnectionFactory;
    }

    /**
     * jmsTemplate simple para envio de mensajes JMS
     * @return 
     */
    @Bean
    public JmsTemplate jmsTemplate(){
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(connectionFactory());
        jmsTemplate.setPubSubDomain(true);  // enable for Pub Sub to topic. Not Required for Queue.
        return jmsTemplate;
    }

    /**
     * jmsMessagingTemplate para envio de mensajes y recepción de respuesta (este es el que vamos a usar)
     * @return 
     */
    @Bean
    public JmsMessagingTemplate jmsMessagingTemplate(){
        JmsMessagingTemplate jmt=new JmsMessagingTemplate(jmsTemplate());
        return jmt;
    }
    
    /**
     * Activa el listener de mensajes
     * @return 
     */
    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(){
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setPubSubDomain(true);
        return factory;
    }    
}
